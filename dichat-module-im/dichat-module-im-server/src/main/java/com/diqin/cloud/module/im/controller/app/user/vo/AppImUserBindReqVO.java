package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户 - 绑定 / 修改手机号或邮箱 Request VO
 *
 * <p>本期直接更新 im_users.mobile / email，不做验证码 / 密码二次校验（安全待评估）。
 *
 * @author dichat
 */
@Schema(description = "App - IM 用户 - 绑定/修改手机号或邮箱 Request VO")
@Data
@Accessors(chain = true)
public class AppImUserBindReqVO {

    @Schema(description = "绑定类型：mobile / email", requiredMode = Schema.RequiredMode.REQUIRED, example = "mobile")
    @NotEmpty(message = "绑定类型不能为空")
    private String type;

    @Schema(description = "绑定值（手机号 11 位 / 邮箱格式）", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @NotEmpty(message = "绑定值不能为空")
    private String value;

}
