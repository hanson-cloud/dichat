package com.diqin.cloud.module.im.dal.dataobject.user_ban;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 用户封禁记录 DO
 *
 * <p>管理员对用户实施的全局禁言/封号处罚台账，独立于用户级 im_blacklist。
 * 封禁生效期间用户无法发送消息/登录。
 *
 * @author 速构构
 */
@TableName(value = "im_user_ban", autoResultMap = true)
@KeySequence("im_user_ban_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImUserBanDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 被处罚用户编号
     */
    private Long userId;
    /**
     * 处罚类型：1-全局禁言 2-封号
     */
    private Integer banType;
    /**
     * 处罚原因
     */
    private String reason;
    /**
     * 执行人编号
     */
    private Long bannedBy;
    /**
     * 处罚开始时间
     */
    private LocalDateTime banStartTime;
    /**
     * 处罚结束时间（空=永久）
     */
    private LocalDateTime banEndTime;
    /**
     * 状态：0-生效中 1-已解封
     */
    private Integer status;
    /**
     * 解封人编号
     */
    private Long unbannedBy;
    /**
     * 解封时间
     */
    private LocalDateTime unbanTime;
    /**
     * 解封原因
     */
    private String unbanReason;

}
