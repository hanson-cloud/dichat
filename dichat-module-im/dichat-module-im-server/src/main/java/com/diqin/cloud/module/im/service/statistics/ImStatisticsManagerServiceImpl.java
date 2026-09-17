package com.diqin.cloud.module.im.service.statistics;

import cn.hutool.core.convert.Convert;
import com.diqin.cloud.framework.common.enums.TerminalEnum;
import com.diqin.cloud.module.im.config.SloProperties;
import com.diqin.cloud.module.im.controller.admin.statistics.vo.*;
import com.diqin.cloud.module.im.dal.dataobject.statistics.ImRegionAggDO;
import com.diqin.cloud.module.im.dal.mysql.statistics.ImStatisticsManagerMapper;
import com.diqin.cloud.module.system.api.area.AreaApi;
import com.diqin.cloud.module.system.api.area.dto.AreaRegionRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * IM 数据看板 Service 实现类
 *
 * @author hanson
 */
@Service
@Validated
public class ImStatisticsManagerServiceImpl implements ImStatisticsManagerService {

    /**
     * 区域类型：1=国家 2=省份 3=城市 4=地区（与 area.csv / AreaTypeEnum 一致）
     * <p>IM 看板不直接依赖 dichat-spring-boot-starter-biz-ip（其传递 ip2region），故在此用常量表达。</p>
     */
    private static final int AREA_TYPE_COUNTRY = 1;
    private static final int AREA_TYPE_PROVINCE = 2;
    private static final int AREA_TYPE_CITY = 3;

    @Resource
    private ImStatisticsManagerMapper statisticsMapper;

    @Resource
    private SloProperties sloProperties;

    @Resource
    private AreaApi areaApi;

    // ==================== 用户 ====================

    @Override
    public Long getTotalUserCount() {
        return statisticsMapper.selectTotalUserCount();
    }

