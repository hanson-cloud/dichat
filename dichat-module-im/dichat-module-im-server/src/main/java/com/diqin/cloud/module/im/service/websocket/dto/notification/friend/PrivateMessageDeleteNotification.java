package com.diqin.cloud.module.im.service.websocket.dto.notification.friend;

import lombok.Data;

import java.util.List;

/**
 * 私聊消息删除通知
 * <p>
 * 语义：当前用户对 (userId, peerUserId) 会话里的若干条 messageIds 做了物理删除，
 * 仅推送给操作人多端做多端同步；对方本地缓存不受影响（DB 物理删除后，对方下次拉取会自然缺失）。
 * <p>
 * payload 携带 peerUserId + messageIds，让前端按 messageIds 从本地缓存中精确移除，避免删除"全量"消息
 *
 * @author hanson
 */
@Data
public class PrivateMessageDeleteNotification extends BaseFriendNotification {

    /**
     * 被删除的消息编号列表
     */
    private List<Long> messageIds;

}
