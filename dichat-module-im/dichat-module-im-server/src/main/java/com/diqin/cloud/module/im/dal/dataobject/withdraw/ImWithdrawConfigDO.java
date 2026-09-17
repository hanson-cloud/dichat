package com.diqin.cloud.module.im.dal.dataobject.withdraw;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 提现开关配置 DO
 * <p>
 * 全局单行配置（固定 {@code id = 1}）：是否开放提现，以及关闭时通知用户的关闭消息。
 * 关闭提现必须由管理端填写 {@code closeMessage}，用户端提现页据此展示横幅并禁用确认按钮；
 * 后端 {@code createWithdraw} 同样会校验该开关，防止绕过前端暴力发起提现。
 *
 * @author dichat
 */
@TableName(value = "im_withdraw_config", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImWithdrawConfigDO extends TenantBaseDO {

    /**
     * 固定主键（全局单行）
     */
    @TableId
    private Long id;
    /**
     * 是否开放提现
     */
    private Boolean enabled;
    /**
     * 关闭提现时通知用户的消息（关闭时必填）
     */
    private String closeMessage;
    /**
     * 最近一次操作管理员编号
     */
    private Long operatorId;

}
