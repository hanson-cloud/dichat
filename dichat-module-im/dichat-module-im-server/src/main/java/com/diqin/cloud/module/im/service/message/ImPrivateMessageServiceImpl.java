package com.diqin.cloud.module.im.service.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.message.vo.privates.ImPrivateMessageManagerPageReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.privates.AppImPrivateMessageListReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.privates.AppImPrivateMessageSendReqVO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.dal.mysql.message.ImPrivateMessageMapper;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageStatusEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import com.diqin.cloud.module.im.service.conversation.ImConversationService;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceManagerService;
import com.diqin.cloud.module.im.service.customer_service.event.CsMessageEvent;
import com.diqin.cloud.module.im.service.friend.ImFriendService;
import com.diqin.cloud.module.im.service.message.dto.ImPrivateMessageSendDTO;
import com.diqin.cloud.module.im.service.review.ImMessageReviewManagerService;
import com.diqin.cloud.module.im.service.robot.ImRobotManagerService;
import com.diqin.cloud.module.im.service.robot.ImRobotReplyEngineService;
import com.diqin.cloud.module.im.service.sensitiveword.ImSensitiveWordService;
import com.diqin.cloud.module.im.service.websocket.ImWebSocketService;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.message.QuoteMessage;
import com.diqin.cloud.module.im.service.websocket.dto.message.RecallMessage;
import com.diqin.cloud.module.im.service.websocket.dto.notification.friend.PrivateChatClearNotification;
import com.diqin.cloud.module.im.service.websocket.dto.notification.friend.PrivateMessageDeleteNotification;
import com.diqin.cloud.module.im.util.ImMessageUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.*;

/**
 * IM 私聊消息 Service 实现类
 *
 * @author hanson
 */
@Service
@Validated
@Slf4j
public class ImPrivateMessageServiceImpl implements ImPrivateMessageService {

    @Resource
    private ImPrivateMessageMapper privateMessageMapper;

    @Resource
    private ImFriendService friendService;
    @Resource
    private ImSensitiveWordService sensitiveWordService;
    @Resource
    private ImMessageReviewManagerService messageReviewManagerService;

    @Resource
    private ImWebSocketService imWebSocketService;

    @Resource
    private ImProperties imProperties;

    @Resource
    private ImRobotManagerService robotManagerService;
    @Resource
    private ImRobotReplyEngineService robotReplyEngineService;
    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;

    @Resource
    private ImConversationService conversationService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    public ImPrivateMessageDO sendPrivateMessage(Long senderId, AppImPrivateMessageSendReqVO reqVO) {
        // 默认发送方为 C 端用户；机器人 / 人工客服主动发送场景走 3-arg 重载
        return sendPrivateMessage(ImMessageParticipantTypeEnum.USER.getType(), senderId, reqVO);
    }

