package com.diqin.cloud.module.im.dto.paypassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * APP - 校验支付密码 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 校验支付密码 Request VO")
@Data
public class ImPayPasswordVerifyReqDTO {

    @Schema(description = "支付密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付密码不能为空")
    private String password;

}
