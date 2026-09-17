package com.diqin.cloud.module.im.controller.app.appversion.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "用户 APP - 应用版本检测 Response VO")
@Data
@Accessors(chain = true)
public class AppImAppVersionCheckRespVO {

    @Schema(description = "是否有新版本可更新", example = "true")
    private Boolean hasUpdate;

    @Schema(description = "最新版本号，如 1.0.0", example = "1.0.0")
    private String latestVersion;

    @Schema(description = "新版本下载地址（apk / App Store / 应用宝 等），已按 platform 选择平台专属地址，缺失时回退通用地址",
            example = "https://download.dichat.com/app/release/dichat-1.0.0.apk")
    private String downloadUrl;

    @Schema(description = "是否强制更新（客户端版本低于最低可运行版本时为 true）", example = "false")
    private Boolean force;

    @Schema(description = "更新说明", example = "修复已知问题，优化消息收发起步速度")
    private String description;

    @Schema(description = "安装包大小，如 28.6 MB", example = "28.6 MB")
    private String size;

}
