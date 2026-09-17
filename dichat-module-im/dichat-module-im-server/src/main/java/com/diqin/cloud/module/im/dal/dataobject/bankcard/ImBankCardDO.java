package com.diqin.cloud.module.im.dal.dataobject.bankcard;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 银行卡 DO
 * <p>
 * 出于安全考虑，卡号与证件号仅存储脱敏后的后四位；完整 PAN 不应持久化。
 *
 * @author dichat
 */
@TableName(value = "im_bank_card", autoResultMap = true)
@KeySequence("im_bank_card_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImBankCardDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 脱敏卡号（仅保留后四位）
     */
    private String cardNoMask;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 持卡人姓名
     */
    private String cardholder;
    /**
     * 脱敏证件号
     */
    private String idCardMask;
    /**
     * 银行预留手机号
     */
    private String phone;
    /**
     * 卡片类型
     * 枚举 {@link com.diqin.cloud.module.im.enums.bankcard.ImBankCardTypeEnum}
     */
    private Integer type;
    /**
     * 状态
     * 枚举 {@link com.diqin.cloud.module.im.enums.bankcard.ImBankCardStatusEnum}
     */
    private Integer status;
    /**
     * 是否默认卡
     */
    private Boolean isDefault;
    /**
     * 绑定时间
     */
    private LocalDateTime bindTime;
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
