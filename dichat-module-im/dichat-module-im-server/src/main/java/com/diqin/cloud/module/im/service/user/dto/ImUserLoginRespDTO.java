package com.diqin.cloud.module.im.service.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户登录响应 DTO
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImUserLoginRespDTO {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * accessToken 过期时间（秒）
     */
    private Integer accessTokenExpiresIn;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * refreshToken 过期时间（秒）
     */
    private Integer refreshTokenExpiresIn;
}
