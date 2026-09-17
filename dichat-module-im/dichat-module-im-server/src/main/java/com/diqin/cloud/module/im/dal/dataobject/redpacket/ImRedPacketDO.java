package com.diqin.cloud.module.im.dal.dataobject.redpacket;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 红包主表 DO
 * <p>
 * 一包红包一行；发送时冻结发送方钱包余额，领取时逐笔解冻到领取方；
 * 状态机见 {@link com.diqin.cloud.module.im.enums.redpacket.ImRedPacketStatusEnum}
 *
 * @author dichat
 */
@TableName("im_red_packet")
@KeySequence("im_red_packet_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImRedPacketDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 红包单号（唯一）
     */
    private String no;
    /**
     * 发送人用户编号
     */
    private Long senderUserId;
    /**
     * 会话类型
     * 枚举 {@link com.diqin.cloud.module.im.enums.ImConversationTypeEnum}
     */
    private Integer conversationType;
    /**
     * 私聊红包领取人用户编号；群聊为 NULL
     */
    private Long receiverUserId;
    /**
     * 群编号；私聊为 NULL
     */
    private Long groupId;
    /**
     * 红包类型
     * 枚举 {@link com.diqin.cloud.module.im.enums.redpacket.ImRedPacketTypeEnum}
     */
    private Integer type;
    /**
     * 红包总金额，单位：分
     */
    private Integer totalAmount;
    /**
     * 红包总个数
     */
    private Integer totalCount;
    /**
     * 剩余金额，单位：分
     */
    private Integer remainAmount;
    /**
     * 剩余个数
     */
    private Integer remainCount;
    /**
     * 祝福语
     */
    private String greeting;
    /**
     * 红包状态
     * 枚举 {@link com.diqin.cloud.module.im.enums.redpacket.ImRedPacketStatusEnum}
     */
    private Integer status;
    /**
     * 过期时间
     */
    private LocalDateTime expireTime;
    /**
     * 已领取金额，单位：分
     */
    private Integer grabbedAmount;
    /**
     * 已领取个数
     */
    private Integer grabbedCount;

}
