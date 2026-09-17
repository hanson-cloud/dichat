package com.diqin.cloud.module.im.controller.admin.payment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 收付款收款码 Response VO")
@Data
public class ImPaymentQrManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户编号（收款人）", example = "1024")
    private Long userId;

    @Schema(description = "收款码", example = "PAYABC123DEF456")
    private String code;

    @Schema(description = "类型：1-收款码", example = "1")
    private Integer type;

    @Schema(description = "收款金额（分，null 表示不限定金额）", example = "1000")
    private Long amount;

    @Schema(description = "收款备注", example = "餐费")
    private String remark;

    @Schema(description = "状态：0-有效 1-已使用 2-已作废", example = "0")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
