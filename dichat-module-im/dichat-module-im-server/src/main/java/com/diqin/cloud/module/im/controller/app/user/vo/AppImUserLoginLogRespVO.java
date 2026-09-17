package com.diqin.cloud.module.im.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * App - 我的登录设备 Response VO
 *
 * @author hanson
 */
@Schema(description = "App - 我的登录设备 Response VO")
@Data
public class AppImUserLoginLogRespVO {

    @Schema(description = "登录日志编号（踢下线时作为 terminalUserId 传入）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "终端类型（TerminalEnum）：0-未知 10-微信小程序 11-微信公众号 20-H5 31-手机App", example = "31")
    private Integer terminal;

    @Schema(description = "浏览器 / 设备 UA")
    private String userAgent;

    @Schema(description = "登录 IP", example = "127.0.0.1")
    private String loginIp;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime loginTime;

    @Schema(description = "登出时间（主动 / 强制）")
    private LocalDateTime logoutTime;

    @Schema(description = "状态：1-在线 2-已强制下线", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

}
