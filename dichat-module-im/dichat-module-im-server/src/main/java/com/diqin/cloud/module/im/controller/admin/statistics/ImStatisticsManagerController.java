package com.diqin.cloud.module.im.controller.admin.statistics;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.date.LocalDateTimeUtils;
import com.diqin.cloud.module.im.controller.admin.statistics.vo.*;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.statistics.ImStatisticsManagerService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - IM 数据看板")
@RestController
@RequestMapping("/im/manager/statistics")
@Validated
public class ImStatisticsManagerController {

    /**
     * 群规模分桶名称的展示顺序
     */
    private static final List<String> GROUP_SIZE_BUCKETS = Arrays.asList("1-9 人", "10-49 人", "50-199 人", "200+ 人");
    /**
     * 看板分布默认时间窗口（天）
     */
    private static final int DISTRIBUTION_WINDOW_DAYS = 30;
    /**
     * TOP 发送者数量
     */
    private static final int TOP_SENDER_LIMIT = 10;

    @Resource
    private ImStatisticsManagerService statisticsService;
    @Resource
    private ImUserService userService;

    @GetMapping("/overview")
    @Operation(summary = "获得数据概览")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<ImStatisticsManagerOverviewRespVO> getOverview() {
        LocalDateTime todayBegin = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowBegin = todayBegin.plusDays(1);
        LocalDateTime yesterdayBegin = todayBegin.minusDays(1);
        // 周活/月活定义为 N 天滚动窗口（含今天）
        LocalDateTime weekBegin = todayBegin.minusDays(6);
        LocalDateTime monthBegin = todayBegin.minusDays(29);
        return success(new ImStatisticsManagerOverviewRespVO()
                .setTotalUser(statisticsService.getTotalUserCount())
                .setNewUserToday(statisticsService.getNewUserCount(todayBegin, tomorrowBegin))
                .setTotalGroup(statisticsService.getTotalGroupCount())
                .setNewGroupToday(statisticsService.getNewGroupCount(todayBegin, tomorrowBegin))
                .setActiveUserDaily(statisticsService.getActiveUserCount(todayBegin, tomorrowBegin))
                .setActiveUserWeekly(statisticsService.getActiveUserCount(weekBegin, tomorrowBegin))
                .setActiveUserMonthly(statisticsService.getActiveUserCount(monthBegin, tomorrowBegin))
                .setPrivateMessageToday(statisticsService.getPrivateMessageCount(todayBegin, tomorrowBegin))
                .setGroupMessageToday(statisticsService.getGroupMessageCount(todayBegin, tomorrowBegin))
                .setPrivateMessageYesterday(statisticsService.getPrivateMessageCount(yesterdayBegin, todayBegin))
                .setGroupMessageYesterday(statisticsService.getGroupMessageCount(yesterdayBegin, todayBegin)));
    }

    @GetMapping("/message-trend")
    @Operation(summary = "获得消息趋势（私聊 / 群聊双线）")
    @Parameter(name = "days", description = "回看天数（含今日）", example = "7")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<ImStatisticsManagerTrendRespVO> getMessageTrend(
            @RequestParam(value = "days", defaultValue = "7") @Min(1) @Max(90) int days) {
        List<LocalDateTime> dates = LocalDateTimeUtils.getLatestDays(days);
        LocalDateTime beginTime = dates.getFirst();
        LocalDateTime endTime = dates.get(days - 1).plusDays(1);
        Map<LocalDateTime, Long> privateMap = statisticsService.getPrivateMessageDailyCountMap(beginTime, endTime);
        Map<LocalDateTime, Long> groupMap = statisticsService.getGroupMessageDailyCountMap(beginTime, endTime);
        // 转换格式
        Map<String, List<Long>> series = new LinkedHashMap<>();
        series.put("private", alignSeries(dates, privateMap));
        series.put("group", alignSeries(dates, groupMap));
        return success(new ImStatisticsManagerTrendRespVO().setDates(dates).setSeries(series));
    }

