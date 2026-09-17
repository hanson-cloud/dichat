package com.diqin.cloud.module.im.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * APP - 发起转账 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 发起转账 Request VO")
@Data
public class ImTransferReqDTO {

    @Schema(description = "收款方用户编号", example = "2048", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "收款方用户编号不能为空")
    private Long toUserId;

    @Schema(description = "转账金额，单位：分（必须 > 0）", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "转账金额不能为空")
    @Positive(message = "转账金额必须大于 0")
    private Long amount;

    @Schema(description = "备注", example = "借你钱")
    private String remark;

    @Schema(description = "支付密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付密码不能为空")
    private String payPassword;

}
