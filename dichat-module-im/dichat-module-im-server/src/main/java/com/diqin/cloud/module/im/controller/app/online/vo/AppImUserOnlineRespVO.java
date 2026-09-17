package com.diqin.cloud.module.im.controller.app.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户在线状态 Response VO
 */
@Schema(description = "管理后台 - IM 用户在线状态 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppImUserOnlineRespVO {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "终端类型", example = "31")
    private Integer terminal;

    @Schema(description = "是否在线", example = "true")
    private Boolean online;

}
