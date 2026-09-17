package com.diqin.cloud.module.system.api.area.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 区域节点 RPC DTO
 * <p>
 * 由 system 服务端基于静态区域树（{@code area.csv} / {@code AreaUtils}）产出，
 * 所有 IP→定位统一经本结构返回，保证 area_id 稳定可引用。
 *
 * @author hanson
 */
@Data
public class AreaRegionRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 区域编号（与 area.csv 一致，唯一且稳定）
     */
    private Long id;

    /**
     * 父级区域编号（全球节点为 null）
     */
    private Long parentId;

    /**
     * 区域类型：1=国家 2=省份 3=城市 4=地区
     */
    private Integer type;

    /**
     * 区域名称（area.csv 原始名，如「广东省」「深圳市」「中国」）
     */
    private String name;

}
