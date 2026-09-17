package com.diqin.cloud.module.im.dto.paypassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * APP - 验证手势密码 Request VO（支付时调用）
 *
 * @author dichat
 */
@Schema(description = "APP - 验证手势密码 Request VO")
@Data
public class ImPayPasswordGestureVerifyReqDTO {

    @Schema(description = "待验证的手势图案 SHA-256 哈希", example = "a1b2c3...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手势图案哈希不能为空")
    @Size(max = 128, message = "手势图案哈希长度异常")
    private String patternHash;

}