    @GetMapping("/user-trend")
    @Operation(summary = "获得用户趋势（新增注册 / 日活双线）")
    @Parameter(name = "days", description = "回看天数（含今日）", example = "7")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<ImStatisticsManagerTrendRespVO> getUserTrend(
            @RequestParam(value = "days", defaultValue = "7") @Min(1) @Max(90) int days) {
        List<LocalDateTime> dates = LocalDateTimeUtils.getLatestDays(days);
        LocalDateTime beginTime = dates.getFirst();
        LocalDateTime endTime = dates.get(days - 1).plusDays(1);
        Map<LocalDateTime, Long> registerMap = statisticsService.getNewUserDailyCountMap(beginTime, endTime);
        Map<LocalDateTime, Long> activeMap = statisticsService.getActiveUserDailyCountMap(beginTime, endTime);
        // 转换格式
        Map<String, List<Long>> series = new LinkedHashMap<>();
        series.put("register", alignSeries(dates, registerMap));
        series.put("active", alignSeries(dates, activeMap));
        return success(new ImStatisticsManagerTrendRespVO().setDates(dates).setSeries(series));
    }

    @GetMapping("/message-type-distribution")
    @Operation(summary = "获得消息类型分布（最近 30 天）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerMessageTypeRespVO>> getMessageTypeDistribution() {
        LocalDateTime endTime = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime beginTime = endTime.minusDays(DISTRIBUTION_WINDOW_DAYS);
        Map<Integer, Long> typeCountMap = statisticsService.getMessageTypeCountMap(beginTime, endTime);
        // 转换格式
        return success(convertList(typeCountMap.entrySet(), entry -> new ImStatisticsManagerMessageTypeRespVO()
                .setType(entry.getKey()).setValue(entry.getValue())));
    }

    @GetMapping("/group-size-distribution")
    @Operation(summary = "获得群规模分布")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerGroupSizeRespVO>> getGroupSizeDistribution() {
        Map<String, Long> groupSizeMap = statisticsService.getGroupSizeCountMap();
        // 转换格式
        return success(convertList(GROUP_SIZE_BUCKETS, bucket -> new ImStatisticsManagerGroupSizeRespVO()
                .setRange(bucket).setCount(groupSizeMap.getOrDefault(bucket, 0L))));
    }

    @GetMapping("/top-senders")
    @Operation(summary = "获得消息 TOP 发送者（最近 30 天）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerTopSenderRespVO>> getTopSenders() {
        LocalDateTime endTime = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime beginTime = endTime.minusDays(DISTRIBUTION_WINDOW_DAYS);
        Map<Long, Long> topSenderMap = statisticsService.getTopSenderCountMap(beginTime, endTime, TOP_SENDER_LIMIT);
        // TOP 发送者：批量回填昵称
        Map<Long, ImUserDO> userMap = userService.getUserMap(topSenderMap.keySet());
        return success(convertList(topSenderMap.entrySet(), entry -> {
            ImStatisticsManagerTopSenderRespVO item = new ImStatisticsManagerTopSenderRespVO()
                    .setUserId(entry.getKey()).setMessageCount(entry.getValue());
            MapUtils.findAndThen(userMap, entry.getKey(), user -> item.setNickname(user.getNickname()));
            return item;
        }));
    }

