package com.diqin.cloud.module.im.dal.dataobject.blacklist;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 全局黑名单 DO
 *
 * @author dichat
 */
@TableName(value = "im_blacklist", autoResultMap = true)
@KeySequence("im_blacklist_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImBlacklistDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 操作用户编号（谁拉的黑）
     */
    private Long userId;

    /**
     * 被拉黑用户编号
     */
    private Long blockId;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 拉黑时间
     */
    private LocalDateTime createdTime;

}
