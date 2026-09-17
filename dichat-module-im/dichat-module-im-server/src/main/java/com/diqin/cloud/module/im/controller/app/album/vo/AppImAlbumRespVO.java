package com.diqin.cloud.module.im.controller.app.album.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户APP - IM 相册 Response VO")
@Data
public class AppImAlbumRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "原图 URL")
    private String imageUrl;

    @Schema(description = "缩略图 URL")
    private String thumbUrl;

    @Schema(description = "上传时间")
    private LocalDateTime createTime;
}
