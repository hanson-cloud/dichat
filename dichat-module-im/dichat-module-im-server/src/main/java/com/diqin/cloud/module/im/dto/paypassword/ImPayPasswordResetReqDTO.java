package com.diqin.cloud.module.im.dto.paypassword;

import com.diqin.cloud.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * APP - 重置支付密码 Request VO
 * <p>
 * 通过短信验证码校验身份后设置新支付密码（替代「忘记密码」场景）。
 *
 * @author dichat
 */
@Schema(description = "APP - 重置支付密码 Request VO")
@Data
public class ImPayPasswordResetReqDTO {

    @Schema(description = "绑定手机号", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Mobile
    @NotEmpty(message = "手机号不能为空")
    private String phone;

    @Schema(description = "短信验证码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "验证码不能为空")
    private String code;

    @Schema(description = "新支付密码（6 位数字）", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付密码不能为空")
    @Size(min = 6, max = 20, message = "支付密码长度必须在 6 - 20 之间")
    private String password;

}
