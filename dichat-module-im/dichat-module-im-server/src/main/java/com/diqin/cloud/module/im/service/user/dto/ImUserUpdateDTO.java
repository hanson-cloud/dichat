package com.diqin.cloud.module.im.service.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户资料更新 DTO
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImUserUpdateDTO {

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 头像缩略图
     */
    private String avatarThumb;

    /**
     * 性别（0 男、1 女）
     */
    private Integer sex;

    /**
     * 个人签名
     */
    private String signature;
}
