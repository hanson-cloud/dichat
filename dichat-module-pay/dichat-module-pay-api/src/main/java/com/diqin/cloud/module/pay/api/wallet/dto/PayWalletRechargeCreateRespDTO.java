package com.diqin.cloud.module.pay.api.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "钱包充值创建 Response DTO")
@Data
public class PayWalletRechargeCreateRespDTO {

    @Schema(description = "钱包充值编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "支付订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long payOrderId;

}
