package com.diqin.cloud.module.im.controller.admin.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 消息审核 Response VO")
@Data
public class ImMessageReviewManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "消息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long messageId;

    @Schema(description = "会话类型：1-私聊 2-群聊", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer chatType;

    @Schema(description = "消息类型：102-图片 103-语音 104-视频 105-文件", requiredMode = Schema.RequiredMode.REQUIRED, example = "102")
    private Integer msgType;

    @Schema(description = "发送人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "225")
    private Long senderId;

    @Schema(description = "内容预览（原始内容 JSON）")
    private String contentPreview;

    @Schema(description = "审核状态：0-待审核 1-审核通过 2-审核驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer reviewStatus;

    @Schema(description = "审核人编号", example = "1")
    private Long reviewerId;

    @Schema(description = "驳回原因", example = "包含违规内容")
    private String reviewReason;

    @Schema(description = "审核时间")
    private LocalDateTime reviewTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
