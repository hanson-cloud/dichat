package com.diqin.cloud.module.im.controller.admin.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 消息审核 Request VO（通过/驳回）")
@Data
public class ImMessageReviewManagerReviewReqVO {

    @Schema(description = "审核记录编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "审核记录编号不能为空")
    private Long id;

    @Schema(description = "审核结果：1-审核通过 2-审核驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "审核结果不能为空")
    private Integer reviewStatus;

    @Schema(description = "驳回原因（驳回时必填）", example = "包含违规图片")
    private String reviewReason;

}
