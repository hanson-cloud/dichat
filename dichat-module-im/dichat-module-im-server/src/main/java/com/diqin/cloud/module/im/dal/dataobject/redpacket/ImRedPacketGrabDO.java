package com.diqin.cloud.module.im.dal.dataobject.redpacket;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 红包领取明细 DO
 *
 * @author dichat
 */
@TableName("im_red_packet_grab")
@KeySequence("im_red_packet_grab_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImRedPacketGrabDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 红包编号
     * 关联 {@link ImRedPacketDO#getId()}
     */
    private Long redPacketId;
    /**
     * 红包单号
     */
    private String redPacketNo;
    /**
     * 领取人用户编号
     */
    private Long userId;
    /**
     * 领取金额，单位：分
     */
    private Integer amount;
    /**
     * 是否手气最佳
     */
    private Boolean isBestLuck;
    /**
     * 领取时间
     */
    private LocalDateTime grabTime;

}
