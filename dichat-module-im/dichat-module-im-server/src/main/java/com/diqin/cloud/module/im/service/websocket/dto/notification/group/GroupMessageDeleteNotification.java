package com.diqin.cloud.module.im.service.websocket.dto.notification.group;

import lombok.Data;

import java.util.List;

/**
 * 群消息删除通知
 * <p>
 * 语义：当前用户在 groupId 群中删除了若干条自己发送的 messageIds（DB 物理删除），仅推送给操作人多端做多端同步；
 * 群内其他成员本地缓存由 DB 物理删除后下次拉取时自然缺失。
 *
 * @author hanson
 */
@Data
public class GroupMessageDeleteNotification extends BaseGroupNotification {

    /**
     * 群编号
     */
    private Long groupId;

    /**
     * 被删除的消息编号列表
     */
    private List<Long> messageIds;

}
