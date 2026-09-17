package com.diqin.cloud.module.im.dal.mysql.statistics;

import com.diqin.cloud.module.im.dal.dataobject.statistics.ImRegionAggDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * IM 数据看板 Mapper
 * <p>
 * 独立于业务 Mapper：所有统计 SQL 集中在此，仅服务于 manager 看板，不被其它业务调用，保持各业务 Mapper / Service 不受统计需求污染。
 *
 * @author hanson
 */
@Mapper
public interface ImStatisticsManagerMapper {

    String NORMAL_MESSAGE_CONDITION = "type IN (101,102,103,104,105,107,108,115,125) AND status <> 2";

    // ==================== 用户 ====================

    /**
     * 用户总数（im_users）
     */
    @Select("SELECT COUNT(*) FROM im_users WHERE deleted = 0")
    Long selectTotalUserCount();

    /**
     * 区间内新增用户数
     */
    @Select("SELECT COUNT(*) FROM im_users " +
            "WHERE deleted = 0 AND create_time >= #{beginTime} AND create_time < #{endTime}")
    Long selectNewUserCount(@Param("beginTime") LocalDateTime beginTime,
                            @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内活跃用户数：私聊或群聊发过消息的去重用户数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM (" +
            "  SELECT sender_id AS user_id FROM im_private_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            "  UNION ALL " +
            "  SELECT sender_id AS user_id FROM im_group_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            ") t")
    Long selectActiveUserCount(@Param("beginTime") LocalDateTime beginTime,
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内每日新增用户数（按天分组）
     *
     * @return [{date: "yyyy-MM-dd", count: 123}, ...]
     */
    @Select("SELECT DATE(create_time) AS date, COUNT(*) AS count FROM im_users " +
            "WHERE deleted = 0 AND create_time >= #{beginTime} AND create_time < #{endTime} " +
            "GROUP BY DATE(create_time)")
    List<Map<String, Object>> selectNewUserDailyCount(@Param("beginTime") LocalDateTime beginTime,
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内每日活跃用户数（按天分组、跨私聊+群聊去重）
     */
    @Select("SELECT day AS date, COUNT(DISTINCT user_id) AS count FROM (" +
            "  SELECT DATE(send_time) AS day, sender_id AS user_id FROM im_private_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            "  UNION ALL " +
            "  SELECT DATE(send_time) AS day, sender_id AS user_id FROM im_group_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            ") t GROUP BY day")
    List<Map<String, Object>> selectActiveUserDailyCount(@Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime);

    // ==================== 群 ====================

    /**
     * 当前有效群总数
     */
    @Select("SELECT COUNT(*) FROM im_group WHERE deleted = 0 AND status = 0")
    Long selectTotalGroupCount();

    /**
     * 区间内新建群数
     */
    @Select("SELECT COUNT(*) FROM im_group " +
            "WHERE deleted = 0 AND create_time >= #{beginTime} AND create_time < #{endTime}")
    Long selectNewGroupCount(@Param("beginTime") LocalDateTime beginTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 群规模分布（按群成员数分桶）
     *
     * @return [{range: "1-9 人", count: 123}, ...]
     */
    @Select("SELECT " +
            "  CASE " +
            "    WHEN cnt < 10 THEN '1-9 人' " +
            "    WHEN cnt < 50 THEN '10-49 人' " +
            "    WHEN cnt < 200 THEN '50-199 人' " +
            "    ELSE '200+ 人' " +
            "  END AS `range`, COUNT(*) AS count " +
            "FROM (" +
            "  SELECT g.id, COUNT(m.id) AS cnt " +
            "  FROM im_group g " +
            "  LEFT JOIN im_group_member m ON m.group_id = g.id AND m.status = 0 AND m.deleted = 0 " +
            "  WHERE g.deleted = 0 AND g.status = 0 " +
            "  GROUP BY g.id" +
            ") t " +
            "GROUP BY `range`")
    List<Map<String, Object>> selectGroupSizeDistribution();

    /**
     * 用户设备类型分布（按 device_type 分组计数）
     *
     * @return [{deviceType: 31, count: 123}, ...]；deviceType 为 null 表示从未上报设备类型
     */
    @Select("SELECT device_type AS deviceType, COUNT(*) AS count " +
            "FROM im_users WHERE deleted = 0 " +
            "GROUP BY device_type")
    List<Map<String, Object>> selectUserDeviceDistribution();

    // ==================== 消息 ====================

    /**
     * 区间内私聊消息数
     */
    @Select("SELECT COUNT(*) FROM im_private_message " +
            "WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            " AND send_time >= #{beginTime} AND send_time < #{endTime}")
    Long selectPrivateMessageCount(@Param("beginTime") LocalDateTime beginTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内群聊消息数
     */
    @Select("SELECT COUNT(*) FROM im_group_message " +
            "WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            " AND send_time >= #{beginTime} AND send_time < #{endTime}")
    Long selectGroupMessageCount(@Param("beginTime") LocalDateTime beginTime,
                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内每日私聊消息数（按天分组）
     */
    @Select("SELECT DATE(send_time) AS date, COUNT(*) AS count FROM im_private_message " +
            "WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            " AND send_time >= #{beginTime} AND send_time < #{endTime} " +
            "GROUP BY DATE(send_time)")
    List<Map<String, Object>> selectPrivateMessageDailyCount(@Param("beginTime") LocalDateTime beginTime,
                                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内每日群聊消息数（按天分组）
     */
    @Select("SELECT DATE(send_time) AS date, COUNT(*) AS count FROM im_group_message " +
            "WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            " AND send_time >= #{beginTime} AND send_time < #{endTime} " +
            "GROUP BY DATE(send_time)")
    List<Map<String, Object>> selectGroupMessageDailyCount(@Param("beginTime") LocalDateTime beginTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内消息类型分布（私聊+群聊合并）
     *
     * @return [{type: 0, count: 123}, ...]
     */
    @Select("SELECT type, COUNT(*) AS count FROM (" +
            "  SELECT type FROM im_private_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            "  UNION ALL " +
            "  SELECT type FROM im_group_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            ") t GROUP BY type")
    List<Map<String, Object>> selectMessageTypeDistribution(@Param("beginTime") LocalDateTime beginTime,
                                                            @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内 TOP 发送者（私聊+群聊合并，按消息数倒序）
     *
     * @return [{userId: 1024, messageCount: 1500}, ...]
     */
    @Select("SELECT user_id AS userId, COUNT(*) AS messageCount FROM (" +
            "  SELECT sender_id AS user_id FROM im_private_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            "  UNION ALL " +
            "  SELECT sender_id AS user_id FROM im_group_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            ") t GROUP BY user_id ORDER BY messageCount DESC LIMIT #{limit}")
    List<Map<String, Object>> selectTopSenders(@Param("beginTime") LocalDateTime beginTime,
                                               @Param("endTime") LocalDateTime endTime,
                                               @Param("limit") int limit);

    // ==================== 红包 ====================

    @Select("SELECT COUNT(*) FROM im_red_packet")
    Long selectRedPacketTotalCount();

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM im_red_packet")
    Long selectRedPacketTotalAmount();

    @Select("SELECT COUNT(*) FROM im_red_packet WHERE create_time >= #{beginTime} AND create_time < #{endTime}")
    Long selectRedPacketCountBetween(@Param("beginTime") LocalDateTime beginTime,
                                     @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM im_red_packet " +
            "WHERE create_time >= #{beginTime} AND create_time < #{endTime}")
    Long selectRedPacketAmountBetween(@Param("beginTime") LocalDateTime beginTime,
                                      @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(total_count), 0) FROM im_red_packet")
    Long selectRedPacketGrabbedCount();

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM im_red_packet")
    Long selectRedPacketGrabbedAmount();

    @Select("SELECT COUNT(*) FROM im_red_packet WHERE status = 10")
    Long selectRedPacketPendingCount();

    // ==================== 转账 ====================

    @Select("SELECT COUNT(*) FROM im_transfer")
    Long selectTransferTotalCount();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM im_transfer WHERE status = 20")
    Long selectTransferTotalAmount();

    @Select("SELECT COUNT(*) FROM im_transfer WHERE create_time >= #{beginTime} AND create_time < #{endTime}")
    Long selectTransferCountBetween(@Param("beginTime") LocalDateTime beginTime,
                                    @Param("endTime") LocalDateTime endTime);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM im_transfer " +
            "WHERE status = 20 AND create_time >= #{beginTime} AND create_time < #{endTime}")
    Long selectTransferAmountBetween(@Param("beginTime") LocalDateTime beginTime,
                                     @Param("endTime") LocalDateTime endTime);

    // ==================== 提现 ====================

    @Select("SELECT COUNT(*) FROM im_withdraw")
    Long selectWithdrawTotalCount();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM im_withdraw")
    Long selectWithdrawTotalAmount();

    @Select("SELECT COUNT(*) FROM im_withdraw WHERE status = 10")
    Long selectWithdrawPendingCount();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM im_withdraw WHERE status = 10")
    Long selectWithdrawPendingAmount();

    @Select("SELECT COUNT(*) FROM im_withdraw WHERE status = 30")
    Long selectWithdrawSuccessCount();

    @Select("SELECT COALESCE(SUM(amount), 0) FROM im_withdraw WHERE status = 30")
    Long selectWithdrawSuccessAmount();

    // ==================== 银行卡 ====================

    @Select("SELECT COUNT(*) FROM im_bank_card WHERE deleted = 0")
    Long selectBankCardTotalCount();

    @Select("SELECT COUNT(*) FROM im_bank_card WHERE deleted = 0 AND status = 20")
    Long selectBankCardNormalCount();

    @Select("SELECT COUNT(*) FROM im_bank_card WHERE deleted = 0 AND status = 10")
    Long selectBankCardPendingCount();

    // ==================== 实时 ====================

    @Select("SELECT COUNT(*) FROM im_rtc_call WHERE status IN (10, 20)")
    Long selectActiveCallCount();

    // ==================== 看板扩展：分布 / 漏斗 / 健康 ====================

    /**
     * 按区域编号 area_id 聚合用户数（看板的全球 / 区域分布）
     * <p>
     * area_id 在<b>用户登录时</b>就已由 {@code LoginAreaWritebackListener} 异步经 system 远程
     * {@code AreaApi} 解析好并回写到 {@code im_users.area_id}（见 {@code LoginAreaRecord} 注解），
     * 这里只是一次纯数据库聚合 —— 不查缓存、不调外部 IP 库、不受任何第三方额度约束，看板刷多少次都一样。
     * <p>
     * 聚合口径严格限定在当前租户内（{@code im_users} 被租户拦截器注入 {@code tenant_id}）。
     * 区域名 / 省市层级由调用方通过 {@code AreaApi#getAreaListByAreaIds} 解析，不在 SQL 内拼接。
     *
     * @return 区域聚合行，仅包含已解析出 area_id 的记录（area_id 为 NULL 的在此被过滤）
     */
    @Select("SELECT area_id AS areaId, COUNT(*) AS cnt " +
            "FROM im_users " +
            "WHERE deleted = 0 AND area_id IS NOT NULL " +
            "GROUP BY area_id")
    List<ImRegionAggDO> selectUserRegionDistribution();

    /**
     * 拥有社交关系的去重用户数（好友关系 或 当前在群内，二者并集去重）
     * <p>好友关系取 im_friend 的双方 userId；在群内取 im_group_member 未退群的成员。</p>
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM (" +
            "  SELECT user_id FROM im_friend WHERE deleted = 0" +
            "  UNION SELECT friend_user_id FROM im_friend WHERE deleted = 0" +
            "  UNION SELECT user_id FROM im_group_member WHERE deleted = 0 AND quit_time IS NULL" +
            ") t")
    Long selectUserWithSocialCount();

    /**
     * 区间内「发过消息」的去重用户数（私聊+群聊合并，排除已撤回消息）
     */
    @Select("SELECT COUNT(DISTINCT sender_id) FROM (" +
            "  SELECT sender_id FROM im_private_message " +
            "  WHERE deleted = 0 AND status <> 2 AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            "  UNION" +
            "  SELECT sender_id FROM im_group_message " +
            "  WHERE deleted = 0 AND status <> 2 AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            ") t")
    Long selectDistinctMessageSenderCount(@Param("beginTime") LocalDateTime beginTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 区间内消息状态分布（私聊+群聊合并）
     *
     * @return [{status: 0, count: 123}, ...]，用于 SLO 消息触达率计算
     */
    @Select("SELECT status, COUNT(*) AS count FROM (" +
            "  SELECT status FROM im_private_message " +
            "  WHERE deleted = 0 AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            "  UNION ALL" +
            "  SELECT status FROM im_group_message " +
            "  WHERE deleted = 0 AND send_time >= #{beginTime} AND send_time < #{endTime}" +
            ") t GROUP BY status")
    List<Map<String, Object>> selectMessageStatusDistribution(@Param("beginTime") LocalDateTime beginTime,
                                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 健康检查探针：执行一条最小查询，用于测量 DB 节点真实延迟与可用性
     */
    @Select("SELECT 1")
    Integer selectHealthCheck();

    // ==================== 看板增强：趋势数据 ====================

    /**
     * 区间内每日总消息数（私聊+群聊合并）
     */
    @Select("SELECT date, SUM(count) AS count FROM (" +
            "  SELECT DATE(send_time) AS date, COUNT(*) AS count FROM im_private_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime} GROUP BY DATE(send_time)" +
            "  UNION ALL " +
            "  SELECT DATE(send_time) AS date, COUNT(*) AS count FROM im_group_message " +
            "  WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            "  AND send_time >= #{beginTime} AND send_time < #{endTime} GROUP BY DATE(send_time)" +
            ") t GROUP BY date ORDER BY date")
    List<Map<String, Object>> selectTotalMessageDailyCount(@Param("beginTime") LocalDateTime beginTime,
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 今日已有时数据快照
     */
    @Select("SELECT COUNT(*) FROM im_private_message WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            " AND DATE(send_time) = CURDATE()")
    Long selectTodayPrivateMessageCount();

    @Select("SELECT COUNT(*) FROM im_group_message WHERE deleted = 0 AND " + NORMAL_MESSAGE_CONDITION +
            " AND DATE(send_time) = CURDATE()")
    Long selectTodayGroupMessageCount();

    @Select("SELECT COUNT(*) FROM im_message_review WHERE review_status = 0")
    Long selectPendingReviewCount();

}
