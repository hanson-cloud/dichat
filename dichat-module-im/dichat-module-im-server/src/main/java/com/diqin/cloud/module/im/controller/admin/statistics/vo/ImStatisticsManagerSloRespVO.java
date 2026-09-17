package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理后台 - IM 数据看板「SLO 服务等级指标」 Response VO
 * <p>
 * 对齐前端 {@code SloItem}。{@code actual} 为基于 IM 真实数据计算的当前值；
 * {@code target} 为目标值（产品/运维目标，当前以常量形式给出并标注 TODO，后续应来自配置中心）。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 数据看板 SLO 指标 Response VO")
@Data
@Accessors(chain = true)
public class ImStatisticsManagerSloRespVO {

    @Schema(description = "指标名称", example = "消息触达率")
    private String name;

    @Schema(description = "目标值", example = "99.0")
    private Double target;

    @Schema(description = "实际值（基于真实数据统计）", example = "99.7")
    private Double actual;

    @Schema(description = "单位", example = "%")
    private String unit;
}
