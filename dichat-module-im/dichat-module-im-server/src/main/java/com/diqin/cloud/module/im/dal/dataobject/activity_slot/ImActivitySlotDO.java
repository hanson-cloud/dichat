package com.diqin.cloud.module.im.dal.dataobject.activity_slot;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 运营活动位 DO
 *
 * <p>运营位用于在 IM 客户端各展示位置投放运营活动/广告内容，
 * 由管理后台统一创建与配置，支持定时上下线与排序。
 *
 * @author 速构构
 */
@TableName(value = "im_activity_slot", autoResultMap = true)
@KeySequence("im_activity_slot_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImActivitySlotDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 活动名称
     */
    private String name;
    /**
     * 展示位：1-发现页Banner 2-聊天列表 3-朋友圈
     */
    private Integer slotPosition;
    /**
     * 图片地址
     */
    private String imageUrl;
    /**
     * 跳转链接
     */
    private String linkUrl;
    /**
     * 链接类型：1-URL 2-WebView 3-APP内页
     */
    private Integer linkType;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    /**
     * 生效开始时间
     */
    private LocalDateTime startTime;
    /**
     * 生效结束时间
     */
    private LocalDateTime endTime;
    /**
     * 描述
     */
    private String description;

}
