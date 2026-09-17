package com.diqin.cloud.module.system.service.iplocation;

/**
 * IP 在线定位服务（ip-api.com，不使用 ip2region）
 *
 * @author hanson
 */
public interface IpLocationService {

    /**
     * 解析单个 IP
     *
     * @param ip 待解析的公网 IP（调用方需已排除内网 / 保留地址）
     * @return 解析结果；任何失败情形均返回 null，由上层决定降级策略
     */
    IpLocationResult getByIp(String ip);

}
