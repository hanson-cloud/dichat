package com.diqin.cloud.module.pay.api.wallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 钱包充值记录 Response DTO（RPC）
 *
 * @author dichat
 */
@Schema(description = "钱包充值记录 Response DTO")
@Data
public class PayWalletRechargeRespDTO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "钱包编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long walletId;

    @Schema(description = "用户编号（运行时根据 walletId 关联补充）", example = "1024")
    private Long userId;

    @Schema(description = "用户实际到账余额，单位分", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer totalPrice;

    @Schema(description = "实际支付金额，单位分", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer payPrice;

    @Schema(description = "钱包赠送金额，单位分", requiredMode = Schema.RequiredMode.REQUIRED, example = "80")
    private Integer bonusPrice;

    @Schema(description = "支付成功的支付渠道", requiredMode = Schema.RequiredMode.REQUIRED)
    private String payChannelCode;

    @Schema(description = "支付渠道名", example = "微信小程序支付")
    private String payChannelName;

    @Schema(description = "支付订单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long payOrderId;

    @Schema(description = "支付订单外部订单号", example = "4200001234567890")
    private String payOrderChannelOrderNo;

    @Schema(description = "是否已支付", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean payStatus;

    @Schema(description = "支付时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime payTime;

    @Schema(description = "退款状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer refundStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
