package com.diqin.cloud.module.im.controller.app.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户APP - IM 收款码创建 Request VO")
@Data
public class AppImPaymentQrCreateReqVO {

    @Schema(description = "收款金额（分，不传表示不限定金额）", example = "1000")
    private Long amount;

    @Schema(description = "收款备注", example = "餐费")
    private String remark;
}
