package com.diqin.cloud.module.im.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 钱包充值创建 Response VO")
@Data
public class AppImWalletRechargeCreateRespVO {

    @Schema(description = "钱包充值编号", example = "1")
    private Long id;

    @Schema(description = "支付订单编号", example = "100")
    private Long payOrderId;

}
