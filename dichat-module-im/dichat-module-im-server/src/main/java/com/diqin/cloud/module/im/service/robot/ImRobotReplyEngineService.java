package com.diqin.cloud.module.im.service.robot;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotReplyRuleDO;
import com.diqin.cloud.module.im.dal.mysql.robot.ImRobotReplyRuleMapper;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceManagerService;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import com.diqin.cloud.module.im.service.message.dto.ImPrivateMessageSendDTO;
import com.diqin.cloud.module.im.service.websocket.ImWebSocketService;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * IM 机器人自动回复引擎
 *
 * <p>在私聊消息落库后由 {@code ImPrivateMessageServiceImpl} 调用：
 * 当接收方是「启用 + 开启自动回复」的机器人时，按匹配方式（精确 / 包含 / 正则）命中首条规则，
 * 以机器人身份自动回一条文本消息。回复以机器人自身 id（im_robot.id）+ ROBOT 类型为发送方、原发送方为接收方，
 * 由于引擎仅在「接收方是机器人」时触发，回复消息不会再次递归。
 *
 * <p>另支持可选 webhook：若机器人配置了回调地址，将异步 POST 入站事件（失败不影响主流程）。
 *
 * @author 速构构
 */
@Service
@Slf4j
public class ImRobotReplyEngineService {

    @Resource
    private ImRobotManagerService robotManagerService;
    @Resource
    private ImRobotReplyRuleMapper replyRuleMapper;
    /**
     * 延迟注入，打破与 {@code ImPrivateMessageServiceImpl} 的循环依赖。
     * 该 Service 因含 {@code @Transactional} 会被事务代理包装：若直接注入其早期原始引用，
     * 最终代理生成后 Spring 会报 BeanCurrentlyInCreationException（raw vs final 不一致）。
     * 用 @Lazy 改为持有一个延迟代理，调用时才解析到最终被包装的 bean，事务正常生效。
     */
    @Lazy
    @Resource
    private ImPrivateMessageService privateMessageService;
    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;
    @Resource
    private ImWebSocketService imWebSocketService;
    @Resource
    private ImProperties imProperties;

    /**
     * 回复规则动作类型：2-转人工（对应 {@code im_robot_reply_rule.reply_type}；1 为文本回复）
     */
    private static final Integer REPLY_TYPE_TRANSFER = 2;

    /**
     * 处理用户发给机器人的私聊消息。
     *
     * @param inbound 已落库的用户私聊消息（其 receiverId 为机器人自身 id——im_robot.id）
     */
    public void handleInbound(ImPrivateMessageDO inbound) {
        // 方案 C：接收方为机器人自身 id（im_robot.id），直接按 id 定位
        ImRobotDO robot = robotManagerService.getRobotById(inbound.getReceiverId());
        if (robot == null) {
            return;
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(robot.getStatus())) {
            return;
        }
        if (!Boolean.TRUE.equals(robot.getAutoReplyEnabled())) {
            return;
        }
        // 1. 可选 webhook 事件回调（异步、尽力而为）
        if (StrUtil.isNotBlank(robot.getWebhookUrl())) {
            postWebhookAsync(robot, inbound);
        }
        // 2. 仅文本消息触发关键词匹配回复
        if (!ImMessageTypeEnum.TEXT.getType().equals(inbound.getType())) {
            return;
        }
        String content = inbound.getContent();
        if (StrUtil.isBlank(content)) {
            return;
        }
        List<ImRobotReplyRuleDO> rules = replyRuleMapper.selectListByRobotIdAndStatus(
                robot.getId(), CommonStatusEnum.ENABLE.getStatus());
        for (ImRobotReplyRuleDO rule : rules) {
            if (!matchRule(rule, content)) {
                continue;
            }
            // 命中规则即代表机器人已「看到」用户消息：以机器人自身身份把该来消息置为已读，
            // 并把 READ 回执推给真实在线的用户，否则用户发给机器人的消息 status 永远是 UNREAD，
            // 自己气泡永远没有「已读双勾」。用 try/catch 包裹：已读开关关闭或任何异常都不影响回复主流程。
            try {
                privateMessageService.readPrivateMessages(robot.getId(), inbound.getSenderId(), inbound.getId());
            } catch (Exception e) {
                log.debug("[ImRobotReplyEngine] 标记用户消息已读失败（不影响回复） inboundId={}", inbound.getId(), e);
            }
            // 动作分支：2-转人工 / 1,3-文本回复（3 为常见问题，答案存在 replyContent）
            if (REPLY_TYPE_TRANSFER.equals(rule.getReplyType())) {
                transferToHuman(robot, inbound);
            } else {
                // 方案 C：机器人以自身身份（im_robot.id + ROBOT 类型）回复，不再寄生 im_users
                privateMessageService.sendPrivateMessage(ImMessageParticipantTypeEnum.ROBOT.getType(), robot.getId(),
                        new ImPrivateMessageSendDTO()
                                .setReceiverId(inbound.getSenderId())
                                .setReceiverType(ImMessageParticipantTypeEnum.USER.getType())
                                .setType(ImMessageTypeEnum.TEXT.getType())
                                .setContent(rule.getReplyContent())
                                .setPersistent(true));
            }
            break;
        }
    }

