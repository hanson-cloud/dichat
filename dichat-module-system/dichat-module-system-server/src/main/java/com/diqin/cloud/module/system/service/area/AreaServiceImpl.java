package com.diqin.cloud.module.system.service.area;

import com.diqin.cloud.framework.ip.core.Area;
import com.diqin.cloud.framework.ip.core.utils.AreaUtils;
import com.diqin.cloud.module.system.api.area.dto.AreaRegionRespDTO;
import com.diqin.cloud.module.system.service.iplocation.IpLocationResult;
import com.diqin.cloud.module.system.service.iplocation.IpLocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 区域 Service 实现
 * <p>
 * 把 ip-api.com 返回的「国家 / 省 / 市」原始名，通过<b>容忍匹配</b>映射到静态区域树
 * （{@code area.csv} / {@code AreaUtils}），产出稳定可引用的 area_id。
 * 区域树是静态资源（area.csv），故 area_id 天然稳定，无需 photo-frame 那套 DB 惰性建树。
 * </p>
 *
 * @author hanson
 */
@Slf4j
@Service
@AllArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final IpLocationService ipLocationService;

    @Override
    public Long getAreaIdByIp(String ip) {
        IpLocationResult loc = safeGet(ip);
        if (loc == null) {
            return null;
        }
        Integer areaId = resolveAreaId(loc.getCountry(), loc.getRegion(), loc.getCity());
        return areaId == null ? null : areaId.longValue();
    }

    @Override
    public AreaRegionRespDTO getRegionByIp(String ip) {
        IpLocationResult loc = safeGet(ip);
        if (loc == null) {
            return null;
        }
        return toDto(resolveAreaId(loc.getCountry(), loc.getRegion(), loc.getCity()));
    }

    @Override
    public List<AreaRegionRespDTO> getAreaListByAreaIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return ids.stream()
                .map(id -> toDto(id == null ? null : id.intValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AreaRegionRespDTO toDto(Integer id) {
        if (id == null) {
            return null;
        }
        Area area = AreaUtils.getArea(id);
        if (area == null) {
            return null;
        }
        AreaRegionRespDTO dto = new AreaRegionRespDTO();
        dto.setId(id.longValue());
        dto.setName(area.getName());
        dto.setType(area.getType());
        dto.setParentId(area.getParent() != null ? area.getParent().getId().longValue() : null);
        return dto;
    }

    private IpLocationResult safeGet(String ip) {
        try {
            IpLocationResult result = ipLocationService.getByIp(ip);
            return (result != null && result.isSuccess()) ? result : null;
        } catch (Exception e) {
            log.warn("[区域解析] 调用 IpLocationService 失败 ip={}", ip, e);
            return null;
        }
    }

    /**
     * 把 ip-api 返回的「国家 / 省 / 市」映射到静态区域树，返回最深层可解析节点的 area_id。
     * 国家匹配不到直接返回 null；省级匹配不到则返回国家 id（国家直接作为归属地）；
     * 否则返回城市 id（命中）或省份 id（仅命中省）。
     */
    private Integer resolveAreaId(String country, String region, String city) {
        Area root = AreaUtils.getArea(Area.ID_GLOBAL);
        if (root == null) {
            return null;
        }
        Area countryNode = findChild(root, country);
        if (countryNode == null) {
            return null;
        }
        Area regionNode = findChild(countryNode, region);
        if (regionNode == null) {
            return countryNode.getId();
        }
        Area cityNode = findChild(regionNode, city);
        return (cityNode != null ? cityNode : regionNode).getId();
    }

    private Area findChild(Area parent, String name) {
        if (parent == null || name == null) {
            return null;
        }
        for (Area child : parent.getChildren()) {
            if (matchName(child.getName(), name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * 容忍匹配：ip-api 返回「广东」「深圳」这类无后缀名，而区域树存「广东省」「深圳市」。
     * 先精确比对，再剥离常见行政区划后缀后比对。长后缀（特别行政区 / XX 自治区）优先于短后缀。
     */
    private static boolean matchName(String csvName, String target) {
        if (csvName == null || target == null) {
            return false;
        }
        if (csvName.equals(target)) {
            return true;
        }
        return normalize(csvName).equals(normalize(target));
    }

    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        String[] suffixes = {"特别行政区", "维吾尔自治区", "壮族自治区", "回族自治区",
                "自治区", "自治州", "自治县", "省", "市", "县", "区", "盟", "地区"};
        for (String suf : suffixes) {
            if (t.endsWith(suf) && t.length() > suf.length()) {
                t = t.substring(0, t.length() - suf.length());
            }
        }
        return t;
    }

}
