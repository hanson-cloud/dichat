package com.diqin.cloud.module.im.controller.app.favorite.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户APP - IM 收藏 Response VO")
@Data
public class AppImFavoriteRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "会话key")
    private String convKey;

    @Schema(description = "消息编号")
    private Long messageId;

    @Schema(description = "消息类型")
    private Integer messageType;

    @Schema(description = "消息内容")
    private String messageContent;

    @Schema(description = "发送者昵称")
    private String senderName;

    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
