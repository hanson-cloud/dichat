package com.diqin.cloud.module.im.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 视频上传 Response VO
 * <p>
 * 上传视频时由服务端用 FFmpeg 抽取首帧生成封面图，与视频地址一并返回。
 * 封面生成失败（ffmpeg 缺失 / 抽帧异常）时 {@link #thumbnail} 为 null，前端自动回退为播放键蒙层。
 *
 * @author dichat
 */
@Schema(description = "App - IM 视频上传 Response VO")
@Data
@Accessors(chain = true)
public class AppImVideoUploadRespVO {

    @Schema(description = "视频访问 URL", example = "https://www.diqin.com/im/video/2026/07/28/clip.mp4")
    private String url;

    @Schema(description = "视频封面（首帧）访问 URL，生成失败为 null", example = "https://www.diqin.com/im/video/cover/2026/07/28/clip_cover.jpg")
    private String thumbnail;

    @Schema(description = "原始文件名", example = "clip.mp4")
    private String name;

    @Schema(description = "文件大小（字节）", example = "2048000")
    private Long size;

    @Schema(description = "文件 MIME 类型", example = "video/mp4")
    private String contentType;

    @Schema(description = "视频原始宽度（像素），ffprobe 成功时返回", example = "1280")
    private Integer width;

    @Schema(description = "视频原始高度（像素），ffprobe 成功时返回", example = "720")
    private Integer height;

    @Schema(description = "视频时长（毫秒），ffprobe 成功时返回", example = "12345")
    private Long duration;

}
