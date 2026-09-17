package com.diqin.cloud.module.im.service.conversation;

import com.diqin.cloud.module.im.controller.app.conversation.vo.AppImConversationRespVO;
import com.diqin.cloud.module.im.dal.dataobject.conversation.ImConversationDO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 私聊会话 Service
 * <p>
 * 维护「参与方配对」维度的会话聚合：消息发送时 upsert 会话、更新末条消息、给接收方未读 +1；
 * 打开会话时清零己方未读。会话列表 / 未读角标直接读此表，避免运行时聚合 im_private_message。
 *
 * @author 速构构
 */
public interface ImConversationService {

    /**
     * 消息发送后维护会话（upsert + 末条消息 + 接收方未读 +1）
     * <p>参与方编号传入「规范 id」：客服传 im_customer_service.id、机器人传 im_robot.id、用户传 im_users.id。
     * 若传入的是过渡期遗留的「绑定 im_users.id」，本方法内部自动解析为规范 id，保证同一逻辑会话不裂成两行。</p>
     *
     * @param senderType     发送方类型
     * @param senderId       发送方编号
     * @param receiverType   接收方类型
     * @param receiverId     接收方编号
     * @param messageId      消息编号
     * @param content        消息内容（文本或 JSON）
     * @param type           消息类型
     * @param sendTime       发送时间
     */
    void onMessageSent(Integer senderType, Long senderId, Integer receiverType, Long receiverId,
                       Long messageId, String content, Integer type, LocalDateTime sendTime);

    /**
     * 读者打开会话时，清零自己在会话上的未读数
     *
     * @param readerType 读者类型（客服=3 / 用户=1）
     * @param readerId   读者编号（客服传 csId / 用户传 userId）
     * @param peerType   对端类型
     * @param peerId     对端编号
     */
    void markRead(Integer readerType, Long readerId, Integer peerType, Long peerId);

    /**
     * 查询某客服的全部会话（按最近消息倒序），供管理端客服工作台列表
     *
     * @param csId 客服编号（im_customer_service.id）
     * @return 会话列表（a_type=3, a_id=csId, a_deleted=0）
     */
    List<ImConversationDO> getCsConversations(Long csId);

    /**
     * 查询某 C 端用户的全部会话（按最近消息倒序），供 APP 端会话列表
     *
     * @param userId 用户编号（im_users.id）
     * @return 会话列表（用户任一侧，且未被该用户删除）
     */
    List<ImConversationDO> getUserConversations(Long userId);

    /**
     * 查询某 C 端用户的会话列表（含对端昵称 / 头像回填），供 APP 端渲染会话页
     * <p>{@code peerId} 返回规范 id：机器人=im_robot.id / 客服=im_customer_service.id / 用户=im_users.id。</p>
     *
     * @param userId 用户编号（im_users.id）
     * @return 会话列表（按最近消息倒序）
     */
    List<AppImConversationRespVO> getUserConversationList(Long userId);

}
