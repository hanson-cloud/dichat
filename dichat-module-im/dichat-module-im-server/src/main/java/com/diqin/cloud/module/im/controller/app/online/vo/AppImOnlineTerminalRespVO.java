package com.diqin.cloud.module.im.controller.app.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户在线终端 Response VO
 */
@Schema(description = "管理后台 - IM 用户在线终端 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppImOnlineTerminalRespVO {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "在线终端列表；空列表表示不在线", example = "[10, 31]")
    private List<Integer> terminals;

}
