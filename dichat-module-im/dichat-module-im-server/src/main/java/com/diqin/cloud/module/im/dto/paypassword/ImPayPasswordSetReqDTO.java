package com.diqin.cloud.module.im.dto.paypassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * APP - 设置支付密码 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 设置支付密码 Request VO")
@Data
public class ImPayPasswordSetReqDTO {

    @Schema(description = "支付密码（6-20 位）", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付密码不能为空")
    @Size(min = 6, max = 20, message = "支付密码长度必须在 6 - 20 之间")
    private String password;

}
