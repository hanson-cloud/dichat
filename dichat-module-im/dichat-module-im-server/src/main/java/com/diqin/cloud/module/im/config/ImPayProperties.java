package com.diqin.cloud.module.im.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * IM 社交支付配置
 * <p>
 * 绑定到 {@code dichat.im.pay} 命名空间（本地 application.yaml / Nacos 均可覆盖）。
 * 金额单位统一为「分」。
 *
 * @author dichat
 */
@Component
@ConfigurationProperties(prefix = "dichat.im.pay")
@Data
public class ImPayProperties {

    /**
     * 单笔转账限额，单位：分（默认 200000 = 2000 元）
     */
    private Long singleTransferLimit = 200000L;

    /**
     * 每日转账限额，单位：分（默认 2000000 = 20000 元）
     */
    private Long dailyTransferLimit = 2000000L;

    /**
     * 单笔红包限额，单位：分（默认 200000 = 2000 元）
     */
    private Long singleRedPacketLimit = 200000L;

    /**
     * 每日红包限额，单位：分（默认 2000000 = 20000 元）
     */
    private Long dailyRedPacketLimit = 2000000L;

    /**
     * 红包过期时间，单位：分钟（默认 1440 = 24 小时）
     */
    private Integer redPacketExpireMinutes = 1440;

}
