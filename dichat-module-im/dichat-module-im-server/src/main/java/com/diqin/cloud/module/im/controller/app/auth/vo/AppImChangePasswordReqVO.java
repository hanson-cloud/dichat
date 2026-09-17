package com.diqin.cloud.module.im.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 修改密码 Request VO
 *
 * @author hanson
 */
@Schema(description = "App - IM 修改密码 Request VO")
@Data
@Accessors(chain = true)
public class AppImChangePasswordReqVO {

    @Schema(description = "原密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "654321", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
