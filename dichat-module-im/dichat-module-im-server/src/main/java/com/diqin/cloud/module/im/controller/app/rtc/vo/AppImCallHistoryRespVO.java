package com.diqin.cloud.module.im.controller.app.rtc.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户APP - IM 通话记录 Response VO")
@Data
public class AppImCallHistoryRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "业务通话编号")
    private String room;

    @Schema(description = "会话类型（1-私聊 2-群聊）")
    private Integer conversationType;

    @Schema(description = "媒体类型（1-音频 2-视频）")
    private Integer mediaType;

    @Schema(description = "发起人编号")
    private Long inviterUserId;

    @Schema(description = "群编号（私聊为null）")
    private Long groupId;

    @Schema(description = "通话状态")
    private Integer status;

    @Schema(description = "结束原因")
    private Integer endReason;

    @Schema(description = "发起时间")
    private LocalDateTime startTime;

    @Schema(description = "接通时间")
    private LocalDateTime acceptTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
}