    @Override
    public Long getNewUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectNewUserCount(beginTime, endTime);
    }

    @Override
    public Long getActiveUserCount(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectActiveUserCount(beginTime, endTime);
    }

    @Override
    public Map<LocalDateTime, Long> getNewUserDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime) {
        List<Map<String, Object>> rows = statisticsMapper.selectNewUserDailyCount(beginTime, endTime);
        return toDailyCountMap(rows);
    }

    @Override
    public Map<LocalDateTime, Long> getActiveUserDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime) {
        List<Map<String, Object>> rows = statisticsMapper.selectActiveUserDailyCount(beginTime, endTime);
        return toDailyCountMap(rows);
    }

    // ==================== 群 ====================

    @Override
    public Long getTotalGroupCount() {
        return statisticsMapper.selectTotalGroupCount();
    }

    @Override
    public Long getNewGroupCount(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectNewGroupCount(beginTime, endTime);
    }

    @Override
    public Map<String, Long> getGroupSizeCountMap() {
        List<Map<String, Object>> rows = statisticsMapper.selectGroupSizeDistribution();
        return convertMap(rows,
                row -> (String) row.get("range"),
                row -> Convert.toLong(row.get("count")));
    }

    // ==================== 消息 ====================

    @Override
    public Long getPrivateMessageCount(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectPrivateMessageCount(beginTime, endTime);
    }

    @Override
    public Long getGroupMessageCount(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectGroupMessageCount(beginTime, endTime);
    }

    @Override
    public Map<LocalDateTime, Long> getPrivateMessageDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime) {
        List<Map<String, Object>> rows = statisticsMapper.selectPrivateMessageDailyCount(beginTime, endTime);
        return toDailyCountMap(rows);
    }

    @Override
    public Map<LocalDateTime, Long> getGroupMessageDailyCountMap(LocalDateTime beginTime, LocalDateTime endTime) {
        List<Map<String, Object>> rows = statisticsMapper.selectGroupMessageDailyCount(beginTime, endTime);
        return toDailyCountMap(rows);
    }

    @Override
    public Map<Integer, Long> getMessageTypeCountMap(LocalDateTime beginTime, LocalDateTime endTime) {
        List<Map<String, Object>> rows = statisticsMapper.selectMessageTypeDistribution(beginTime, endTime);
        return convertMap(rows,
                row -> Convert.toInt(row.get("type")),
                row -> Convert.toLong(row.get("count")));
    }

    @Override
    public Map<Long, Long> getTopSenderCountMap(LocalDateTime beginTime, LocalDateTime endTime, int limit) {
        List<Map<String, Object>> rows = statisticsMapper.selectTopSenders(beginTime, endTime, limit);
        return convertMap(rows,
                row -> Convert.toLong(row.get("userId")),
                row -> Convert.toLong(row.get("messageCount")));
    }

    // ==================== 红包 ====================

    @Override
    public Long getRedPacketTotalCount() {
        return statisticsMapper.selectRedPacketTotalCount();
    }

    @Override
    public Long getRedPacketTotalAmount() {
        return statisticsMapper.selectRedPacketTotalAmount();
    }

    @Override
    public Long getRedPacketCountBetween(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectRedPacketCountBetween(beginTime, endTime);
    }

    @Override
    public Long getRedPacketAmountBetween(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectRedPacketAmountBetween(beginTime, endTime);
    }

    @Override
    public Long getRedPacketGrabbedCount() {
        return statisticsMapper.selectRedPacketGrabbedCount();
    }

    @Override
    public Long getRedPacketGrabbedAmount() {
        return statisticsMapper.selectRedPacketGrabbedAmount();
    }

    @Override
    public Long getRedPacketPendingCount() {
        return statisticsMapper.selectRedPacketPendingCount();
    }

    // ==================== 转账 ====================

    @Override
    public Long getTransferTotalCount() {
        return statisticsMapper.selectTransferTotalCount();
    }

    @Override
    public Long getTransferTotalAmount() {
        return statisticsMapper.selectTransferTotalAmount();
    }

    @Override
    public Long getTransferCountBetween(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectTransferCountBetween(beginTime, endTime);
    }

    @Override
    public Long getTransferAmountBetween(LocalDateTime beginTime, LocalDateTime endTime) {
        return statisticsMapper.selectTransferAmountBetween(beginTime, endTime);
    }

    // ==================== 提现 ====================

    @Override
    public Long getWithdrawTotalCount() {
        return statisticsMapper.selectWithdrawTotalCount();
    }

    @Override
    public Long getWithdrawTotalAmount() {
        return statisticsMapper.selectWithdrawTotalAmount();
    }

    @Override
    public Long getWithdrawPendingCount() {
        return statisticsMapper.selectWithdrawPendingCount();
    }

    @Override
    public Long getWithdrawPendingAmount() {
        return statisticsMapper.selectWithdrawPendingAmount();
    }

    @Override
    public Long getWithdrawSuccessCount() {
        return statisticsMapper.selectWithdrawSuccessCount();
    }

    @Override
    public Long getWithdrawSuccessAmount() {
        return statisticsMapper.selectWithdrawSuccessAmount();
    }

    // ==================== 银行卡 ====================

    @Override
    public Long getBankCardTotalCount() {
        return statisticsMapper.selectBankCardTotalCount();
    }

    @Override
    public Long getBankCardNormalCount() {
        return statisticsMapper.selectBankCardNormalCount();
    }

    @Override
    public Long getBankCardPendingCount() {
        return statisticsMapper.selectBankCardPendingCount();
    }

    // ==================== 实时 ====================

    @Override
    public Long getActiveCallCount() {
        return statisticsMapper.selectActiveCallCount();
    }

    /**
     * 把 [{date, count}] 行映射为 {LocalDateTime -> Long}；
     */
    private static Map<LocalDateTime, Long> toDailyCountMap(List<Map<String, Object>> rows) {
        return convertMap(rows,
                row -> Convert.convert(LocalDate.class, row.get("date")).atStartOfDay(),
                row -> Convert.toLong(row.get("count")));
    }

    /**
     * 漏斗 / SLO 统计回看窗口（天）
     */
    private static final int FUNNEL_WINDOW_DAYS = 30;

    // ==================== 看板扩展：分布 / 漏斗 / 健康 ====================

    @Override
    public List<ImStatisticsManagerGeoDistributionRespVO> getGeoDistribution() {
        // 归属地在登录时已由 LoginAreaWritebackListener 异步解析为 area_id 并回写 im_users，
        // 这里只做一次 GROUP BY area_id 的纯数据库聚合，看板刷多少次都不产生任何外部调用。
        // 区域名 / 省-市层级经 system 远程 AreaApi 解析（引用 dichat 区域树），不依赖 ip2region。
        List<ImRegionAggDO> rows = statisticsMapper.selectUserRegionDistribution();
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> ids = rows.stream().map(ImRegionAggDO::getAreaId).collect(Collectors.toSet());
        Map<Long, AreaRegionRespDTO> areaMap = loadAreaMap(ids);

        // 三级聚合：国家 -> 省份 -> 城市
        Map<Long, CountryAgg> countries = new LinkedHashMap<>();
        for (ImRegionAggDO row : rows) {
            long cnt = Convert.toLong(row.getCnt(), 0L);
            Long countryId = countryIdOf(row.getAreaId(), areaMap);
            Long provinceId = provinceIdOf(row.getAreaId(), areaMap);
            Long cityId = cityIdOf(row.getAreaId(), areaMap);
            countries.computeIfAbsent(countryId, CountryAgg::new)
                    .add(provinceId, cityId, cnt, areaMap);
        }
        return countries.values().stream()
                .sorted((a, b) -> Long.compare(b.users, a.users))
                .map(agg -> agg.toVo(areaMap))
                .collect(Collectors.toList());
    }

    @Override
    public List<ImStatisticsManagerNameValueRespVO> getRegionDistribution() {
        List<ImRegionAggDO> rows = statisticsMapper.selectUserRegionDistribution();
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        // 收集所有 area_id，批量向 system 拉取区域节点（id / 父级 / 类型 / 名称）
        Set<Long> ids = rows.stream().map(ImRegionAggDO::getAreaId).collect(Collectors.toSet());
        Map<Long, AreaRegionRespDTO> areaMap = loadAreaMap(ids);
        // 按「省份」聚合（无省份则按国家本身）
        Map<Long, Long> provinceCount = new LinkedHashMap<>();
        for (ImRegionAggDO row : rows) {
            Long provinceId = provinceIdOf(row.getAreaId(), areaMap);
            provinceCount.merge(provinceId, Convert.toLong(row.getCnt(), 0L), Long::sum);
        }
        return provinceCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(e -> new ImStatisticsManagerNameValueRespVO().setName(areaName(e.getKey(), areaMap)).setValue(e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ImStatisticsManagerNameValueRespVO> getDeviceDistribution() {
        // 真实统计：基于 im_users.device_type（登录时由客户端上报的终端类型）按设备分组计数。
        // device_type 对齐框架 TerminalEnum，未上报/未知统一归为「未知」。
        List<Map<String, Object>> rows = statisticsMapper.selectUserDeviceDistribution();
        Map<String, Long> bucket = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Object raw = row.get("deviceType");
            Integer code = raw == null ? null : Convert.toInt(raw, null);
            String name = terminalName(code);
            long cnt = Convert.toLong(row.get("count"), 0L);
            bucket.merge(name, cnt, Long::sum);
        }
        return bucket.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new ImStatisticsManagerNameValueRespVO().setName(e.getKey()).setValue(e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ImStatisticsManagerFunnelStageRespVO> getFunnel() {
        LocalDate today = LocalDate.now();
        LocalDateTime begin = today.minusDays(FUNNEL_WINDOW_DAYS).atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        long registered = statisticsMapper.selectTotalUserCount();
        long active = statisticsMapper.selectActiveUserCount(begin, end);
        long messaged = statisticsMapper.selectDistinctMessageSenderCount(begin, end);
        long social = statisticsMapper.selectUserWithSocialCount();
        List<ImStatisticsManagerFunnelStageRespVO> stages = new ArrayList<>();
        stages.add(new ImStatisticsManagerFunnelStageRespVO().setName("注册用户").setValue(registered));
        stages.add(new ImStatisticsManagerFunnelStageRespVO().setName("月活用户").setValue(active));
        stages.add(new ImStatisticsManagerFunnelStageRespVO().setName("发消息用户").setValue(messaged));
        stages.add(new ImStatisticsManagerFunnelStageRespVO().setName("建关系用户").setValue(social));
        return stages;
    }

    @Override
    public List<ImStatisticsManagerSystemHealthRespVO> getSystemHealth() {
        List<ImStatisticsManagerSystemHealthRespVO> nodes = new ArrayList<>();
        long messageTps = computeMessageTpsLastMinute();
        long activeCalls = statisticsMapper.selectActiveCallCount();
        nodes.add(buildNode("接入网关", probe(() -> statisticsMapper.selectHealthCheck() != null), 0L));
        nodes.add(buildNode("消息服务", probe(() -> statisticsMapper.selectTotalUserCount() != null), messageTps));
        nodes.add(buildNode("关系链/群组", probe(() -> statisticsMapper.selectTotalGroupCount() != null), 0L));
        nodes.add(buildNode("实时音视频", probe(() -> true), activeCalls));
        nodes.add(buildNode("数据库/存储", probe(() -> statisticsMapper.selectHealthCheck() != null), 0L));
        return nodes;
    }

    @Override
    public List<ImStatisticsManagerSloRespVO> getSlo() {
        LocalDate today = LocalDate.now();
        LocalDateTime begin = today.minusDays(FUNNEL_WINDOW_DAYS).atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();
        // 消息触达率：UNREAD(0)/READ(3) 视为已触达，RECALL(2) 视为撤回；SENDING(-1) 仅客户端不持久化
        List<Map<String, Object>> statusRows = statisticsMapper.selectMessageStatusDistribution(begin, end);
        long delivered = 0;
        long recalled = 0;
        for (Map<String, Object> row : statusRows) {
            int status = Convert.toInt(row.get("status"));
            long c = Convert.toLong(row.get("count"), 0L);
            if (status == 0 || status == 3) {
                delivered += c;
            } else if (status == 2) {
                recalled += c;
            }
        }
        long total = delivered + recalled;
        double reachRate = total > 0 ? delivered * 100.0 / total : 0.0;
        long mau = statisticsMapper.selectActiveUserCount(begin, end);
        long activeCalls = statisticsMapper.selectActiveCallCount();
        long tps = computeMessageTpsLastMinute();
        List<ImStatisticsManagerSloRespVO> list = new ArrayList<>();
        list.add(slo("消息触达率", sloProperties.getMessageReachRate(), round1(reachRate), "%"));
        list.add(slo("月活跃用户", sloProperties.getMonthlyActiveUser(), (double) mau, "人"));
        list.add(slo("实时通话并发", sloProperties.getRealtimeCallConcurrency(), (double) activeCalls, "路"));
        list.add(slo("消息吞吐", sloProperties.getMessageThroughput(), (double) tps, "条/秒"));
        return list;
    }

    // ==================== 区域聚合辅助 ====================

    /**
     * 批量拉取区域节点信息，并递归补齐父级节点，直到根（全球 / 中国）为止。
     * 看板需要「国家→省份→城市」的完整层级来拼树，故父级也必须可查名。
     * 最多迭代数次（区域树深度 ≤ 4），收敛快；RPC 失败则返回已拿到部分。
     */
    private Map<Long, AreaRegionRespDTO> loadAreaMap(Set<Long> ids) {
        Set<Long> needed = new HashSet<>(ids);
        Map<Long, AreaRegionRespDTO> map = new HashMap<>();
        int guard = 0;
        while (!needed.isEmpty() && guard++ < 6) {
            List<AreaRegionRespDTO> list = areaApi.getAreaListByAreaIds(new ArrayList<>(needed)).getData();
            if (list == null || list.isEmpty()) {
                break;
            }
            Set<Long> next = new HashSet<>();
            for (AreaRegionRespDTO dto : list) {
                if (dto.getId() == null || map.containsKey(dto.getId())) {
                    continue;
                }
                map.put(dto.getId(), dto);
                // 父级仍需拉取（全球=0 / 中国=1 视为根，停止上溯）
                if (dto.getParentId() != null && dto.getParentId() > 1 && !map.containsKey(dto.getParentId())) {
                    next.add(dto.getParentId());
                }
            }
            needed = next;
        }
        return map;
    }

    private Long countryIdOf(Long areaId, Map<Long, AreaRegionRespDTO> map) {
        AreaRegionRespDTO node = map.get(areaId);
        while (node != null) {
            if (node.getType() != null && node.getType() == AREA_TYPE_COUNTRY) {
                return node.getId();
            }
            if (node.getParentId() == null) {
                break;
            }
            node = map.get(node.getParentId());
        }
        return areaId;
    }

    private Long provinceIdOf(Long areaId, Map<Long, AreaRegionRespDTO> map) {
        AreaRegionRespDTO node = map.get(areaId);
        while (node != null) {
            if (node.getType() != null && node.getType() == AREA_TYPE_PROVINCE) {
                return node.getId();
            }
            if (node.getParentId() == null) {
                break;
            }
            node = map.get(node.getParentId());
        }
        return areaId; // 无省份（国家/地区级）归到自身
    }

    private Long cityIdOf(Long areaId, Map<Long, AreaRegionRespDTO> map) {
        AreaRegionRespDTO node = map.get(areaId);
        return (node != null && node.getType() != null && node.getType() == AREA_TYPE_CITY) ? node.getId() : null;
    }

    private static String areaName(Long id, Map<Long, AreaRegionRespDTO> map) {
        AreaRegionRespDTO dto = map.get(id);
        return (dto != null && dto.getName() != null) ? dto.getName() : "未知";
    }

    // ==================== 私有辅助 ====================

    /**
     * 终端编码 → 展示名（对齐框架 {@link TerminalEnum}）。
     * 未上报（null）或无法识别的编码统一归为「未知」，避免前端出现裸数字。
     */
    private static String terminalName(Integer code) {
        if (code == null) {
            return TerminalEnum.UNKNOWN.getName();
        }
        for (TerminalEnum e : TerminalEnum.values()) {
            if (e.getTerminal().equals(code)) {
                return e.getName();
            }
        }
        return TerminalEnum.UNKNOWN.getName();
    }

    /**
     * 真实探针：执行 action 并测量耗时（毫秒）。action 抛异常视为探针失败。
     */
    private ProbeResult probe(Supplier<Boolean> action) {
        long start = System.nanoTime();
        boolean ok;
        try {
            ok = action.get();
        } catch (Exception e) {
            ok = false;
        }
        long latency = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        ProbeResult r = new ProbeResult();
        r.ok = ok;
        r.latency = Math.max(latency, 0L);
        return r;
    }

    private ImStatisticsManagerSystemHealthRespVO buildNode(String name, ProbeResult probe, long tps) {
        String status = !probe.ok ? "error" : statusByLatency(probe.latency);
        return new ImStatisticsManagerSystemHealthRespVO()
                .setName(name).setStatus(status).setTps(tps).setLatency(probe.latency).setLag(0L);
    }

    private static String statusByLatency(long latency) {
        if (latency > 3000) {
            return "error";
        }
        if (latency > 1000) {
            return "warn";
        }
        return "ok";
    }

    /**
     * 近 1 分钟消息总数 / 60 ≈ 消息 TPS（真实数据，瞬时值可能为 0）
     */
    private long computeMessageTpsLastMinute() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMinAgo = now.minusMinutes(1);
        long count = statisticsMapper.selectPrivateMessageCount(oneMinAgo, now)
                + statisticsMapper.selectGroupMessageCount(oneMinAgo, now);
        return count / 60L;
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    private ImStatisticsManagerSloRespVO slo(String name, double target, double actual, String unit) {
        return new ImStatisticsManagerSloRespVO().setName(name).setTarget(target).setActual(actual).setUnit(unit);
    }

    /**
     * 探针结果载体
     */
    private static final class ProbeResult {
        boolean ok;
        long latency;
    }

    /**
     * 全球分布三级聚合载体：国家 → 省份 → 城市(运营商)
     */
    private static final class CountryAgg {
        final Long countryId;
        long users = 0;
        final Map<Long, ProvinceAgg> provinces = new LinkedHashMap<>();

        CountryAgg(Long countryId) {
            this.countryId = countryId;
        }

        void add(Long provinceId, Long cityId, long cnt, Map<Long, AreaRegionRespDTO> map) {
            users += cnt;
            // 国家级（无省份）直接归入国家，不再建省份节点
            if (provinceId != null) {
                provinces.computeIfAbsent(provinceId, ProvinceAgg::new).add(cityId, cnt, map);
            }
        }

        ImStatisticsManagerGeoDistributionRespVO toVo(Map<Long, AreaRegionRespDTO> map) {
            String name = areaName(countryId, map);
            ImStatisticsManagerGeoDistributionRespVO vo = new ImStatisticsManagerGeoDistributionRespVO();
            vo.setName(name).setCnName(name).setUsers(users).setGeo("");
            vo.setProvinces(provinces.values().stream()
                    .sorted((a, b) -> Long.compare(b.users, a.users))
                    .map(p -> p.toVo(map))
                    .collect(Collectors.toList()));
            return vo;
        }
    }

    private static final class ProvinceAgg {
        final Long provinceId;
        long users = 0;
        final Map<Long, CityAgg> cities = new LinkedHashMap<>();

        ProvinceAgg(Long provinceId) {
            this.provinceId = provinceId;
        }

        void add(Long cityId, long cnt, Map<Long, AreaRegionRespDTO> map) {
            users += cnt;
            if (cityId != null) {
                cities.computeIfAbsent(cityId, CityAgg::new).users += cnt;
            }
        }

        ImStatisticsManagerGeoDistributionRespVO.ProvinceRespVO toVo(Map<Long, AreaRegionRespDTO> map) {
            return new ImStatisticsManagerGeoDistributionRespVO.ProvinceRespVO()
                    .setName(areaName(provinceId, map)).setUsers(users)
                    .setCities(cities.values().stream()
                            .sorted((a, b) -> Long.compare(b.users, a.users))
                            .map(c -> c.toVo(map))
                            .collect(Collectors.toList()));
        }
    }

    private static final class CityAgg {
        final Long cityId;
        long users = 0;

        CityAgg(Long cityId) {
            this.cityId = cityId;
        }

        ImStatisticsManagerGeoDistributionRespVO.CityRespVO toVo(Map<Long, AreaRegionRespDTO> map) {
            return new ImStatisticsManagerGeoDistributionRespVO.CityRespVO()
                    .setCity(areaName(cityId, map)).setUsers(users)
                    .setLat(0.0).setLng(0.0)
                    .setIsps(Collections.emptyList()).setSampleIp("");
        }
    }

}
