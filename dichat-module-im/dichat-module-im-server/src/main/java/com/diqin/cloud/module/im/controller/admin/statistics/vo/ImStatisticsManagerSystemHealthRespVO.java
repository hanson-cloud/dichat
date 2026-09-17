package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 管理后台 - IM 数据看板「系统链路健康」节点 Response VO
 * <p>
 * 对齐前端 {@code ChainNode}。字段均为真实探测/聚合结果：
 * <ul>
 *     <li>{@code status}：ok / warn / error，由真实探针（DB 延迟、消息 TPS 等）结合阈值得出</li>
 *     <li>{@code tps}：真实消息吞吐或实时通话并发</li>
 *     <li>{@code latency}：真实探针耗时（毫秒）</li>
 *     <li>{@code lag}：暂无消息积压监控源，恒为 0（待接入监控后补全）</li>
 * </ul>
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 数据看板系统链路健康 Response VO")
@Data
@Accessors(chain = true)
public class ImStatisticsManagerSystemHealthRespVO {

    @Schema(description = "链路节点名称", example = "消息服务")
    private String name;

    @Schema(description = "节点状态：ok / warn / error", example = "ok")
    private String status;

    @Schema(description = "吞吐量（消息 TPS 或实时通话并发路数）", example = "120")
    private Long tps;

    @Schema(description = "探针耗时（毫秒）", example = "12")
    private Long latency;

    @Schema(description = "消息积压延迟（暂无监控源，恒为 0）", example = "0")
    private Long lag;
}
