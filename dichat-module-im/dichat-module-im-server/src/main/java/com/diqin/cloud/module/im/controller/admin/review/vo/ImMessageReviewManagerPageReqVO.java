package com.diqin.cloud.module.im.controller.admin.review.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 消息审核分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImMessageReviewManagerPageReqVO extends PageParam {

    @Schema(description = "审核状态：0-待审核 1-审核通过 2-审核驳回", example = "0")
    private Integer reviewStatus;

    @Schema(description = "消息类型：102-图片 103-语音 104-视频 105-文件", example = "102")
    private Integer msgType;

    @Schema(description = "会话类型：1-私聊 2-群聊", example = "1")
    private Integer chatType;

    @Schema(description = "发送人编号", example = "225")
    private Long senderId;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
