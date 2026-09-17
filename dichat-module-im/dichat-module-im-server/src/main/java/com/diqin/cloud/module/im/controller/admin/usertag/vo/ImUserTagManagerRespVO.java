package com.diqin.cloud.module.im.controller.admin.usertag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 用户标签 Response VO")
@Data
public class ImUserTagManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP 用户")
    private String name;

    @Schema(description = "标签颜色（十六进制）", example = "#409EFF")
    private String color;

    @Schema(description = "标签描述", example = "高价值付费用户")
    private String description;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "已打标用户数", example = "12")
    private Long taggedUserCount;

    @Schema(description = "创建者", example = "admin")
    private String creator;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
