package com.diqin.cloud.module.im.controller.admin.message.vo.search;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 全局消息搜索 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImMessageSearchReqVO extends PageParam {

    @Schema(description = "关键词（搜索消息内容）", example = "你好")
    private String keyword;

    @Schema(description = "消息类型：101=文本 102=图片 103=语音 104=视频 105=文件", example = "101")
    private Integer msgType;

    @Schema(description = "发送人编号", example = "225")
    private Long senderId;

    @Schema(description = "开始时间")
    private LocalDateTime beginTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

}