    @Override
    public ImPrivateMessageDO sendPrivateMessage(Integer senderType, Long senderId, AppImPrivateMessageSendReqVO reqVO) {
        // 1.1 幂等校验：根据 senderId + clientMessageId 查重
        ImPrivateMessageDO existing = privateMessageMapper.selectBySenderIdAndClientMessageId(
                senderType, senderId, reqVO.getClientMessageId());
        if (existing != null) {
            log.info("[sendPrivateMessage][幂等命中 senderId({}) clientMessageId({}) 已存在消息({})]",
                    senderId, reqVO.getClientMessageId(), existing.getId());
            return existing;
        }
        // 1.2 消息内容校验
        ImMessageUtils.validateUserMessageContent(reqVO.getType(), reqVO.getContent());
        // 1.3 好友校验（接收方为机器人 / 人工客服时跳过：用户可直接与机器人、客服私聊，无需互为好友）
        // 方案 C：receiverId 即对方自身 id（用户 im_users.id / 机器人 im_robot.id / 客服 im_customer_service.id）
        boolean receiverIsRobot = robotManagerService.getRobotById(reqVO.getReceiverId()) != null;
        boolean receiverIsCs = customerServiceManagerService.getCustomerServiceMap(
                List.of(reqVO.getReceiverId())).get(reqVO.getReceiverId()) != null;
        if (!receiverIsRobot && !receiverIsCs) {
            friendService.validateFriend(senderId, reqVO.getReceiverId());
        }
        // 1.4 文本消息敏感词过滤
        if (ImMessageTypeEnum.TEXT.getType().equals(reqVO.getType())) {
            sensitiveWordService.validateText(reqVO.getContent());
        }

        // 2.1 引用 quote 消息规范化
        reqVO.setContent(normalizeQuoteContent(reqVO, senderId));
        // 2.2 构建并保存消息（方案 C：携带发送方 / 接收方类型维度）
        Integer receiverType = receiverIsCs
                ? ImMessageParticipantTypeEnum.CS.getType()
                : (receiverIsRobot
                    ? ImMessageParticipantTypeEnum.ROBOT.getType()
                    : ImMessageParticipantTypeEnum.USER.getType());
        ImPrivateMessageDO message = BeanUtils.toBean(reqVO, ImPrivateMessageDO.class, m -> m
                .setSenderId(senderId).setSenderType(senderType)
                .setReceiverType(receiverType)
                .setStatus(ImMessageStatusEnum.UNREAD.getStatus()).setSendTime(LocalDateTime.now()));
        privateMessageMapper.insert(message);

        // 3.5 维护会话聚合（upsert + 末条消息 + 接收方未读 +1）
        conversationService.onMessageSent(message.getSenderType(), message.getSenderId(),
                message.getReceiverType(), message.getReceiverId(),
                message.getId(), message.getContent(), message.getType(), message.getSendTime());

        // 4. 多媒体消息自动入审核队列
        if (ImMessageTypeEnum.isMediaMessage(reqVO.getType())) {
            messageReviewManagerService.autoCreateReview(message.getId(), 1,
                    message.getType(), message.getSenderId(), message.getContent());
        }

        // 3. WebSocket 异步推送：发送方多端同步
        ImPrivateMessageDTO websocketMessage = ImPrivateMessageDTO.ofSend(message);
        imWebSocketService.sendPrivateMessageAsync(senderId, websocketMessage);
        // 4. 接收方推送：非多媒体消息即时推送；多媒体消息走先审后发（审核通过后由 reviewService 推）
        if (!ImMessageTypeEnum.isMediaMessage(reqVO.getType())) {
            imWebSocketService.sendPrivateMessageAsync(message.getReceiverId(), websocketMessage);
        }
        // 5. 机器人自动回复：接收方为机器人时触发（内部校验启用 / 自动回复开关 + 规则匹配）
        if (receiverIsRobot) {
            robotReplyEngineService.handleInbound(message);
        }
        // 6. 若消息涉及人工客服，发布事件供管理端客服工作台 SSE 实时推送
        maybePublishCsEvent(message);
        return message;
    }

    @Override
    public ImPrivateMessageDO sendPrivateMessage(Long senderId, ImPrivateMessageSendDTO dto) {
        // 默认发送方 / 接收方均为 C 端用户；机器人 / 人工客服场景通过 dto.senderType / dto.receiverType 或 3-arg 重载指定
        return sendPrivateMessage(ImMessageParticipantTypeEnum.USER.getType(), senderId, dto);
    }

