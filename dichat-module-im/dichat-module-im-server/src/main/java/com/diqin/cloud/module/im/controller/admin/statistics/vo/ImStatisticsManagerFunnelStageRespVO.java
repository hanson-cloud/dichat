package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理后台 - IM 数据看板「转化漏斗」阶段 Response VO
 * <p>
 * 对齐前端 {@code FunnelStage}：{@code name} 为漏斗阶段名，{@code value} 为该阶段用户/事件数。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 数据看板转化漏斗 Response VO")
@Data
@Accessors(chain = true)
public class ImStatisticsManagerFunnelStageRespVO {

    @Schema(description = "漏斗阶段名", example = "注册用户")
    private String name;

    @Schema(description = "该阶段数量", example = "12345")
    private Long value;
}
