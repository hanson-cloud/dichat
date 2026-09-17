package com.diqin.cloud.module.pay.api.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Objects;

@Schema(description = "钱包充值创建 Request DTO")
@Data
public class PayWalletRechargeCreateReqDTO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    @Schema(description = "客户端 IP（RPC 调用可传空）", example = "127.0.0.1")
    private String userIp;

    @Schema(description = "支付金额，单位：分", example = "1000")
    private Integer payPrice;

    @Schema(description = "充值套餐编号", example = "1024")
    private Long packageId;

    @AssertTrue(message = "充值金额和充值套餐不能同时为空")
    public boolean isValidPayPriceAndPackageId() {
        return Objects.nonNull(payPrice) || Objects.nonNull(packageId);
    }

}
