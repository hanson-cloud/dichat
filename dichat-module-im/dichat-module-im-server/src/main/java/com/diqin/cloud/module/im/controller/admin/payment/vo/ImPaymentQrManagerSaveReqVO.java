package com.diqin.cloud.module.im.controller.admin.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 收付款收款码创建 Request VO")
@Data
public class ImPaymentQrManagerSaveReqVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "收款金额（分，不传表示不限定金额）", example = "1000")
    private Long amount;

    @Schema(description = "收款备注", example = "餐费")
    private String remark;
}
