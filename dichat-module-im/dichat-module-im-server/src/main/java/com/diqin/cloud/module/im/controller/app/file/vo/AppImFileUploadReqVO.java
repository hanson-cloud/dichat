package com.diqin.cloud.module.im.controller.app.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * App - IM 文件上传 Request VO
 *
 * @author hanson
 */
@Schema(description = "App - IM 文件上传 Request VO")
@Data
public class AppImFileUploadReqVO {

    @Schema(description = "文件附件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件附件不能为空")
    private MultipartFile file;

    @Schema(description = "文件目录（如 im/avatar / im/message），必须为相对路径，禁止 .. 和绝对路径",
            example = "im/message")
    @Pattern(regexp = "^[A-Za-z0-9_\\-./]+$", message = "文件目录只能包含字母、数字、下划线、连字符、点和斜杠")
    private String directory;

}