    /**
     * 转人工：自动分配一个「在线」的客服，以客服自身身份（csId + CS 类型）给用户发接入提示，
     * 并把用户原问题转给客服便于接手；无可用客服时由机器人兜底提示。
     *
     * <p>实现方式：用户原本在与机器人私聊，转人工时新建一条「用户 ↔ 客服（csId + CS 类型）」私聊——
     * 以客服自身 id 为发送方给用户发提示即开通该会话；再把用户原问题以用户身份发给客服作为上下文。
     * 客服非机器人，不会再次触发本引擎，无递归。
     */
    private void transferToHuman(ImRobotDO robot, ImPrivateMessageDO inbound) {
        Long userId = inbound.getSenderId();
        // 总闸：自动分配关闭时，转人工整体降级为兜底提示（不挑客服、不开会话）
        if (!imProperties.getRobot().isEnableAutoAssign()) {
            sendTransferFallback(robot, userId);
            return;
        }
        // 去重冷却：近 30 分钟内已与某客服有过私聊，说明已转接/在人工会话中，避免每次在 bot 会话里说话都重开客服会话
        if (customerServiceManagerService.hasActiveCsConversation(userId)) {
            // 冷却期内已在人工会话中：发一句提示而非静默丢弃，避免用户在机器人窗口石沉大海
            privateMessageService.sendPrivateMessage(ImMessageParticipantTypeEnum.ROBOT.getType(), robot.getId(),
                    new ImPrivateMessageSendDTO()
                            .setReceiverId(userId)
                            .setReceiverType(ImMessageParticipantTypeEnum.USER.getType())
                            .setType(ImMessageTypeEnum.TEXT.getType())
                            .setContent("您已接入人工客服，请稍候，客服会尽快回复您～")
                            .setPersistent(true));
            return;
        }
        ImCustomerServiceDO cs = customerServiceManagerService.pickOnlineCustomerService();
        if (cs == null) {
            // 兜底：当前无可用人工客服
            sendTransferFallback(robot, userId);
            return;
        }
        String csName = StrUtil.isNotBlank(cs.getNickname()) ? cs.getNickname() : cs.getUsername();
        // 1) 给用户：接入提示（以客服自身身份 cs.id + CS 类型开通用户↔客服会话）
        String tip = "正在为您接入人工客服「" + csName + "」，请稍候～"
                + (StrUtil.isNotBlank(cs.getWelcomeMsg()) ? "\n" + cs.getWelcomeMsg() : "");
        privateMessageService.sendPrivateMessage(ImMessageParticipantTypeEnum.CS.getType(), cs.getId(),
                new ImPrivateMessageSendDTO()
                        .setReceiverId(userId)
                        .setReceiverType(ImMessageParticipantTypeEnum.USER.getType())
                        .setType(ImMessageTypeEnum.TEXT.getType())
                        .setContent(tip)
                        .setPersistent(true));
        // 2) 给客服：带来用户原问题，便于接手（接收方为客服，receiverType=CS）
        String ctx = "【机器人转人工】用户向「"
                + (StrUtil.isNotBlank(robot.getNickname()) ? robot.getNickname() : robot.getUsername())
                + "」咨询：" + inbound.getContent();
        privateMessageService.sendPrivateMessage(userId,
                new ImPrivateMessageSendDTO()
                        .setReceiverId(cs.getId())
                        .setReceiverType(ImMessageParticipantTypeEnum.CS.getType())
                        .setType(ImMessageTypeEnum.TEXT.getType())
                        .setContent(ctx)
                        .setPersistent(true));
        // 3) 给用户：转人工切换信号（前端识别后自动切到人工客服会话）
        //    用户原本停在机器人会话窗口，仅发 tip 不会切窗；这条信号携带 csUserId 让前端打开对应会话。
        //    字段名必须用 csUserId（与 App.vue handleTransferToHuman 解析的 data.csUserId 对齐），
        //    值取 cs.getId()，与「联系客服」前端打开会话所用的 targetId（= im_customer_service.id）一致，确保切换落到同一条会话。
        //    直接走 WS 推送（不经 privateMessageService），规避其「persistent=false 时不推接收方」的限制，也不落库。
        ImPrivateMessageDTO switchSignal = new ImPrivateMessageDTO()
                .setType(ImMessageTypeEnum.TRANSFER_TO_HUMAN.getType())
                .setSenderType(ImMessageParticipantTypeEnum.CS.getType())
                .setSenderId(cs.getId())
                .setReceiverId(userId)
                .setReceiverType(ImMessageParticipantTypeEnum.USER.getType())
                .setContent("{\"csUserId\":" + cs.getId() + ",\"csName\":\"" + escapeJson(csName) + "\"}")
                .setSendTime(java.time.LocalDateTime.now());
        imWebSocketService.sendPrivateMessageAsync(List.of(userId), switchSignal);
    }

