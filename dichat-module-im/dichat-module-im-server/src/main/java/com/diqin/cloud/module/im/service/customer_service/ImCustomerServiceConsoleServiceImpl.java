package com.diqin.cloud.module.im.service.customer_service;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsConversationPageReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsConversationRespVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsMessageListReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsMessageSendReqVO;
import com.diqin.cloud.module.im.dal.dataobject.conversation.ImConversationDO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.conversation.ImConversationMapper;
import com.diqin.cloud.module.im.dal.mysql.message.ImPrivateMessageMapper;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.service.conversation.ImConversationService;
import com.diqin.cloud.module.im.service.customer_service.event.CsMessageEventBus;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import com.diqin.cloud.module.im.service.message.dto.ImPrivateMessageSendDTO;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.CUSTOMER_SERVICE_ADMIN_NOT_BOUND;

/**
 * 管理端「客服工作台」Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImCustomerServiceConsoleServiceImpl implements ImCustomerServiceConsoleService, CsMessageEventBus.CsConnStateListener {

    private static final Logger log = LoggerFactory.getLogger(ImCustomerServiceConsoleServiceImpl.class);

    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;

    @Resource
    private ImPrivateMessageService imPrivateMessageService;

    @Resource
    private ImPrivateMessageMapper privateMessageMapper;

    @Resource
    private ImConversationMapper conversationMapper;

    @Resource
    private ImUserService imUserService;

    @Resource
    private ImConversationService conversationService;

    @Resource
    private CsMessageEventBus csMessageEventBus;

    @Override
    public ImCustomerServiceDO resolveCs(Long adminUserId) {
        ImCustomerServiceDO cs = customerServiceManagerService.getCustomerServiceByAdminUserId(adminUserId);
        if (cs == null) {
            throw ServiceExceptionUtil.exception(CUSTOMER_SERVICE_ADMIN_NOT_BOUND);
        }
        return cs;
    }

    @Override
    public PageResult<ImCsConversationRespVO> getConversationPage(ImCsConversationPageReqVO reqVO, Long adminUserId) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        // 1. 直接读会话聚合表（a_type=3, a_id=csId，按最近消息倒序）—— 零运行时聚合
        List<ImConversationDO> conversations = conversationService.getCsConversations(cs.getId());

        // 2. 批量回填对端用户昵称 / 头像，并映射为 VO
        List<Long> peerIds = conversations.stream().map(ImConversationDO::getParticipantBId).collect(Collectors.toList());
        Map<Long, ImUserDO> userMap = peerIds.isEmpty() ? Map.of() : imUserService.getUserMap(peerIds);
        List<ImCsConversationRespVO> all = conversations.stream().map(c -> {
            ImCsConversationRespVO vo = new ImCsConversationRespVO();
            vo.setPeerId(c.getParticipantBId());
            vo.setUnreadCount(c.getAUnreadCount());
            vo.setLastMessageContent(c.getLastMessageContent());
            vo.setLastMessageType(c.getLastMessageType());
            vo.setLastMessageTime(c.getLastMessageTime());
            vo.setPinned(c.getAPinned());
            ImUserDO user = userMap.get(c.getParticipantBId());
            if (user != null) {
                vo.setPeerNickname(user.getNickname());
                vo.setPeerAvatar(user.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());

        // 3. 手动分页（会话表已按最近消息倒序）
        int total = all.size();
        int from = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        int to = Math.min(from + reqVO.getPageSize(), all.size());
        List<ImCsConversationRespVO> pageList = from >= all.size() ? List.of() : all.subList(from, to);
        return new PageResult<>(pageList, (long) total);
    }

    @Override
    public void markRead(Long peerId, Long adminUserId) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        // 1. 清零客服侧会话未读数（原有逻辑，稳定）
        conversationService.markRead(ImMessageParticipantTypeEnum.CS.getType(), cs.getId(),
                ImMessageParticipantTypeEnum.USER.getType(), peerId);
        // 2. 同步「已读回执」给 APP 端用户：把该会话内「用户发来、客服尚未读」的消息翻成 READ，
        //    并通过 WS 给 APP 用户推送 RECEIPT（type=2200），使其气泡显示「已读」双勾。
        //    私聊已读开关关闭或任何异常都不应影响「清零未读」主流程，故 try/catch 仅告警。
        try {
            ImConversationDO conv = conversationMapper.selectByPair(
                    ImMessageParticipantTypeEnum.CS.getType(), cs.getId(),
                    ImMessageParticipantTypeEnum.USER.getType(), peerId);
            if (conv != null && conv.getLastMessageId() != null) {
                imPrivateMessageService.readPrivateMessages(cs.getId(), peerId, conv.getLastMessageId());
            }
        } catch (Exception e) {
            log.warn("[CS Console] 同步已读回执失败（不影响会话已读） peerId={}", peerId, e);
        }
    }

    @Override
    public List<ImPrivateMessageDO> getMessageList(ImCsMessageListReqVO reqVO, Long adminUserId) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        return privateMessageMapper.selectCsConversationMessages(
                cs.getId(), reqVO.getPeerId(), reqVO.getMaxId(), reqVO.getLimit());
    }

    @Override
    public ImPrivateMessageDO sendMessage(ImCsMessageSendReqVO reqVO, Long adminUserId) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        // 以客服身份（CS 类型 + csId）发送，复用既有发送链路：落库 + 推送给 APP 端用户 + 触发 SSE 实时推送
        return imPrivateMessageService.sendPrivateMessage(
                ImMessageParticipantTypeEnum.CS.getType(), cs.getId(),
                new ImPrivateMessageSendDTO()
                        .setReceiverId(reqVO.getPeerId())
                        .setReceiverType(ImMessageParticipantTypeEnum.USER.getType())
                        .setType(reqVO.getType())
                        .setContent(reqVO.getContent())
                        .setPersistent(true));
    }

    @Override
    public SseEmitter openStream(Long adminUserId) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        SseEmitter emitter = new SseEmitter(30L * 60 * 1000); // 30 分钟超时，前端断线自动重连
        csMessageEventBus.register(cs.getId(), emitter);
        emitter.onCompletion(() -> csMessageEventBus.unregister(cs.getId(), emitter));
        emitter.onTimeout(() -> csMessageEventBus.unregister(cs.getId(), emitter));
        // 连接异常（如网关断流、客户端断网）时及时清理，避免半开悬挂的连接长期占用
        emitter.onError((ex) -> csMessageEventBus.unregister(cs.getId(), emitter));
        return emitter;
    }

    /**
     * 把本 Service 注册为 SSE 连接状态监听器：连线自动进入忙碌、断线自动下线。
     * <p>监听器在 Bean 初始化后注册，避免依赖未就绪。</p>
     */
    @PostConstruct
    public void init() {
        csMessageEventBus.setConnListener(this);
    }

    @Override
    public void onFirstConnect(Long csId) {
        // 工作台打开 = 上线，按需求自动进入「忙碌(2)」；坐席可随后手动切到「在线(1)」
        customerServiceManagerService.setStatus(csId, 2);
    }

    @Override
    public void onLastDisconnect(Long csId) {
        // 全部连接断开（关页 / 心跳丢失 / 网关断流）= 自动下线(0)
        customerServiceManagerService.setStatus(csId, 0);
    }

    @Override
    public void setStatus(Long adminUserId, Integer status) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        customerServiceManagerService.setStatus(cs.getId(), status);
    }

    @Override
    public Integer getMyStatus(Long adminUserId) {
        ImCustomerServiceDO cs = resolveCs(adminUserId);
        return cs.getStatus();
    }

}
