package com.diqin.cloud.module.im.service.websocket.dto;

import cn.hutool.core.lang.Assert;
import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.service.websocket.dto.notification.friend.BaseFriendNotification;
import com.diqin.cloud.module.im.service.websocket.dto.notification.group.BaseGroupNotification;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * IM 私聊消息 WebSocket 统一推送 DTO
 * <p>
 * 发送、已读、回执、撤回都通过 {@link #type} 字段区分，
 * 并统一复用 {@link #id} 字段表达目标消息或已读位置。
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImPrivateMessageDTO {

    public static final String TYPE = "im-private-message";

    /**
     * 消息编号
     * <p>
     * 普通消息：当前消息 id
     * READ：已读位置（我已读到这条消息）
     * RECEIPT：已读位置（对方已读到这条消息）
     * RECALL：被撤回的原消息 id
     */
    private Long id;
    /**
     * 客户端消息编号
     */
    private String clientMessageId;
    /**
     * 发送人编号
     */
    private Long senderId;
    /**
     * 接收人编号
     */
    private Long receiverId;
    /**
     * 发送方类型：1-用户 2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     * <p>
     * 方案 C：私聊参与方不再限定为 im_users，机器人 / 人工客服作为独立参与方，
     * 客户端据此渲染「机器人 / 客服」身份气泡，并与 receiverId 组合出完整的参与方定位。
     */
    private Integer senderType;
    /**
     * 接收方类型：1-用户 2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     */
    private Integer receiverType;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息状态
     */
    private Integer status;
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    // ========== 静态工厂方法 ==========

    /**
     * 构建发送消息 DTO
     *
     * @param message 私聊消息 DO
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofSend(ImPrivateMessageDO message) {
        return BeanUtils.toBean(message, ImPrivateMessageDTO.class);
    }

    /**
     * 构建已读同步 DTO（多端同步：通知自己的其他终端"我已经读了某个会话"）
     *
     * @param senderId   当前用户编号
     * @param receiverId 对方用户编号
     * @param readId     已读位置（最大已读消息编号）
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofRead(Long senderId, Long receiverId, Long readId) {
        return new ImPrivateMessageDTO()
                .setId(readId).setType(ImMessageTypeEnum.READ.getType())
                .setSenderId(senderId).setReceiverId(receiverId);
    }

    /**
     * 构建已读回执 DTO（通知对方"我已读了你的消息"）
     *
     * @param senderId   已读方的用户编号
     * @param receiverId 对方用户编号
     * @param readId     已读位置（最大已读消息编号）
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofReceipt(Long senderId, Long receiverId, Long readId) {
        return new ImPrivateMessageDTO()
                .setId(readId).setType(ImMessageTypeEnum.RECEIPT.getType())
                .setSenderId(senderId).setReceiverId(receiverId);
    }

    /**
     * 构建参与方资料变更同步 DTO（方案 C：人工客服 / 机器人资料变更后广播给在线 C 端用户）
     * <p>客户端收到后据此重新拉取「联系客服」/ 机器人列表，确保会话内资料实时一致。</p>
     *
     * @param participantType 参与方类型：2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     * @param participantId   参与方编号（im_robot.id / im_customer_service.id）
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofParticipantSync(Integer participantType, Long participantId) {
        return new ImPrivateMessageDTO()
                .setType(ImMessageTypeEnum.PARTICIPANT_PROFILE_SYNC.getType())
                .setSenderType(participantType).setSenderId(participantId)
                .setSendTime(LocalDateTime.now());
    }

    // ==================== 好友变更相关 ====================

    /**
     * 构建好友通知推送 DTO（统一入口）
     *
     * @param type            消息类型；取自 {@link ImMessageTypeEnum} 中的 FRIEND_* 段
     * @param operatorUserId  操作人用户编号；同时作为帧的 senderId 用于定位接收端 friendUserId
     * @param receiverUserId  推送目标用户编号
     * @param payload         好友通知 payload（继承 {@link BaseFriendNotification}）
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofFriendNotification(Integer type, Long operatorUserId,
                                                           Long receiverUserId, BaseFriendNotification payload) {
        validateNotification(type, payload, ImMessageTypeEnum.isFriendNotification(type));
        return new ImPrivateMessageDTO().setType(type)
                .setSenderId(operatorUserId).setReceiverId(receiverUserId)
                .setContent(JsonUtils.toJsonString(payload)).setSendTime(LocalDateTime.now());
    }

    // ==================== 群定向私聊通知 ====================

    /**
     * 构建群通知推送 DTO（走私聊通道定向推送，不入群消息流）
     * <p>
     * 用于 GROUP_REQUEST_RECEIVED / GROUP_REQUEST_APPROVED / GROUP_REQUEST_REJECTED 段位；
     * 与 {@link com.diqin.cloud.module.im.service.message.dto.ImGroupMessageSendDTO} 走 sendGroupMessage 群广播路径不同
     *
     * @param type           消息类型；取自 {@link ImMessageTypeEnum} 中的 GROUP_REQUEST_* 段
     * @param operatorUserId 操作人用户编号；同时作为帧的 senderId
     * @param receiverUserId 推送目标用户编号
     * @param payload        群事件 payload（继承 {@link BaseGroupNotification}）
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofGroupNotification(Integer type, Long operatorUserId,
                                                          Long receiverUserId, BaseGroupNotification payload) {
        validateNotification(type, payload, ImMessageTypeEnum.isGroupRequestNotification(type));
        return new ImPrivateMessageDTO().setType(type)
                .setSenderId(operatorUserId).setReceiverId(receiverUserId)
                .setContent(JsonUtils.toJsonString(payload)).setSendTime(LocalDateTime.now());
    }

    // ==================== 实时通话信令 ====================

    /**
     * 构建通话信令推送 DTO（走私聊通道仅推参与方，不入消息流）
     * <p>
     * 用于 RTC_CALL（INVITE / REJECT 等）/ RTC_PARTICIPANT_CONNECTED / RTC_PARTICIPANT_DISCONNECTED
     *
     * @param type           消息类型；取自 {@link ImMessageTypeEnum} 中的 RTC_* 段
     * @param senderId       发送人编号；INVITE 时为发起人，REJECT 时为拒绝者，参与者事件时为加入 / 离开者
     * @param receiverUserId 推送目标用户编号
     * @param payload        通话事件 payload（任意 RTC 通知 DTO）
     * @return 私聊 DTO
     */
    public static ImPrivateMessageDTO ofRtcNotification(Integer type, Long senderId,
                                                        Long receiverUserId, Object payload) {
        validateNotification(type, payload, ImMessageTypeEnum.isRtcNotification(type));
        return new ImPrivateMessageDTO().setType(type)
                .setSenderId(senderId).setReceiverId(receiverUserId)
                .setContent(JsonUtils.toJsonString(payload)).setSendTime(LocalDateTime.now());
    }

    private static void validateNotification(Integer type, Object payload, boolean supported) {
        Assert.notNull(type, "消息类型不能为空");
        Assert.notNull(payload, "消息内容不能为空");
        Assert.isTrue(supported, "消息类型不匹配 type={}", type);
    }

}
