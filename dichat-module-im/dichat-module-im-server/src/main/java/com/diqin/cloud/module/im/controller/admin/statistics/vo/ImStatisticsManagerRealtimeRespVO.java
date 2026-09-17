package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 实时监控快照 Response VO")
@Data
public class ImStatisticsManagerRealtimeRespVO {

    @Schema(description = "在线用户数（近 5 分钟活跃用户代理）", requiredMode = Schema.RequiredMode.REQUIRED, example = "320")
    private Long onlineCount;

    @Schema(description = "进行中通话数（创建/接通未结束）", requiredMode = Schema.RequiredMode.REQUIRED, example = "8")
    private Long activeCallCount;

    @Schema(description = "今日消息总数（私聊+群聊）", requiredMode = Schema.RequiredMode.REQUIRED, example = "120000")
    private Long todayMessageCount;

    @Schema(description = "待审核提现数", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    private Long pendingWithdrawCount;

    @Schema(description = "待审核银行卡数", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Long pendingBankCardCount;

    @Schema(description = "待领取红包数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long pendingRedPacketCount;

    @Schema(description = "快照时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime snapshotTime;

}
