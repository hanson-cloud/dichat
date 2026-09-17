package com.diqin.cloud.module.im.service.friend.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 好友更新 DTO
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImFriendUpdateDTO {

    /**
     * 好友的用户编号
     */
    private Long friendUserId;

    /**
     * 好友备注
     */
    private String displayName;

    /**
     * 免打扰（0:正常 1:免打扰）
     */
    private Boolean silent;

    /**
     * 置顶
     */
    private Boolean pinned;
}
