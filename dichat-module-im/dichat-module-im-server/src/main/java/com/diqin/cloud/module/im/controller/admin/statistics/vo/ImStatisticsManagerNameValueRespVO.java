package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理后台 - IM 数据看板「名称-数值」通用 Response VO
 * <p>
 * 用于「设备分布」「区域分布」等需要 {名称, 数值} 二元组的端点，对齐前端 {@code NameValue}。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 数据看板名称-数值 Response VO")
@Data
@Accessors(chain = true)
public class ImStatisticsManagerNameValueRespVO {

    @Schema(description = "名称（设备类型 / 省份等）", example = "浙江省")
    private String name;

    @Schema(description = "数值（用户数）", example = "3200")
    private Long value;
}
