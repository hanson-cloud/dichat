package com.diqin.cloud.module.im.dal.dataobject.statistics;

import lombok.Data;

/**
 * 归属地分布聚合结果
 * <p>
 * {@code im_users} 按 {@code area_id} 分组的一行结果；area_id 引用 dichat 静态区域树（area.csv），
 * 由登录后异步回写（见 {@code LoginAreaWritebackListener}），不再依赖 im_ip_region 文本表。
 * 不对应任何物理表，仅作为统计 SQL 的出参载体。
 *
 * @author hanson
 */
@Data
public class ImRegionAggDO {

    /**
     * 区域编号（area.csv 中的稳定 id；引用 dichat 区域树）
     */
    private Long areaId;

    /**
     * 该区域编号下的用户数
     */
    private Long cnt;

}
