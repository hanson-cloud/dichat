package com.diqin.cloud.module.system.service.area;

import com.diqin.cloud.module.system.api.area.dto.AreaRegionRespDTO;

import java.util.List;

/**
 * 区域 Service
 * <p>把「IP → 区域编号 area_id」与「area_id → 区域节点信息」两类能力收敛到一处，
 * 底层基于 ip-api.com 在线解析 + 静态区域树（{@code area.csv} / {@code AreaUtils}）映射，
 * 不使用 ip2region。</p>
 *
 * @author hanson
 */
public interface AreaService {

    /**
     * 根据 IP 获取区域编号 area_id
     *
     * @param ip IP（内网 / 保留地址或解析失败均返回 null）
     * @return 区域编号；无法解析时返回 null
     */
    Long getAreaIdByIp(String ip);

    /**
     * 根据 IP 获取区域节点（含 area_id / 名称 / 父级 / 类型）
     */
    AreaRegionRespDTO getRegionByIp(String ip);

    /**
     * 批量获取区域节点信息（供看板拼树 / 出名）
     *
     * @param ids 区域编号列表
     * @return 节点信息；无效 id 自动跳过
     */
    List<AreaRegionRespDTO> getAreaListByAreaIds(List<Long> ids);

}
