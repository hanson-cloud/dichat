package com.diqin.cloud.module.im.service.websocket.dto.notification.group;

import lombok.Data;

import java.util.List;

/**
 * 群事件成员列表通知基类
 *
 * @author hanson
 */
@Data
public abstract class GroupMemberListNotification extends BaseGroupNotification {

    /**
     * 受影响的成员用户编号列表
     */
    private List<Long> memberUserIds;

}
