package com.diqin.cloud.module.im.controller.admin.message.vo.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 全局消息搜索 Response VO")
@Data
public class ImMessageSearchRespVO {

    @Schema(description = "消息编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long messageId;

    @Schema(description = "会话类型：1-私聊 2-群聊", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer chatType;

    @Schema(description = "发送人编号", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long senderId;

    @Schema(description = "目标方编号（私聊=接收人ID，群聊=群ID）")
    private Long targetId;

    @Schema(description = "目标方名称（私聊=接收人昵称，群聊=群名称，由 service 回填）")
    private String targetName;

    @Schema(description = "消息类型：101=文本 102=图片 103=语音 104=视频 105=文件")
    private Integer msgType;

    @Schema(description = "消息内容（JSON）")
    private String content;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
