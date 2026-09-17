package com.diqin.cloud.module.im.service.statistics;

import com.diqin.cloud.module.im.controller.admin.statistics.vo.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * IM 数据看板 Service 接口
 * <p>
 * 仅服务于 manager 后台统计页，独立于业务 Service，避免污染。
 * 返回的均为聚合后的简单结构，由 Controller 负责 VO 装配与昵称回填。
 *
 * @author hanson
 */
public interface ImStatisticsManagerService {

    // ==================== 用户 ====================

    /**
     * 获取用户总数
     */
    Long getTotalUserCount();

    /**
     * 获取区间内新增用户数
     */
    Long getNewUserCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内活跃用户数（私聊+群聊去重）
     */
    Long getActiveUserCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内每日新增用户数 Map（key 为 yyyy-MM-dd 日期）
     */
    Map<LocalDateTime, Long> getNewUserDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内每日活跃用户数 Map
     */
    Map<LocalDateTime, Long> getActiveUserDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime);

    // ==================== 群 ====================

    /**
     * 获取群总数
     */
    Long getTotalGroupCount();

    /**
     * 获取区间内新建群数
     */
    Long getNewGroupCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取群规模分布 Map（key 为分桶名）
     */
    Map<String, Long> getGroupSizeCountMap();

    // ==================== 消息 ====================

    /**
     * 获取区间内私聊消息数
     */
    Long getPrivateMessageCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内群聊消息数
     */
    Long getGroupMessageCount(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内每日私聊消息数 Map
     */
    Map<LocalDateTime, Long> getPrivateMessageDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内每日群聊消息数 Map
     */
    Map<LocalDateTime, Long> getGroupMessageDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内消息类型分布 Map（key 为消息类型）
     */
    Map<Integer, Long> getMessageTypeCountMap(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 获取区间内 TOP 发送者 Map（key 为 userId，value 为消息数；按消息数倒序）
     */
    Map<Long, Long> getTopSenderCountMap(LocalDateTime beginTime, LocalDateTime endTime, int limit);

    // ==================== 红包 ====================

    Long getRedPacketTotalCount();

    Long getRedPacketTotalAmount();

    Long getRedPacketCountBetween(LocalDateTime beginTime, LocalDateTime endTime);

    Long getRedPacketAmountBetween(LocalDateTime beginTime, LocalDateTime endTime);

    Long getRedPacketGrabbedCount();

    Long getRedPacketGrabbedAmount();

    Long getRedPacketPendingCount();

    // ==================== 转账 ====================

    Long getTransferTotalCount();

    Long getTransferTotalAmount();

    Long getTransferCountBetween(LocalDateTime beginTime, LocalDateTime endTime);

    Long getTransferAmountBetween(LocalDateTime beginTime, LocalDateTime endTime);

    // ==================== 提现 ====================

    Long getWithdrawTotalCount();

    Long getWithdrawTotalAmount();

    Long getWithdrawPendingCount();

    Long getWithdrawPendingAmount();

    Long getWithdrawSuccessCount();

    Long getWithdrawSuccessAmount();

    // ==================== 银行卡 ====================

    Long getBankCardTotalCount();

    Long getBankCardNormalCount();

    Long getBankCardPendingCount();

    // ==================== 实时 ====================

    /**
     * 进行中通话数（创建 / 接通未结束）
     */
    Long getActiveCallCount();

    // ==================== 看板扩展：分布 / 漏斗 / 健康 ====================

    /**
     * 全球用户分布（国家 → 省份 → 城市 三级）
     * <p>基于 im_users.area_id 聚合（area_id 由登录后异步经 system 远程区域服务解析得到，不依赖 ip2region）；
     * 无任何区域数据的用户不计入分布。</p>
     */
    List<ImStatisticsManagerGeoDistributionRespVO> getGeoDistribution();

    /**
     * 区域（省份）分布
     * <p>基于 im_users.area_id 聚合到省份（无省份则按国家），区域名经 system 远程区域服务解析。</p>
     */
    List<ImStatisticsManagerNameValueRespVO> getRegionDistribution();

    /**
     * 设备（PC / 移动 / Web / Pad）分布
     * <p><b>当前无数据源</b>：im_users 未存设备类型字段、且 IP 无法推导设备。
     * 返回空列表，待补充设备类型采集（客户端登录上报）后实现真实统计。</p>
     */
    List<ImStatisticsManagerNameValueRespVO> getDeviceDistribution();

    /**
     * 转化漏斗（注册 → 月活 → 发消息 → 建关系）
     * <p>各阶段均为基于 IM 真实数据的去重计数，非单调约束（如实反映）。</p>
     */
    List<ImStatisticsManagerFunnelStageRespVO> getFunnel();

    /**
     * 系统链路健康（真实探针：DB 延迟、消息 TPS、实时通话并发等）
     */
    List<ImStatisticsManagerSystemHealthRespVO> getSystemHealth();

    /**
     * SLO 服务等级指标（actual 来自真实统计，target 为产品目标常量，后续应来自配置中心）
     */
    List<ImStatisticsManagerSloRespVO> getSlo();

}