    @Override
    public ImPrivateMessageDO sendPrivateMessage(Integer senderType, Long senderId, ImPrivateMessageSendDTO dto) {
        // 1.1 content 序列化：null / String 透传，POJO 走 JSON
        Object payload = dto.getContent();
        String contentString = payload == null || payload instanceof String
                ? (String) payload
                : JsonUtils.toJsonString(payload);
        // 1.2 发送方 / 接收方类型：方法入参 senderType 优先，其次 dto.senderType，最后默认 USER（方案 C 类型维度）
        Integer st = senderType != null ? senderType
                : (dto.getSenderType() != null ? dto.getSenderType() : ImMessageParticipantTypeEnum.USER.getType());
        Integer rt = dto.getReceiverType() != null ? dto.getReceiverType() : ImMessageParticipantTypeEnum.USER.getType();
        // 1.3 构建消息
        ImPrivateMessageDO message = new ImPrivateMessageDO().setClientMessageId(IdUtil.fastSimpleUUID())
                .setSenderId(senderId).setSenderType(st)
                .setReceiverId(dto.getReceiverId()).setReceiverType(rt)
                .setType(dto.getType()).setContent(contentString)
                .setStatus(ImMessageStatusEnum.UNREAD.getStatus()).setSendTime(LocalDateTime.now());
        // 1.4 决定是否持久化：dto.persistent 优先；为 null 时按 type 默认
        boolean persistent = dto.getPersistent() != null
                ? dto.getPersistent()
                : ImMessageTypeEnum.validate(dto.getType()).isPersistent();
        if (persistent) {
            privateMessageMapper.insert(message);
            // 维护会话聚合（upsert + 末条消息 + 接收方未读 +1）
            conversationService.onMessageSent(st, senderId, rt, dto.getReceiverId(),
                    message.getId(), contentString, dto.getType(), message.getSendTime());
        }

        // 2. WebSocket 异步推送：双向（默认）；单边语义（persistent=false）下仅推 sender 多端，对方不感知
        ImPrivateMessageDTO websocketMessage = ImPrivateMessageDTO.ofSend(message);
        if (persistent) {
            imWebSocketService.sendPrivateMessageAsync(dto.getReceiverId(), websocketMessage);
        }
        imWebSocketService.sendPrivateMessageAsync(senderId, websocketMessage);
        // 若消息涉及人工客服，发布事件供管理端客服工作台 SSE 实时推送
        maybePublishCsEvent(message);
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImPrivateMessageDO recallPrivateMessage(Long userId, Long messageId) {
        // 1.1 校验消息存在
        ImPrivateMessageDO message = privateMessageMapper.selectById(messageId);
        if (message == null) {
            throw exception(MESSAGE_NOT_EXISTS);
        }
        // 1.2 只能撤回自己发送的消息
        if (ObjUtil.notEqual(message.getSenderId(), userId)) {
            throw exception(MESSAGE_RECALL_DENIED);
        }
        // 1.3 不能重复撤回
        if (ImMessageStatusEnum.RECALL.getStatus().equals(message.getStatus())) {
            throw exception(MESSAGE_ALREADY_RECALLED);
        }
        // 1.4 只允许撤回限定时间内的消息
        int recallTimeoutMinutes = imProperties.getMessage().getRecallTimeoutMinutes();
        if (message.getSendTime().plusMinutes(recallTimeoutMinutes).isBefore(LocalDateTime.now())) {
            throw exception(MESSAGE_RECALL_TIMEOUT, recallTimeoutMinutes);
        }

        // 2. 更新原消息状态为撤回
        privateMessageMapper.updateById(new ImPrivateMessageDO().setId(messageId)
                .setStatus(ImMessageStatusEnum.RECALL.getStatus()));

        // 3. 发送撤回事件（携带原消息的参与方类型，保证机器人 / 客服会话中的撤回也能正确寻址）
        return sendPrivateMessage(message.getSenderType(), userId, new ImPrivateMessageSendDTO().setReceiverId(message.getReceiverId())
                .setReceiverType(message.getReceiverType())
                .setType(ImMessageTypeEnum.RECALL.getType()).setContent(new RecallMessage().setMessageId(messageId)));
    }

    /**
     * 私聊引用消息规范化
     *
     * @param reqVO 发送请求
     * @param senderId 发送人编号
     * @return 规范化后的 content
     */
    private String normalizeQuoteContent(AppImPrivateMessageSendReqVO reqVO, Long senderId) {
        // 解析客户端 content 里的 quote.messageId
        Long quoteMessageId = ImMessageUtils.parseQuoteMessageId(reqVO.getContent());

        // 情况一：没有 quoteMessageId，直接 remove 掉 content 里可能伪造的 quote 字段
        if (quoteMessageId == null) {
            return ImMessageUtils.removeQuote(reqVO.getContent());
        }

        // 情况二：有 quoteMessageId，加载原消息并校验
        ImPrivateMessageDO original = privateMessageMapper.selectById(quoteMessageId);
        if (original == null
                || ImMessageStatusEnum.RECALL.getStatus().equals(original.getStatus())) {
            throw exception(MESSAGE_QUOTE_INVALID);
        }
        // 校验是同对话
        boolean sameConversation = (ObjUtil.equal(original.getSenderId(), senderId) // 发送人是当前用户，接收人是对方
                && ObjUtil.equal(original.getReceiverId(), reqVO.getReceiverId()))
                || (ObjUtil.equal(original.getSenderId(), reqVO.getReceiverId()) // 发送人是对方，接收人是当前用户
                        && ObjUtil.equal(original.getReceiverId(), senderId));
        if (!sameConversation) {
            throw exception(MESSAGE_QUOTE_INVALID);
        }
        // 构建 quote 对象并注入 content
        QuoteMessage quote = ImMessageUtils.buildQuote(original.getId(),
                original.getSenderId(), original.getType(), original.getContent());
        return ImMessageUtils.appendQuote(reqVO.getContent(), quote);
    }

    /**
     * 解析私聊对端参与方的候选编号
     * <p>方案 C：客服 / 机器人 / 用户均以各自表主键作为唯一地址（不再存在「绑定 im_users.id」并列编号），
     * 故客户端传入的对端编号即规范编号，原样返回即可。供历史 / 已读 / 删除 / 清空等查询按参与方维度匹配。</p>
     *
     * @param apparentId 客户端传入的对端编号
     * @return 对端参与方的候选编号（单元素列表）
     */
    private List<Long> resolvePeerIds(Long apparentId) {
        if (apparentId == null) {
            return List.of();
        }
        return List.of(apparentId);
    }

    @Override
    public List<ImPrivateMessageDO> pullPrivateMessageList(Long userId, Long minId, Integer size) {
        int maxPullSize = imProperties.getMessage().getMaxPullSize();
        if (size > maxPullSize) {
            throw exception(MESSAGE_PULL_SIZE_EXCEEDED, maxPullSize);
        }
        // 0. 拉取时间窗；超过窗口的老消息不再通过离线通道推送
        LocalDateTime minSendTime = LocalDateTime.now().minusDays(imProperties.getMessage().getPrivatePullMaxDays());

        // 根据 minId 和 minSendTime 拉取消息，避免 minId 恰好被发出后才拉取，导致漏消息
        List<ImPrivateMessageDO> messages = privateMessageMapper.selectListByMinId(userId, minId, minSendTime, size);
        log.info("[pullPrivateMessageList][userId({}) minId({}) size({}) result({})]",
                userId, minId, size, messages.size());
        return messages;
    }

    @Override
    public void readPrivateMessages(Long userId, Long receiverId, Long messageId) {
        // 1. 全局开关校验
        if (BooleanUtil.isFalse(imProperties.getMessage().isPrivateReadEnabled())) {
            throw exception(MESSAGE_PRIVATE_READ_DISABLED);
        }
        Assert.notNull(messageId, "已读消息编号不能为空");
        // 2. 把 (对端 → userId) 这条会话上、id <= messageId 的未读消息一步更新为已读
        // 仅 UNREAD 行被命中，避免覆盖已撤回/已读的状态；select-then-update 合成单条 SQL 后也消除了竞态窗口
        // 方案 C：对端可能是客服 / 机器人，其「自身 id」与「绑定 im_users.id」都可能出现在本方向，需逐 id 更新
        List<Long> peerIds = resolvePeerIds(receiverId);
        int updated = 0;
        for (Long peerId : peerIds) {
            updated += privateMessageMapper.updateBySenderIdAndReceiverIdAndIdLeAndStatus(
                    peerId, userId, messageId, ImMessageStatusEnum.UNREAD.getStatus(),
                    new ImPrivateMessageDO().setStatus(ImMessageStatusEnum.READ.getStatus()));
        }
        if (updated == 0) {
            return;
        }

        // 3. 更新对方（receiverId）的好友记录上的 lastReadMessageId，
        //    表示 userId 已读到 receiverId 发给他的消息的 messageId 位置
        //    这样 receiverId 进入会话时能查到 userId 的已读位置（用于已读回执双勾显示）
        friendService.updateLastReadMessageId(receiverId, userId, messageId);

        // 4. 异步发送 READ + RECEIPT 事件（已读位置以前端上报为准，与多端 / 对方 UI 显示一致）
        imWebSocketService.sendPrivateMessageAsync(userId,
                ImPrivateMessageDTO.ofRead(userId, receiverId, messageId));
        imWebSocketService.sendPrivateMessageAsync(receiverId,
                ImPrivateMessageDTO.ofReceipt(userId, receiverId, messageId));
    }

    @Override
    public Long getMaxReadMessageId(Long userId, Long peerId) {
        if (BooleanUtil.isFalse(imProperties.getMessage().isPrivateReadEnabled())) {
            throw exception(MESSAGE_PRIVATE_READ_DISABLED);
        }
        // 从好友记录读取对方已读位置：查 (userId, peerId) 记录的 lastReadMessageId，
        // 表示 peerId 已读到 userId 发给 peerId 的哪条消息
        return friendService.getLastReadMessageId(userId, peerId);
    }

    @Override
    public List<ImPrivateMessageDO> getPrivateMessageList(Long userId, AppImPrivateMessageListReqVO reqVO) {
        // 方案 C：对端可能是客服 / 机器人，需同时按「绑定 im_users.id」与「自身 id」两种编号匹配历史
        List<Long> peerIds = resolvePeerIds(reqVO.getReceiverId());
        return privateMessageMapper.selectHistoryListByPeerIds(userId, peerIds, reqVO.getMaxId(), reqVO.getLimit());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrivateMessages(Long userId, Long peerUserId, List<Long> messageIds) {
        if (CollUtil.isEmpty(messageIds)) {
            return 0;
        }
        // 1. 加载待删消息并校验归属
        List<ImPrivateMessageDO> messages = privateMessageMapper.selectByIds(messageIds);
        if (CollUtil.isEmpty(messages)) {
            return 0;
        }
        // 1.1 校验：所有 messageId 都必须存在 + 全部由当前用户与对端参与方之间互发
        // 方案 C：对端可能是客服 / 机器人，需按「绑定 im_users.id」与「自身 id」两种编号校验归属
        List<Long> peerIds = resolvePeerIds(peerUserId);
        Set<Long> requestedIds = CollUtil.newHashSet(messageIds);
        List<Long> deletableIds = CollUtil.newArrayList();
        for (ImPrivateMessageDO message : messages) {
            boolean belongs = (ObjUtil.equal(message.getSenderId(), userId) && peerIds.contains(message.getReceiverId()))
                    || (peerIds.contains(message.getSenderId()) && ObjUtil.equal(message.getReceiverId(), userId));
            if (!belongs) {
                throw exception(MESSAGE_DELETE_DENIED);
            }
            requestedIds.remove(message.getId());
            deletableIds.add(message.getId());
        }
        // 1.2 校验：所有请求 messageId 都被加载到了（防 ID 伪造 / 已删）
        if (CollUtil.isNotEmpty(requestedIds)) {
            throw exception(MESSAGE_NOT_EXISTS);
        }
        if (CollUtil.isEmpty(deletableIds)) {
            return 0;
        }

        // 2. 物理删除（MyBatis-Plus deleteBatchIds）
        int deleted = privateMessageMapper.deleteByIds(deletableIds);
        log.info("[deletePrivateMessages][userId({}) peerUserId({}) ids={} deleted={}]",
                userId, peerUserId, deletableIds, deleted);

        // 3. WebSocket 多端同步：仅推 userId 自己多端，对方不动（与单边删除语义对齐）
        PrivateMessageDeleteNotification payload = new PrivateMessageDeleteNotification();
        payload.setOperatorUserId(userId);
        payload.setFriendUserId(peerUserId);
        payload.setMessageIds(deletableIds);
        imWebSocketService.sendPrivateMessageAsync(userId, ImPrivateMessageDTO.ofFriendNotification(
                ImMessageTypeEnum.PRIVATE_MESSAGE_DELETE.getType(), userId, userId, payload));
        return deleted;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int clearPrivateChat(Long userId, Long peerUserId) {
        // 1. 物理删除 userId ↔ 对端参与方之间所有（双向）消息
        // 方案 C：对端可能是客服 / 机器人，需按「绑定 im_users.id」与「自身 id」两种编号匹配
        List<Long> peerIds = resolvePeerIds(peerUserId);
        int deleted = privateMessageMapper.deleteByUserPairIds(userId, peerIds);
        log.info("[clearPrivateChat][userId({}) peerUserId({}) deleted={}]", userId, peerUserId, deleted);

        // 2. WebSocket 多端同步：仅推 userId 自己多端，对方不动（与单边清空语义对齐）
        PrivateChatClearNotification payload = (PrivateChatClearNotification)
                new PrivateChatClearNotification()
                        .setOperatorUserId(userId).setFriendUserId(peerUserId);
        imWebSocketService.sendPrivateMessageAsync(userId, ImPrivateMessageDTO.ofFriendNotification(
                ImMessageTypeEnum.PRIVATE_CHAT_CLEAR.getType(), userId, userId, payload));
        return deleted;
    }

    /**
     * 若消息的发送方或接收方为人工客服（CS），发布 {@link CsMessageEvent} 供管理端客服工作台 SSE 实时推送。
     * <p>方案 C：客服以自身 id（csId）作为唯一地址，直接按 id 定位，不再经由 im_users.id 桥接。</p>
     */
    private void maybePublishCsEvent(ImPrivateMessageDO message) {
        Integer receiverType = message.getReceiverType();
        Integer senderType = message.getSenderType();
        if (!ImMessageParticipantTypeEnum.CS.getType().equals(receiverType)
                && !ImMessageParticipantTypeEnum.CS.getType().equals(senderType)) {
            return;
        }
        // 参与方编号：入站为 receiverId，出站为 senderId。
        // 方案 C：客服以自身 id（im_customer_service.id，即 csId）作为唯一地址，
        // App「联系客服」/ 2301 转人工信号下发的 targetId 均为 csId，直接按 id 定位即可。
        Long participantId = ImMessageParticipantTypeEnum.CS.getType().equals(receiverType)
                ? message.getReceiverId() : message.getSenderId();
        ImCustomerServiceDO cs = customerServiceManagerService
                .getCustomerServiceMap(List.of(participantId)).get(participantId);
        if (cs != null) {
            eventPublisher.publishEvent(new CsMessageEvent(cs.getId(), cs.getId(), message));
        }
    }

    // ==================== 管理后台 ====================

    @Override
    public PageResult<ImPrivateMessageDO> getPrivateMessagePage(ImPrivateMessageManagerPageReqVO reqVO) {
        return privateMessageMapper.selectPage(reqVO);
    }

    @Override
    public List<ImPrivateMessageDO> getPrivateMessageExportList(ImPrivateMessageManagerPageReqVO reqVO) {
        return privateMessageMapper.selectExportList(reqVO);
    }

    @Override
    public ImPrivateMessageDO getPrivateMessage(Long id) {
        return privateMessageMapper.selectById(id);
    }

}
