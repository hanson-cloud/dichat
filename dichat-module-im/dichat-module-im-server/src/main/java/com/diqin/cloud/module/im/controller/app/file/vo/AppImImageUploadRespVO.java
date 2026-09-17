package com.diqin.cloud.module.im.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 图片上传 Response VO
 *
 * @author hanson
 */
@Schema(description = "App - IM 图片上传 Response VO")
@Data
@Accessors(chain = true)
public class AppImImageUploadRespVO {

    @Schema(description = "原图访问 URL", example = "https://www.diqin.com/im/image/2026/06/12/original.png")
    private String url;

    @Schema(description = "缩略图访问 URL", example = "https://www.diqin.com/im/image/2026/06/12/thumb.png")
    private String thumbUrl;

    @Schema(description = "原始文件名", example = "screenshot.png")
    private String name;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long size;

    @Schema(description = "文件 MIME 类型", example = "image/png")
    private String contentType;

}
