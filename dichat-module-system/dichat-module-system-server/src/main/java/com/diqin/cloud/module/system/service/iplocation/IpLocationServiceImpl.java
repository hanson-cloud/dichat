package com.diqin.cloud.module.system.service.iplocation;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.module.system.convert.iplocation.IpLocationConvert;
import com.diqin.cloud.module.system.dal.dataobject.iplocation.IpLocationDO;
import com.diqin.cloud.module.system.dal.mysql.iplocation.IpLocationMapper;
import com.diqin.cloud.module.system.framework.area.AreaProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * IP 定位缓存 Service（参考 photo-frame 的 IpLocationServiceImpl）。
 * 策略（缓存优先，ip-api 兜底）：
 *  1) 内网 / 保留地址 / 非法格式 IP：本地判定为非法，直接返回未解析（不调 ip-api）；
 *  2) L1 Redis 命中则直接返回（维度 ip）；
 *  3) L2 MySQL 命中「成功行」则回写 Redis 并返回；
 *  4) 未命中则调用 ip-api.com/json：拿到正常数据 → 全字段落库(upsert) + 写 Redis(7d)；
 *     无正常数据（网络/超时/非 success）→ 间隔 2 秒重试，耗尽次数后返回未解析（不落库、不写缓存）；
 *  5) ip-api 未启用（{@code areaProperties.enabled=false}）时跳过外调，仅服务缓存/DB。
 * 语种固定 zh-CN（与静态区域树 area.csv 一致）。全程静默不阻断业务。
 *
 * @author hanson
 */
@Slf4j
@Service
public class IpLocationServiceImpl implements IpLocationService {

    /**
     * ip-api.com 限定返回字段（全字段，省流量）
     */
    private static final String FIELDS = "status,message,country,countryCode,region,regionName,city,district,zip,isp,org,as,lat,lon,timezone,query";

    /**
     * Redis 缓存 key 前缀（维度为 ip；语种固定 zh-CN 故不入 key）
     */
    private static final String REDIS_KEY_PREFIX = "ip_location:";

    /**
     * 固定语种（与静态区域树 area.csv 一致）
     */
    private static final String DEFAULT_LANG = "zh-CN";

    /**
     * 成功结果 TTL：7 天（拿到正确数据后才缓存）
     */
    private static final long REDIS_TTL_SUCCESS_SECONDS = 7L * 24 * 60 * 60;

    /**
     * 外调总尝试次数（含首次）；任一次拿到正确数据即返回，否则间隔 RETRY_INTERVAL_MILLIS 重试
     */
    private static final int MAX_ATTEMPTS = 3;

    /**
     * 重试间隔（ms）
     */
    private static final long RETRY_INTERVAL_MILLIS = 2000L;

    /**
     * 成功状态标记
     */
    private static final Integer STATUS_SUCCESS = 1;

