package com.diqin.cloud.module.im.controller.app.favorite.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户APP - IM 收藏创建 Request VO")
@Data
public class AppImFavoriteCreateReqVO {

    @Schema(description = "会话key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private String convKey;

    @Schema(description = "消息编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Long messageId;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private Integer messageType;

    @Schema(description = "消息内容")
    private String messageContent;

    @Schema(description = "发送者昵称")
    private String senderName;
}
