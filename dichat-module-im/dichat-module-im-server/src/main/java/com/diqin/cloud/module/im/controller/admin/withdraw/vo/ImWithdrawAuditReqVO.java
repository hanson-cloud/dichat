package com.diqin.cloud.module.im.controller.admin.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 提现审核 Request VO")
@Data
public class ImWithdrawAuditReqVO {

    @Schema(description = "提现编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "提现编号不能为空")
    private Long id;

    @Schema(description = "是否通过", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "审核结果不能为空")
    private Boolean approve;

    @Schema(description = "审核原因；拒绝时必填", example = "银行卡异常")
    private String reason;

}
