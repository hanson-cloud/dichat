package com.diqin.cloud.module.im.dal.dataobject.withdraw;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 提现申请 DO（余额提现到银行卡；实际扣减委托钱包模块）
 *
 * @author dichat
 */
@TableName("im_withdraw")
@KeySequence("im_withdraw_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImWithdrawDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 提现单号
     */
    private String no;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 银行卡编号
     */
    private Long bankCardId;
    /**
     * 银行名称（快照）
     */
    private String bankName;
    /**
     * 脱敏卡号（快照）
     */
    private String cardNoMask;
    /**
     * 提现金额，单位：分
     */
    private Integer amount;
    /**
     * 状态
     * 枚举 {@link com.diqin.cloud.module.im.enums.withdraw.ImWithdrawStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 审核人用户编号
     */
    private Long auditUserId;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 审核原因
     */
    private String auditReason;

}
