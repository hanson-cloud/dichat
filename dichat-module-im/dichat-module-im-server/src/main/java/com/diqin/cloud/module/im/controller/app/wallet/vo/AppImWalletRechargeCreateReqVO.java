package com.diqin.cloud.module.im.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 发起钱包充值 Request VO")
@Data
public class AppImWalletRechargeCreateReqVO {

    @Schema(description = "支付金额，单位：分", example = "1000")
    private Integer payPrice;

    @Schema(description = "充值套餐编号", example = "1024")
    private Long packageId;

}
