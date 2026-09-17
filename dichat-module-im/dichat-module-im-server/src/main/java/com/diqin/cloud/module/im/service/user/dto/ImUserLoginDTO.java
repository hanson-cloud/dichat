package com.diqin.cloud.module.im.service.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户登录 DTO
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImUserLoginDTO {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 记住我（延长 token 有效期）
     */
    private Boolean rememberMe;

    /**
     * 终端类型
     */
    private Integer terminal;
}
