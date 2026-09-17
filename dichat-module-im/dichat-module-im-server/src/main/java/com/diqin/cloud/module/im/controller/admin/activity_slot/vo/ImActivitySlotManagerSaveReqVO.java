package com.diqin.cloud.module.im.controller.admin.activity_slot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 运营活动位新增 Request VO")
@Data
public class ImActivitySlotManagerSaveReqVO {

    @Schema(description = "活动名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夏日大促")
    @NotBlank(message = "活动名称不能为空")
    private String name;

    @Schema(description = "展示位：1-发现页Banner 2-聊天列表 3-朋友圈", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "展示位不能为空")
    private Integer slotPosition;

    @Schema(description = "图片地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://xxx.com/banner.png")
    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    @Schema(description = "跳转链接", example = "https://xxx.com/activity")
    private String linkUrl;

    @Schema(description = "链接类型：1-URL 2-WebView 3-APP内页", example = "1")
    private Integer linkType;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "描述", example = "首页Banner夏日促销活动")
    private String description;

}
