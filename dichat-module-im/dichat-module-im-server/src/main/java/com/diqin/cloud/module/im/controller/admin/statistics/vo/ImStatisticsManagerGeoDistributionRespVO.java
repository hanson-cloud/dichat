package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 管理后台 - IM 数据看板「全球用户分布」 Response VO
 * <p>
 * 结构对齐前端 {@code CountryDetail}：国家 → 省份 → 城市 三级。
 * 数据来源于 {@code im_users.area_id} 的真实聚合（area_id 由登录后异步经 system 远程区域服务解析得到，不依赖 ip2region）；
 * 区域服务不提供经纬度，{@link CityRespVO#lat}/{@link CityRespVO#lng} 恒为 0，{@link CityRespVO#sampleIp} 为空串。
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 数据看板全球分布 Response VO")
@Data
@Accessors(chain = true)
public class ImStatisticsManagerGeoDistributionRespVO {

    @Schema(description = "国家名称（与地图 GeoJSON 一致，用于地图匹配）", example = "China")
    private String name;

    @Schema(description = "国家中文名（用于展示）", example = "中国")
    private String cnName;

    @Schema(description = "该国家在线/注册用户数", example = "12345")
    private Long users;

    @Schema(description = "已注册的省份级地图名（暂无省界 GeoJSON 时为空）", example = "")
    private String geo;

    @Schema(description = "省份级明细")
    private List<ProvinceRespVO> provinces;

    @Data
    @Accessors(chain = true)
    @Schema(description = "省份明细")
    public static class ProvinceRespVO {

        @Schema(description = "省份名称", example = "浙江省")
        private String name;

        @Schema(description = "该省份用户数", example = "3200")
        private Long users;

        @Schema(description = "城市级明细（按 area_id 聚合到城市，区域服务不提供经纬度）")
        private List<CityRespVO> cities;
    }

    @Data
    @Accessors(chain = true)
    @Schema(description = "城市明细")
    public static class CityRespVO {

        @Schema(description = "城市名称", example = "杭州市")
        private String city;

        @Schema(description = "该城市用户数", example = "1800")
        private Long users;

        @Schema(description = "纬度（区域服务不提供，恒为 0）", example = "0")
        private Double lat;

        @Schema(description = "经度（区域服务不提供，恒为 0）", example = "0")
        private Double lng;

        @Schema(description = "该城市 IP 按运营商分布")
        private List<CityIspRespVO> isps;

        @Schema(description = "代表 IP 段（区域服务不提供，恒为空串）", example = "")
        private String sampleIp;
    }

    @Data
    @Accessors(chain = true)
    @Schema(description = "城市运营商分布")
    public static class CityIspRespVO {

        @Schema(description = "运营商", example = "电信")
        private String isp;

        @Schema(description = "该运营商用户数", example = "900")
        private Long users;
    }
}
