package com.diqin.cloud.module.im.dal.dataobject.bank;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 支持的银行 DO
 *
 * @author dichat
 */
@TableName(value = "im_bank", autoResultMap = true)
@KeySequence("im_bank_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImBankDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 银行编码（唯一，如 CMB/ICBC）
     */
    private String bankCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 卡组织：UNIONPAY/VISA/MASTER/JCB/AMEX
     */
    private String cardOrg;
    /**
     * BIN 前缀（逗号分隔，卡号前 6 位，用于卡号识别）
     */
    private String binPrefix;
    /**
     * 银行 LOGO 地址
     */
    private String logoUrl;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    /**
     * 排序（越小越靠前）
     */
    private Integer sort;

}
