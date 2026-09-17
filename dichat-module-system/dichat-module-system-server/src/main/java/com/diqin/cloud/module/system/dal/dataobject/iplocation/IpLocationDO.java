package com.diqin.cloud.module.system.dal.dataobject.iplocation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.mybatis.core.dataobject.BaseDO;
import com.diqin.cloud.framework.tenant.core.aop.TenantIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * IP 定位缓存表（L2 持久化，全局共享）
 * <p>复用策略：本地缓存(Redis)优先，未命中再查本表，仍无则调 ip-api.com 查询并落库，供二次查询。</p>
 *
 * @author hanson
 */
@TableName("system_ip_location")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TenantIgnore
public class IpLocationDO extends BaseDO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 客户端 IP（明文）
     */
    private String ip;

    /**
     * 语种（固定 zh-CN；与静态区域树语种一致）
     */
    private String lang;

    /**
     * 查询状态：1=成功，0=失败（失败也落库做负缓存，避免对同一个坏 IP 反复外调）
     */
    private Integer status;

    private String country;
    private String region;
    private String city;
    private String district;
    private String isp;
    private String latitude;
    private String longitude;
    private String countryCode;
    private String regionCode;
    private String zip;
    private String timezone;
    private String org;
    private String asn;

}
