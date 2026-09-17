package com.diqin.cloud.module.im.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - IM 资金看板概览 Response VO")
@Data
public class ImStatisticsManagerMoneyOverviewRespVO {

    // ==================== 红包 ====================

    @Schema(description = "红包总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1000")
    private Long redPacketTotalCount;

    @Schema(description = "红包累计金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "5000000")
    private Long redPacketTotalAmount;

    @Schema(description = "今日红包数", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    private Long redPacketTodayCount;

    @Schema(description = "今日红包金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "600000")
    private Long redPacketTodayAmount;

    @Schema(description = "累计领取个数", requiredMode = Schema.RequiredMode.REQUIRED, example = "980")
    private Long redPacketGrabbedCount;

    @Schema(description = "累计领取金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "4900000")
    private Long redPacketGrabbedAmount;

    @Schema(description = "待领取红包数", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Long redPacketPendingCount;

    // ==================== 转账 ====================

    @Schema(description = "转账总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "2000")
    private Long transferTotalCount;

    @Schema(description = "转账累计成功金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "9000000")
    private Long transferTotalAmount;

    @Schema(description = "今日转账数", requiredMode = Schema.RequiredMode.REQUIRED, example = "240")
    private Long transferTodayCount;

    @Schema(description = "今日转账成功金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "1100000")
    private Long transferTodayAmount;

    // ==================== 提现 ====================

    @Schema(description = "提现总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    private Long withdrawTotalCount;

    @Schema(description = "提现累计金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "3000000")
    private Long withdrawTotalAmount;

    @Schema(description = "待审核提现数", requiredMode = Schema.RequiredMode.REQUIRED, example = "15")
    private Long withdrawPendingCount;

    @Schema(description = "待审核提现金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "90000")
    private Long withdrawPendingAmount;

    @Schema(description = "成功提现数", requiredMode = Schema.RequiredMode.REQUIRED, example = "470")
    private Long withdrawSuccessCount;

    @Schema(description = "成功提现金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED, example = "2820000")
    private Long withdrawSuccessAmount;

    // ==================== 银行卡 ====================

    @Schema(description = "银行卡总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "800")
    private Long bankCardTotalCount;

    @Schema(description = "正常银行卡数", requiredMode = Schema.RequiredMode.REQUIRED, example = "760")
    private Long bankCardNormalCount;

    @Schema(description = "待审核银行卡数", requiredMode = Schema.RequiredMode.REQUIRED, example = "12")
    private Long bankCardPendingCount;

}
