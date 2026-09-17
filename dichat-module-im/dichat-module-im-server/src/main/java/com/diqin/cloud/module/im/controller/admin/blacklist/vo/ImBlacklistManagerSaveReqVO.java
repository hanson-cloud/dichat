package com.diqin.cloud.module.im.controller.admin.blacklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 全局黑名单新增 Request VO")
@Data
public class ImBlacklistManagerSaveReqVO {

    @Schema(description = "操作用户编号（谁拉的黑）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "操作用户编号不能为空")
    private Long userId;

    @Schema(description = "被拉黑用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "被拉黑用户编号不能为空")
    private Long blockId;

    @Schema(description = "拉黑原因", example = "骚扰谩骂")
    @NotEmpty(message = "拉黑原因不能为空")
    private String reason;

}
