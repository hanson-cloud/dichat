package com.diqin.cloud.module.im.dto.bankcard;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * APP - 设置默认银行卡 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 设置默认银行卡 Request VO")
@Data
public class ImBankCardSetDefaultReqDTO {

    @Schema(description = "银行卡编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "银行卡编号不能为空")
    private Long id;

}
