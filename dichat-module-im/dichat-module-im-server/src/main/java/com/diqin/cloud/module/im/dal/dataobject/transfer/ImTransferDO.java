package com.diqin.cloud.module.im.dal.dataobject.transfer;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 转账流水 DO（P2P 转账台账；实际余额变动委托钱包模块）
 *
 * @author dichat
 */
@TableName("im_transfer")
@KeySequence("im_transfer_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImTransferDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 转账单号
     */
    private String no;
    /**
     * 转账人用户编号
     */
    private Long senderUserId;
    /**
     * 收款人用户编号
     */
    private Long receiverUserId;
    /**
     * 转账金额，单位：分
     */
    private Integer amount;
    /**
     * 转账备注
     */
    private String remark;
    /**
     * 状态
     * 枚举 {@link com.diqin.cloud.module.im.enums.transfer.ImTransferStatusEnum}
     */
    private Integer status;
    /**
     * 完成时间
     */
    private LocalDateTime payTime;

}
