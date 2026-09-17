package com.diqin.cloud.module.im.controller.app.moment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户APP - IM 朋友圈发布 Request VO")
@Data
public class AppImMomentCreateReqVO {

    @Schema(description = "文字内容")
    private String content;

    @Schema(description = "图片URL列表（JSON数组）")
    private String images;

    @Schema(description = "可见性（0-公开 1-仅自己可见 2-部分可见 3-不给谁看）")
    private Integer visibility;

    @Schema(description = "发布位置（地名 / 地址）")
    private String location;

    @Schema(description = "部分可见白名单用户编号列表（visibility=2 时生效）")
    private List<Long> visibleUserIds;

    @Schema(description = "不给谁看黑名单用户编号列表（visibility=3 时生效）")
    private List<Long> invisibleUserIds;

    @Schema(description = "提醒谁看的用户编号列表")
    private List<Long> remindUserIds;
}
