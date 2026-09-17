package com.diqin.cloud.module.im.service.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 用户注册 DTO
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImUserRegisterDTO {

    /**
     * 登录账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称；为空时与 username 相同
     */
    private String nickname;
}
