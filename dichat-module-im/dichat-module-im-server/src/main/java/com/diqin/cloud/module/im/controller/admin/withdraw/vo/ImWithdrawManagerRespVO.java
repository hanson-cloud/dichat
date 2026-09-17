package com.diqin.cloud.module.im.controller.admin.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 提现 Response VO")
@Data
public class ImWithdrawManagerRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "提现单号", example = "uuid-xxx")
    private String no;

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "提现银行卡编号", example = "1024")
    private Long bankCardId;

    @Schema(description = "银行名称（快照）", example = "招商银行")
    private String bankName;

    @Schema(description = "脱敏卡号（快照）", example = "****1234")
    private String cardNoMask;

    @Schema(description = "提现金额，单位：分", example = "10000")
    private Integer amount;

    @Schema(description = "状态", example = "10")
    private Integer status;

    @Schema(description = "备注", example = "工资提现")
    private String remark;

    @Schema(description = "审核人用户编号", example = "2048")
    private Long auditUserId;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核原因")
    private String auditReason;

    @Schema(description = "创建时间（申请时间）")
    private LocalDateTime createTime;

}