    @GetMapping("/money-overview")
    @Operation(summary = "获得资金看板概览（红包/转账/提现/银行卡）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<ImStatisticsManagerMoneyOverviewRespVO> getMoneyOverview() {
        LocalDateTime todayBegin = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowBegin = todayBegin.plusDays(1);
        return success(new ImStatisticsManagerMoneyOverviewRespVO()
                .setRedPacketTotalCount(statisticsService.getRedPacketTotalCount())
                .setRedPacketTotalAmount(statisticsService.getRedPacketTotalAmount())
                .setRedPacketTodayCount(statisticsService.getRedPacketCountBetween(todayBegin, tomorrowBegin))
                .setRedPacketTodayAmount(statisticsService.getRedPacketAmountBetween(todayBegin, tomorrowBegin))
                .setRedPacketGrabbedCount(statisticsService.getRedPacketGrabbedCount())
                .setRedPacketGrabbedAmount(statisticsService.getRedPacketGrabbedAmount())
                .setRedPacketPendingCount(statisticsService.getRedPacketPendingCount())
                .setTransferTotalCount(statisticsService.getTransferTotalCount())
                .setTransferTotalAmount(statisticsService.getTransferTotalAmount())
                .setTransferTodayCount(statisticsService.getTransferCountBetween(todayBegin, tomorrowBegin))
                .setTransferTodayAmount(statisticsService.getTransferAmountBetween(todayBegin, tomorrowBegin))
                .setWithdrawTotalCount(statisticsService.getWithdrawTotalCount())
                .setWithdrawTotalAmount(statisticsService.getWithdrawTotalAmount())
                .setWithdrawPendingCount(statisticsService.getWithdrawPendingCount())
                .setWithdrawPendingAmount(statisticsService.getWithdrawPendingAmount())
                .setWithdrawSuccessCount(statisticsService.getWithdrawSuccessCount())
                .setWithdrawSuccessAmount(statisticsService.getWithdrawSuccessAmount())
                .setBankCardTotalCount(statisticsService.getBankCardTotalCount())
                .setBankCardNormalCount(statisticsService.getBankCardNormalCount())
                .setBankCardPendingCount(statisticsService.getBankCardPendingCount()));
    }

    @GetMapping("/realtime")
    @Operation(summary = "获得实时监控快照")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<ImStatisticsManagerRealtimeRespVO> getRealtime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayBegin = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrowBegin = todayBegin.plusDays(1);
        LocalDateTime fiveMinAgo = now.minusMinutes(5);
        Long todayMessageCount = statisticsService.getPrivateMessageCount(todayBegin, tomorrowBegin)
                + statisticsService.getGroupMessageCount(todayBegin, tomorrowBegin);
        return success(new ImStatisticsManagerRealtimeRespVO()
                .setOnlineCount(statisticsService.getActiveUserCount(fiveMinAgo, now))
                .setActiveCallCount(statisticsService.getActiveCallCount())
                .setTodayMessageCount(todayMessageCount)
                .setPendingWithdrawCount(statisticsService.getWithdrawPendingCount())
                .setPendingBankCardCount(statisticsService.getBankCardPendingCount())
                .setPendingRedPacketCount(statisticsService.getRedPacketPendingCount())
                .setSnapshotTime(now));
    }

    @GetMapping("/geo-distribution")
    @Operation(summary = "获得全球用户分布（国家→省份→城市，基于登录 IP 解析）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerGeoDistributionRespVO>> getGeoDistribution() {
        return success(statisticsService.getGeoDistribution());
    }

    @GetMapping("/region-distribution")
    @Operation(summary = "获得区域（省份）分布")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerNameValueRespVO>> getRegionDistribution() {
        return success(statisticsService.getRegionDistribution());
    }

    @GetMapping("/device-distribution")
    @Operation(summary = "获得设备分布（PC / 移动 / Web / Pad）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerNameValueRespVO>> getDeviceDistribution() {
        return success(statisticsService.getDeviceDistribution());
    }

    @GetMapping("/funnel")
    @Operation(summary = "获得转化漏斗（注册→月活→发消息→建关系）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerFunnelStageRespVO>> getFunnel() {
        return success(statisticsService.getFunnel());
    }

    @GetMapping("/system-health")
    @Operation(summary = "获得系统链路健康（真实探针：DB 延迟 / 消息 TPS / 实时通话并发）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerSystemHealthRespVO>> getSystemHealth() {
        return success(statisticsService.getSystemHealth());
    }

    @GetMapping("/slo")
    @Operation(summary = "获得 SLO 服务等级指标（actual 来自真实统计，target 为产品目标）")
    @PreAuthorize("@ss.hasPermission('im:manager:statistics:query')")
    public CommonResult<List<ImStatisticsManagerSloRespVO>> getSlo() {
        return success(statisticsService.getSlo());
    }

    /**
     * 把每日聚合 Map 对齐到 dates 序列；缺失天补 0
     */
    private static List<Long> alignSeries(List<LocalDateTime> dates, Map<LocalDateTime, Long> dailyMap) {
        return convertList(dates, date -> dailyMap.getOrDefault(date, 0L));
    }

}
