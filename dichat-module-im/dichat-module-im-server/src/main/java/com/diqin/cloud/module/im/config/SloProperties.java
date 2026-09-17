package com.diqin.cloud.module.im.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * IM 数据看板 SLO 指标「目标值」配置
 * <p>
 * 绑定到 {@code dichat.im.slo} 命名空间（本地 application.yaml / Nacos 均可覆盖）。
 * 各目标值为产品 / 运维给定的服务等级目标（SLO），用于看板展示「达标 / 未达标」。
 * 对应指标的 actual（实际值）仍为基于真实数据的统计值，见
 * {@code ImStatisticsManagerService#getSlo()}。
 * <p>
 * 默认值即推荐的初始 SLO，上线后可由运维在 Nacos（data-id:
 * {@code dichat-module-im-server-{profile}.yaml}）按业务实际情况调整，无需改代码。
 *
 * @author hanson
 */
@Component
@ConfigurationProperties(prefix = "dichat.im.slo")
@Data
public class SloProperties {

    /**
     * 消息触达率目标，单位：%（默认 99.0）
     * <p>
     * 触达定义：UNREAD(0) / READ(3) 视为已触达，RECALL(2) 视为撤回；二者之和记为总量。
     */
    private Double messageReachRate = 99.0;

    /**
     * 月活跃用户目标，单位：人（默认 50000）
     * <p>
     * 统计窗口与漏斗一致（{@code FUNNEL_WINDOW_DAYS} 天）。
     */
    private Double monthlyActiveUser = 50000.0;

    /**
     * 实时通话并发目标，单位：路（默认 200）
     * <p>
     * 取 RTC 进行中通话数（创建 / 接通未结束）。
     */
    private Double realtimeCallConcurrency = 200.0;

    /**
     * 消息吞吐目标，单位：条/秒（默认 100）
     * <p>
     * 取近 1 分钟消息总量 / 60 得到的实时 TPS 估算值。
     */
    private Double messageThroughput = 100.0;

}
