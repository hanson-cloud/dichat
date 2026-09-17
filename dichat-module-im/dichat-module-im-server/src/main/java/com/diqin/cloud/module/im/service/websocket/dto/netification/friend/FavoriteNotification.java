package com.diqin.cloud.module.im.service.websocket.dto.netification.friend;

import com.diqin.cloud.module.im.service.websocket.dto.notification.friend.BaseFriendNotification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 收藏消息通知
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FavoriteNotification extends BaseFriendNotification {

    private Long messageId;
    private Integer messageType;
}
