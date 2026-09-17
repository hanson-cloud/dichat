package com.diqin.cloud.module.im.service.message;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.message.vo.privates.ImPrivateMessageManagerPageReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.privates.AppImPrivateMessageListReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.privates.AppImPrivateMessageSendReqVO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.service.message.dto.ImPrivateMessageSendDTO;

import java.util.List;

/**
 * IM 私聊消息 Service 接口
 *
 * @author hanson
 */
public interface ImPrivateMessageService {

    /**
     * 【用户调用】发送私聊消息
     * <p>
     * 用户在 IM 客户端发送 TEXT / IMAGE 等消息时调用，含幂等、好友校验、敏感词、quote 解析等业务校验。
     * type 校验由 VO 层 {@code @InEnum} + {@code @AssertTrue} 完成（仅允许 normal 类型）。
     *
     * @param senderId 发送人编号
     * @param reqVO    发送请求
     * @return 消息
     */
    ImPrivateMessageDO sendPrivateMessage(Long senderId, AppImPrivateMessageSendReqVO reqVO);

    /**
     * 【用户调用】发送私聊消息（显式指定发送方类型）
     * <p>
     * 方案 C：消息参与方不再限定为 im_users，客服 / 机器人作为独立参与方需携带类型维度寻址。
     *
     * @param senderType 发送方类型（{@link ImMessageParticipantTypeEnum}）
     * @param senderId   发送人编号
     * @param reqVO      发送请求
     * @return 消息
     */
    ImPrivateMessageDO sendPrivateMessage(Integer senderType, Long senderId, AppImPrivateMessageSendReqVO reqVO);

    /**
     * 【系统调用】发送私聊消息
     *
     * @param senderId 发送人编号
     * @param dto      消息 DTO
     * @return 构造的消息 DO（持久化时 id 已回填）
     */
    ImPrivateMessageDO sendPrivateMessage(Long senderId, ImPrivateMessageSendDTO dto);

    /**
     * 【系统调用】发送私聊消息（显式指定发送方 / 接收方类型）
     * <p>
     * 机器人自动回复、人工客服转接等场景使用；dto 上的 senderType / receiverType 优先级高于此处入参之外的默认 USER。
     *
     * @param senderType 发送方类型（{@link ImMessageParticipantTypeEnum}）
     * @param senderId   发送人编号
     * @param dto        消息 DTO（其 senderType / receiverType 字段可进一步覆盖）
     * @return 构造的消息 DO（持久化时 id 已回填）
     */
    ImPrivateMessageDO sendPrivateMessage(Integer senderType, Long senderId, ImPrivateMessageSendDTO dto);

    /**
     * 【用户调用】撤回私聊消息
     *
     * @param userId    当前用户编号
     * @param messageId 消息编号
     * @return 撤回后的消息
     */
    ImPrivateMessageDO recallPrivateMessage(Long userId, Long messageId);

    /**
     * 拉取私聊消息（增量）
     *
     * @param userId 当前用户编号
     * @param minId  最小消息 id（不含）
     * @param size   拉取数量
     * @return 消息列表
     */
    List<ImPrivateMessageDO> pullPrivateMessageList(Long userId, Long minId, Integer size);

    /**
     * 标记私聊消息已读
     * <p>
     * 语义：将「对方发给当前用户、id <= messageId 的未读消息」一次性翻转为已读，
     * 与群聊 readGroupMessages 对称，避免"select-then-update"两步式带来的竞态。
     *
     * @param userId     当前用户编号
     * @param receiverId 接收方用户编号（对方）
     * @param messageId  已读位置（含），通常是前端会话内最大消息 id
     */
    void readPrivateMessages(Long userId, Long receiverId, Long messageId);

    /**
     * 查询对方已读到我发的最大消息 id
     * <p>
     * 用于多端 / 离线场景下的已读位置补齐：客户端进入会话或断线重连后，
     * 调用此接口拿到对方的 maxReadId，再按 id <= maxReadId 翻转本地自发消息为已读，弥补离线期间错过的 RECEIPT 推送事件。
     *
     * @param userId 当前用户编号
     * @param peerId 对方用户编号
     * @return 对方已读到的最大消息 id；对方一条都没读过时返回 null
     */
    Long getMaxReadMessageId(Long userId, Long peerId);

    /**
     * 查询私聊历史消息（游标拉取）
     *
     * @param userId 当前用户编号
     * @param reqVO  拉取请求
     * @return 消息列表（按 id 倒序）
     */
    List<ImPrivateMessageDO> getPrivateMessageList(Long userId, AppImPrivateMessageListReqVO reqVO);

    /**
     * 物理删除当前用户与 peerUserId 会话中的若干条消息
     * <p>
     * 权限：仅当所有 messageIds 的发送人都是 userId 时才允许删除；混入非本人消息直接抛错，避免越权
     * <p>
     * 推送：删除成功后给 userId 多端推 PRIVATE_MESSAGE_DELETE（payload.messageIds 让前端按 id 精确移除）
     *
     * @param userId     当前用户编号
     * @param peerUserId 对方用户编号
     * @param messageIds 待删除的消息编号列表
     * @return 实际删除的消息条数
     */
    int deletePrivateMessages(Long userId, Long peerUserId, List<Long> messageIds);

    /**
     * 清空当前用户与 peerUserId 的私聊会话
     * <p>
     * 物理删除 userId ↔ peerUserId 之间所有（双向）消息；删除后给 userId 多端推 PRIVATE_CHAT_CLEAR，
     * 对方多端不动（与单边清空语义对齐，与 box-im ChatDeleteDTO 行为一致）
     *
     * @param userId     当前用户编号
     * @param peerUserId 对方用户编号
     * @return 实际删除的消息条数
     */
    int clearPrivateChat(Long userId, Long peerUserId);

    // ==================== 管理后台 ====================

    /**
     * 【管理后台】分页查询私聊消息
     */
    PageResult<ImPrivateMessageDO> getPrivateMessagePage(ImPrivateMessageManagerPageReqVO reqVO);

    /**
     * 【管理后台】获取私聊消息详情
     */
    ImPrivateMessageDO getPrivateMessage(Long id);

    /**
     * 【管理后台】导出私聊消息（按筛选条件全量，不分页）
     */
    List<ImPrivateMessageDO> getPrivateMessageExportList(ImPrivateMessageManagerPageReqVO reqVO);

}
