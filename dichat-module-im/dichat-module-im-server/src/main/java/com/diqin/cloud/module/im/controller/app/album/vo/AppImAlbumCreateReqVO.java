package com.diqin.cloud.module.im.controller.app.album.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "用户APP - IM 相册创建 Request VO")
@Data
public class AppImAlbumCreateReqVO {

    @Schema(description = "原图 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "原图 URL 不能为空")
    private String imageUrl;

    @Schema(description = "缩略图 URL")
    private String thumbUrl;
}
