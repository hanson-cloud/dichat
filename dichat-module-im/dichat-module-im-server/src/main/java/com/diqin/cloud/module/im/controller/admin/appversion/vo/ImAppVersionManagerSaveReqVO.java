package com.diqin.cloud.module.im.controller.admin.appversion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 应用版本新增 Request VO")
@Data
public class ImAppVersionManagerSaveReqVO {

    @Schema(description = "目标平台: 1=Android 2=iOS 3=鸿蒙(HarmonyOS)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "目标平台不能为空")
    private Integer platform;

    @Schema(description = "版本号（整数，用于版本比较）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "版本号不能为空")
    private Integer versionCode;

    @Schema(description = "版本名称（如 1.2.3）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.2.3")
    @NotBlank(message = "版本名称不能为空")
    private String versionName;

    @Schema(description = "安装包下载地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://download.dichat.com/app/release/dichat-1.2.3.apk")
    @NotBlank(message = "下载地址不能为空")
    private String downloadUrl;

    @Schema(description = "更新方式: 0=普通更新 1=强制更新 2=静默更新", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "更新方式不能为空")
    private Integer updateType;

    @Schema(description = "状态: 0=下线 1=灰度中 2=全量发布 3=已归档", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "安装包大小（字节）", example = "30000000")
    private Long fileSize;

    @Schema(description = "版本更新日志")
    private String changelog;

    @Schema(description = "最低兼容版本code（低于此版本无法使用）", example = "100")
    private Integer minSupportCode;

    @Schema(description = "安装包 MD5 校验值")
    private String md5;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "下线时间")
    private LocalDateTime offlineTime;

}
