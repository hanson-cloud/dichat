package com.diqin.cloud.module.im.controller.app.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 APP - 发起提现 Request VO")
@Data
public class AppImWithdrawCreateReqVO {

    @Schema(description = "提现银行卡编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "提现银行卡编号不能为空")
    private Long bankCardId;

    @Schema(description = "提现金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "10000")
    @NotNull(message = "提现金额不能为空")
    @Min(value = 1, message = "提现金额必须大于零")
    private Integer amount;

    @Schema(description = "提现备注", example = "工资提现")
    private String remark;

    @Schema(description = "支付密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "支付密码不能为空")
    private String payPassword;

}
