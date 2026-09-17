package com.diqin.cloud.module.system.framework.area;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 区域解析配置（IP → area_id）
 * <p>
 * 解析主源为 ip-api.com 在线服务（见 {@code IpLocationService}），不依赖 ip2region。
 * 对应 yaml 路径 {@code dichat.system.area.*}。
 *
 * @author hanson
 */
@ConfigurationProperties(prefix = "dichat.system.area")
@Component
@Validated
@Data
public class AreaProperties {

    /**
     * 是否启用 ip-api.com 在线解析
     * <p>关闭后 {@code getByIp} 直接返回 null，调用方保留历史 area_id、不再更新。</p>
     */
    private boolean enabled = true;

    /**
     * ip-api.com 单查端点地址前缀（实际请求为 {url}/{ip}?fields=...&lang=zh-CN）
     * <p>免费版不支持 HTTPS（SSL 属付费功能），此处只能是 http。</p>
     */
    @NotBlank
    private String url = "http://ip-api.com/json";

    /**
     * 连接超时（毫秒）
     */
    private int connectTimeoutMs = 3000;

    /**
     * 读取超时（毫秒）——解析跑在异步线程里，超时直接降级返回 null，不值得长时间占用线程
     */
    private int readTimeoutMs = 5000;

}