    /**
     * IPv4 格式校验（识别明显非法的「假 IP」，直接降级不调 ip-api）
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)$");

    @Resource
    private IpLocationMapper ipLocationMapper;

    @Resource
    private AreaProperties areaProperties;

    @Resource
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;

    /**
     * IP 定位专用 RestTemplate（独立实例，避免被框架 @LoadBalanced 拦截器把 ip-api.com 当服务名解析）。
     */
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(areaProperties.getConnectTimeoutMs());
        factory.setReadTimeout(areaProperties.getReadTimeoutMs());
        this.restTemplate = new RestTemplate(factory);
        log.info("[init][ip-api 客户端就绪，在线解析={}]", areaProperties.isEnabled());
    }

    @Override
    public IpLocationResult getByIp(String ip) {
        // 1. 入参防护：空 / 内网 / 非法格式 → 直接降级（不调 ip-api）
        if (StrUtil.isBlank(ip) || isPrivateOrUnsupported(ip) || !isValidIpv4(ip)) {
            return unknown(ip);
        }

        // 2. L1 命中直接返回
        IpLocationResult cached = getFromRedis(ip);
        if (cached != null) {
            return cached;
        }

        // 3. L2 命中：仅「成功行」短路返回；历史负缓存行(status=0)不下发
        IpLocationDO db = ipLocationMapper.selectByIp(ip);
        boolean dbExists = db != null;
        if (dbExists && STATUS_SUCCESS.equals(db.getStatus())) {
            IpLocationResult dto = IpLocationConvert.convert(db);
            writeRedis(ip, dto);
            return dto;
        }

        // 4. 外调 ip-api.com（受 enabled 控制）：拿到正确数据 → 全字段落库 + 写 Redis
        if (areaProperties.isEnabled()) {
            IpLocationResult dto = fetchWithRetry(ip);
            if (dto != null) {
                dto.setIp(ip);
                dto.setLang(DEFAULT_LANG);
                IpLocationDO successEntity = IpLocationConvert.convert(dto);
                upsert(successEntity, STATUS_SUCCESS, dbExists ? db.getId() : null);
                writeRedis(ip, dto);
                return dto;
            }
        }

        // 5. 耗尽重试仍无正常数据：不落库、不写缓存，返回未解析
        return unknown(ip);
    }

    private String redisKey(String ip) {
        return REDIS_KEY_PREFIX + ip;
    }

    /**
     * 从 Redis 读取缓存的定位结果；读取异常时降级返回 null，不阻断主流程。
     */
    private IpLocationResult getFromRedis(String ip) {
        try {
            String json = stringRedisTemplate.opsForValue().get(redisKey(ip));
            if (StrUtil.isBlank(json)) {
                return null;
            }
            return JsonUtils.parseObject(json, IpLocationResult.class);
        } catch (Exception e) {
            log.debug("[IP定位] Redis 读取异常 ip={} err={}", ip, e.getMessage());
            return null;
        }
    }

    /**
     * 写入 Redis 缓存；Redis 不可用时仅记录日志并降级，不阻断业务。
     */
    private void writeRedis(String ip, IpLocationResult dto) {
        try {
            stringRedisTemplate.opsForValue().set(redisKey(ip), JsonUtils.toJsonString(dto),
                    REDIS_TTL_SUCCESS_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.debug("[IP定位] Redis 写入失败 ip={} err={}", ip, e.getMessage());
        }
    }

    /**
     * 调用 ip-api.com/json 查询单个 IP 的定位信息。
     * 仅在返回 success 且有正常地理数据时返回 DTO；其余（无响应 / 非 success / 4xx·5xx / 网络·超时）一律返回 null。
     */
    private IpLocationResult fetchFromIpApi(String ip) {
        try {
            String url = StrUtil.removeSuffix(areaProperties.getUrl(), "/")
                    + "/" + ip + "?fields=" + FIELDS + "&lang=" + DEFAULT_LANG;
            ResponseEntity<String> response = restTemplate.getForEntity(URI.create(url), String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("[IP定位] ip-api 返回 {}，降级 ip={}", response.getStatusCode().value(), ip);
                return null;
            }
            String body = response.getBody();
            if (body == null || body.isBlank()) {
                return null;
            }
            IpApiResponse resp = JsonUtils.parseObject(body, IpApiResponse.class);
            if (resp == null || !"success".equalsIgnoreCase(resp.getStatus())) {
                log.warn("[IP定位] ip-api 返回非成功状态 ip={} status={}", ip, resp == null ? "null" : resp.getStatus());
                return null;
            }
            return parseResp(resp);
        } catch (HttpStatusCodeException e) {
            log.warn("[IP定位] ip-api 返回错误状态码 ip={} status={}", ip, e.getStatusCode());
            return null;
        } catch (Exception e) {
            log.warn("[IP定位] 调用 ip-api 瞬时故障 ip={} err={}", ip, e.getMessage());
            return null;
        }
    }

    /**
     * 带间隔重试调用 ip-api.com：最多 MAX_ATTEMPTS 次，每次间隔 RETRY_INTERVAL_MILLIS（2 秒）。
     * 仅当拿到正确地理数据才返回 DTO，否则返回 null。
     */
    private IpLocationResult fetchWithRetry(String ip) {
        IpLocationResult dto = fetchFromIpApi(ip);
        for (int attempt = 1; attempt < MAX_ATTEMPTS && dto == null; attempt++) {
            try {
                Thread.sleep(RETRY_INTERVAL_MILLIS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("[IP定位] 重试等待被中断，放弃后续重试 ip={}", ip);
                break;
            }
            log.warn("[IP定位] ip-api 无正常数据，准备第 {} 次重试 ip={}", attempt, ip);
            dto = fetchFromIpApi(ip);
        }
        return dto;
    }

    private IpLocationResult parseResp(IpApiResponse resp) {
        IpLocationResult dto = new IpLocationResult();
        dto.setSuccess(true);
        dto.setCountry(StrUtil.toStringOrEmpty(resp.getCountry()));
        dto.setRegion(StrUtil.toStringOrEmpty(resp.getRegionName()));
        dto.setCity(StrUtil.toStringOrEmpty(resp.getCity()));
        dto.setDistrict(StrUtil.toStringOrEmpty(resp.getDistrict()));
        dto.setIsp(StrUtil.toStringOrEmpty(resp.getIsp()));
        dto.setLatitude(StrUtil.toStringOrEmpty(resp.getLat()));
        dto.setLongitude(StrUtil.toStringOrEmpty(resp.getLon()));
        dto.setCountryCode(StrUtil.toStringOrEmpty(resp.getCountryCode()));
        // ip-api.com 的 region 为省/州代码（如 44），regionName 才是名称；本行映射到 regionCode 字段
        dto.setRegionCode(StrUtil.toStringOrEmpty(resp.getRegion()));
        dto.setZip(StrUtil.toStringOrEmpty(resp.getZip()));
        dto.setTimezone(StrUtil.toStringOrEmpty(resp.getTimezone()));
        dto.setOrg(StrUtil.toStringOrEmpty(resp.getOrg()));
        dto.setAsn(StrUtil.toStringOrEmpty(resp.getAs()));
        return dto;
    }

    /**
     * 落库：新增或按既有 id 更新，使成功结果覆盖历史负缓存行（自愈），复用 MP 自动填充 createTime/updateTime。
     */
    private void upsert(IpLocationDO entity, Integer status, Long existingId) {
        try {
            entity.setStatus(status);
            if (existingId != null) {
                entity.setId(existingId);
                ipLocationMapper.updateById(entity);
            } else {
                ipLocationMapper.insert(entity);
            }
        } catch (Exception e) {
            log.debug("[IP定位] 落库失败 ip={} err={}", entity.getIp(), e.getMessage());
        }
    }

    /**
     * 判断是否为内网 / 保留地址 / IPv6：此类地址不发起外网调用。
     */
    private boolean isPrivateOrUnsupported(String ip) {
        if (ip.contains(":")) {
            return true; // IPv6，ip-api 免费档仅支持 IPv4
        }
        try {
            return Ipv4Util.isInnerIP(ip);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 校验是否为合法 IPv4 格式（点分十进制）；非法格式视为「假 IP」，直接降级不调 ip-api。
     */
    private boolean isValidIpv4(String ip) {
        return IPV4_PATTERN.matcher(ip).matches();
    }

    private IpLocationResult unknown(String ip) {
        IpLocationResult dto = new IpLocationResult();
        dto.setIp(ip);
        dto.setLang(DEFAULT_LANG);
        dto.setSuccess(false);
        return dto;
    }

    /**
     * ip-api.com 单 IP 解析响应（全字段）
     */
    @lombok.Data
    public static class IpApiResponse {
        private String status;
        private String message;
        private String country;
        private String countryCode;
        private String region;
        private String regionName;
        private String city;
        private String district;
        private String zip;
        private String isp;
        private String org;
        private String as;
        private String lat;
        private String lon;
        private String timezone;
        private String query;
    }

}
