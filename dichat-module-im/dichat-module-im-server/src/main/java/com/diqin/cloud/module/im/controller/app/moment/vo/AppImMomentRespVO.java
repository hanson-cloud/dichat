package com.diqin.cloud.module.im.controller.app.moment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户APP - IM 朋友圈 Response VO")
@Data
public class AppImMomentRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "发布者编号")
    private Long userId;

    @Schema(description = "发布者昵称")
    private String nickname;

    @Schema(description = "发布者头像")
    private String avatar;

    @Schema(description = "文字内容")
    private String content;

    @Schema(description = "图片URL列表（JSON数组字符串）")
    private String images;

    @Schema(description = "可见性（0-公开 1-仅自己可见 2-部分可见 3-不给谁看）")
    private Integer visibility;

    @Schema(description = "发布位置（地名 / 地址）")
    private String location;

    @Schema(description = "提醒谁看的用户编号列表（JSON 数组字符串）")
    private String remindUserIds;

    @Schema(description = "部分可见白名单用户编号列表（JSON 数组字符串，visibility=2 时生效）")
    private String visibleUserIds;

    @Schema(description = "不给谁看黑名单用户编号列表（JSON 数组字符串，visibility=3 时生效）")
    private String invisibleUserIds;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "当前用户是否已点赞")
    private Boolean liked;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
