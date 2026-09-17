package com.diqin.cloud.module.system.service.iplocation;

import lombok.Data;

import java.io.Serializable;

/**
 * IP 在线解析结果（ip-api.com 驱动）
 * <p>承载 ip-api 全字段（国家/省/市/区/运营商/经纬度/时区/组织/ASN 等），
 * 同时作为 Redis 缓存对象与 AreaService 区域树映射的输入；映射 area_id 的职责在 {@code AreaService}。</p>
 *
 * @author hanson
 */
@Data
public class IpLocationResult implements Serializable {

    /**
     * 客户端 IP（明文，与入参一致）
     */
    private String ip;

    /**
     * 语种（固定 zh-CN；与静态区域树 area.csv 语种一致，便于归一化匹配）
     */
    private String lang;

    /**
     * 是否解析成功（ip-api 返回 status=success 且拿到了国家）
     */
    private boolean success;

    /**
     * 国家名（zh-CN，如「中国」）
     */
    private String country;

    /**
     * 省 / 州（zh-CN，如「广东」）
     */
    private String region;

    /**
     * 城市（zh-CN，如「深圳」）
     */
    private String city;

    /**
     * 区 / 县（zh-CN，如「南山区」）
     */
    private String district;

    /**
     * 运营商（如「中国电信」），可能为空
     */
    private String isp;

    /**
     * 纬度（字符串，避免精度问题；如 "22.5431"）
     */
    private String latitude;

    /**
     * 经度（字符串）
     */
    private String longitude;

    /**
     * 国家代码（ISO 3166-1 alpha-2，如 CN）
     */
    private String countryCode;

    /**
     * 省 / 州代码（如 44；区别于 region 字段存的中文名）
     */
    private String regionCode;

    /**
     * 邮编
     */
    private String zip;

    /**
     * 时区（如 Asia/Shanghai）
     */
    private String timezone;

    /**
     * 网络归属组织
     */
    private String org;

    /**
     * ASN（如 AS4134 Chinanet）
     */
    private String asn;

}
