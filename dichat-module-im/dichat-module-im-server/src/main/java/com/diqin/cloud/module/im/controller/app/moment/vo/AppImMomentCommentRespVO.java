package com.diqin.cloud.module.im.controller.app.moment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户APP - IM 朋友圈评论 Response VO")
@Data
public class AppImMomentCommentRespVO {

    @Schema(description = "评论编号")
    private Long id;

    @Schema(description = "朋友圈编号")
    private Long momentId;

    @Schema(description = "评论者用户编号")
    private Long userId;

    @Schema(description = "评论者昵称")
    private String nickname;

    @Schema(description = "评论者头像")
    private String avatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "被回复用户编号（回复某条评论时填充）")
    private Long replyUserId;

    @Schema(description = "被回复用户昵称（便于前端直接展示\"回复 @昵称\"）")
    private String replyNickname;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
