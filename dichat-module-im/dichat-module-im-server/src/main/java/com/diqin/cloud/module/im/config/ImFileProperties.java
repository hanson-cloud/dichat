package com.diqin.cloud.module.im.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * IM 文件上传配置
 * <p>
 * 绑定到 {@code dichat.im.file} 命名空间（本地 application.yaml / Nacos 均可覆盖）。
 * 大小阈值单位统一为「字节」，缩略图尺寸单位统一为「像素」。
 *
 * @author dichat
 */
@Component
@ConfigurationProperties(prefix = "dichat.im.file")
@Data
public class ImFileProperties {

    /**
     * 单文件大小上限，单位：字节（默认 10 MB）
     */
    private Long maxFileSize = 10L * 1024 * 1024;

    /**
     * 缩略图默认最大边长，单位：像素（默认 200）
     */
    private Integer defaultThumbSize = 200;

    /**
     * 缩略图最大边长上限，单位：像素（默认 1000），用于防止前端传入过大的目标尺寸
     */
    private Integer maxThumbSize = 1000;

    /**
     * 视频封面（首帧）最大宽度，单位：像素（默认 480）。抽帧后按比例缩放，宽度不超过该值。
     */
    private Integer defaultVideoCoverWidth = 480;

    /**
     * ffmpeg 可执行文件路径。默认走系统 PATH（填 null / 空即直接调用 ffmpeg）；
     * 生产环境若 ffmpeg 不在 PATH 中，请配置绝对路径（如 /usr/bin/ffmpeg）。
     */
    private String ffmpegPath;

    /**
     * ffprobe 可执行文件路径。默认走系统 PATH；用于抽取视频真实宽高与时长（可选，失败不影响封面生成）。
     */
    private String ffprobePath;

}
