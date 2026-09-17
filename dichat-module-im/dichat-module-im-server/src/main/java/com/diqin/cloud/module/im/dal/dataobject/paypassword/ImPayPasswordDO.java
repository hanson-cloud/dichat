package com.diqin.cloud.module.im.dal.dataobject.paypassword;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * IM 支付密码 DO
 * <p>
 * {@code password} 存储 BCrypt 哈希，永不保存明文。
 *
 * @author dichat
 */
@TableName(value = "im_pay_password")
@KeySequence("im_pay_password_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImPayPasswordDO extends TenantBaseDO {

    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 支付密码 BCrypt 哈希
     */
    private String password;

    /**
     * 状态：0-正常 1-已重置
     */
    private Integer status;

    /**
     * 手势密码 SHA-256 哈希（存储点序哈希，永不存明文轨迹）
     */
    private String gesturePattern;

    /**
     * 手势密码是否开启：0-关闭 1-开启
     */
    private Integer gestureEnabled;

    /**
     * 指纹支付是否开启：0-关闭 1-开启
     */
    private Integer fingerprintEnabled;

}
