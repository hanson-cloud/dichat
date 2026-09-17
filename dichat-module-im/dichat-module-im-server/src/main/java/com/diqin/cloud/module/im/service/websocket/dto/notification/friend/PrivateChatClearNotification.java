package com.diqin.cloud.module.im.service.websocket.dto.notification.friend;

import lombok.Data;

/**
 * 私聊会话清空通知
 * <p>
 * 语义：当前用户清空其与 friendUserId 的所有私聊消息（DB 物理删除），仅推送给操作人多端做多端同步；
 * 对方本地缓存由 DB 物理删除后下次拉取时自然缺失。
 * <p>
 * payload 仅需 friendUserId；前端按它清空与该好友的会话本地缓存即可
 *
 * @author hanson
 */
@Data
public class PrivateChatClearNotification extends BaseFriendNotification {

}
