package com.diqin.cloud.module.im.controller.admin.appversion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 应用版本 Response VO")
@Data
public class ImAppVersionManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "目标平台: 1=Android 2=iOS 3=鸿蒙(HarmonyOS)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer platform;

    @Schema(description = "版本号（整数，用于版本比较）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    private Integer versionCode;

    @Schema(description = "版本名称（如 1.2.3）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.2.3")
    private String versionName;

    @Schema(description = "安装包下载地址", example = "https://download.dichat.com/app/release/dichat-1.2.3.apk")
    private String downloadUrl;

    @Schema(description = "安装包大小（字节）", example = "30000000")
    private Long fileSize;

    @Schema(description = "更新方式: 0=普通更新 1=强制更新 2=静默更新", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer updateType;

    @Schema(description = "版本更新日志")
    private String changelog;

    @Schema(description = "最低兼容版本code（低于此版本无法使用）", example = "100")
    private Integer minSupportCode;

    @Schema(description = "安装包 MD5 校验值")
    private String md5;

    @Schema(description = "状态: 0=下线 1=灰度中 2=全量发布 3=已归档", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "下线时间")
    private LocalDateTime offlineTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
