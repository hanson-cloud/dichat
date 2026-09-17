package com.diqin.cloud.module.im.controller.admin.usertag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 用户标签新增 Request VO")
@Data
public class ImUserTagManagerSaveReqVO {

    @Schema(description = "标签名称（全局唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP 用户")
    @NotEmpty(message = "标签名称不能为空")
    private String name;

    @Schema(description = "标签颜色（十六进制，如 #409EFF）", example = "#409EFF")
    private String color;

    @Schema(description = "标签描述", example = "高价值付费用户")
    private String description;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

}
