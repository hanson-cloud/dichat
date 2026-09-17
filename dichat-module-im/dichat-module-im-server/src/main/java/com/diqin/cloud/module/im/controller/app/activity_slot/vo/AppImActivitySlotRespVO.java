package com.diqin.cloud.module.im.controller.app.activity_slot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户APP - IM 运营活动位 Response VO")
@Data
public class AppImActivitySlotRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "活动名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "夏日大促")
    private String name;

    @Schema(description = "展示位：1-发现页Banner 2-聊天列表 3-朋友圈", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer slotPosition;

    @Schema(description = "图片地址", example = "https://xxx.com/banner.png")
    private String imageUrl;

    @Schema(description = "跳转链接", example = "https://xxx.com/activity")
    private String linkUrl;

    @Schema(description = "链接类型：1-URL 2-WebView 3-APP内页", example = "1")
    private Integer linkType;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "描述", example = "首页Banner夏日促销活动")
    private String description;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

}
