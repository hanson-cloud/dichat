package com.diqin.cloud.module.im.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户注册 Request VO
 *
 * @author hanson
 */
@Schema(description = "App - IM 用户注册 Request VO")
@Data
@Accessors(chain = true)
public class AppImAuthRegisterReqVO {

    @Schema(description = "登录账号", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "登录账号不能为空")
    private String username;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

//    @Schema(description = "手机验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
//    @NotEmpty(message = "手机验证码不能为空")
//    @Length(min = 4, max = 6, message = "手机验证码长度为 4-6 位")
//    @Pattern(regexp = "^[0-9]+$", message = "手机验证码必须都是数字")
//    private String code;
}
