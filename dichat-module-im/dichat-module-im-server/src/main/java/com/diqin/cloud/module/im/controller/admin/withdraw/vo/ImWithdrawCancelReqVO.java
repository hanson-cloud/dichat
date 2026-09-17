package com.diqin.cloud.module.im.controller.admin.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 提现撤销 Request VO")
@Data
public class ImWithdrawCancelReqVO {

    @Schema(description = "提现编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "提现编号不能为空")
    private Long id;

    @Schema(description = "撤销原因", example = "用户主动撤销")
    private String reason;
}