    /**
     * 转人工兜底提示：当自动分配关闭或当前无可用客服时，以机器人身份给用户发一句「请留言」提示。
     */
    private void sendTransferFallback(ImRobotDO robot, Long userId) {
        // 方案 C：兜底提示以机器人自身身份（im_robot.id + ROBOT 类型）发出
        privateMessageService.sendPrivateMessage(ImMessageParticipantTypeEnum.ROBOT.getType(), robot.getId(),
                new ImPrivateMessageSendDTO()
                        .setReceiverId(userId)
                        .setReceiverType(ImMessageParticipantTypeEnum.USER.getType())
                        .setType(ImMessageTypeEnum.TEXT.getType())
                        .setContent("人工客服当前繁忙，请稍后再试或留下您的联系方式，我们会尽快与您联系～")
                        .setPersistent(true));
    }

    /**
     * 规则匹配
     *
     * @param rule    回复规则（matchType：1-精确 2-包含 3-正则）
     * @param content 用户消息文本
     */
    private boolean matchRule(ImRobotReplyRuleDO rule, String content) {
        String keyword = rule.getKeyword();
        if (StrUtil.isBlank(keyword)) {
            return false;
        }
        Integer matchType = rule.getMatchType();
        if (matchType == null) {
            return false;
        }
        switch (matchType) {
            case 1: // 精确匹配
                return keyword.equals(content);
            case 2: // 包含匹配
                return content.contains(keyword);
            case 3: // 正则匹配
                try {
                    return Pattern.compile(keyword).matcher(content).find();
                } catch (PatternSyntaxException e) {
                    log.warn("[ImRobotReplyEngine] 正则规则无效 keyword={}", keyword, e);
                    return false;
                }
            default:
                return false;
        }
    }

    /**
     * 异步将入站事件 POST 到机器人 webhook（仅尽力而为，异常吞掉不影响主流程）
     */
    private void postWebhookAsync(ImRobotDO robot, ImPrivateMessageDO inbound) {
        // 注意：严禁用 String.format 拼接 JSON —— 用户消息含 '%'（如"打5折""99% off"）会抛
        // UnknownFormatConversionException，且该方法在 runAsync 之外同步执行，异常会直接上抛打断 handleInbound，
        // 导致机器人不回复、转人工也不触发。改用字符串拼接 + escapeJson，零格式依赖。
        String payload = "{\"robotId\":" + robot.getId()
                + ",\"robotUsername\":\"" + escapeJson(robot.getUsername()) + "\""
                + ",\"fromUserId\":" + inbound.getSenderId()
                + ",\"messageId\":" + inbound.getId()
                + ",\"content\":\"" + escapeJson(inbound.getContent()) + "\"}";
        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(3))
                        .build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(robot.getWebhookUrl()))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 400) {
                    log.warn("[ImRobotReplyEngine] webhook 返回非成功状态码={}", response.statusCode());
                }
            } catch (Exception e) {
                log.warn("[ImRobotReplyEngine] webhook 推送失败 url={}", robot.getWebhookUrl(), e);
            }
        });
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

}
