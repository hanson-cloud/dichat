package com.diqin.cloud.module.im.service.websocket.dto.notification.group;

import lombok.Data;

/**
 * 群会话清空通知
 * <p>
 * 语义：当前用户清空 groupId 群中可见的所有消息（DB 物理删除），仅推送给操作人多端做多端同步；
 * 群内其他成员本地缓存由 DB 物理删除后下次拉取时自然缺失。
 *
 * @author hanson
 */
@Data
public class GroupChatClearNotification extends BaseGroupNotification {

    /**
     * 群编号
     */
    private Long groupId;

}
