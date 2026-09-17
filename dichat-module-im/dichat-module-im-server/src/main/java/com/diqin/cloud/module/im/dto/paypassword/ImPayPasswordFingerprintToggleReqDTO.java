package com.diqin.cloud.module.im.dto.paypassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * APP - 开关指纹支付 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 开关指纹支付 Request VO")
@Data
public class ImPayPasswordFingerprintToggleReqDTO {

    @Schema(description = "是否开启：true=开启 false=关闭", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开关状态不能为空")
    private Boolean enabled;

}
