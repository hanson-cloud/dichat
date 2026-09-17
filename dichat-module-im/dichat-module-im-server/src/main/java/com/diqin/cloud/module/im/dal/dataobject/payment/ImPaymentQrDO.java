package com.diqin.cloud.module.im.dal.dataobject.payment;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

@TableName("im_payment_qr")
@KeySequence("im_payment_qr_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImPaymentQrDO extends TenantBaseDO {

    @TableId
    private Long id;
    /** 用户编号 */
    private Long userId;
    /** 收款码（唯一，付款方扫码时使用） */
    private String code;
    /** 类型：1-收款码 */
    private Integer type;
    /** 收款金额（分，null 表示不限定金额） */
    private Long amount;
    /** 收款备注 */
    private String remark;
    /** 状态：0-有效 1-已使用 2-已作废 */
    private Integer status;
    /** 过期时间 */
    private LocalDateTime expireTime;
}
