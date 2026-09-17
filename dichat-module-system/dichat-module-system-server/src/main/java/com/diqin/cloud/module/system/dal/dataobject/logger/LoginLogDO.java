package com.diqin.cloud.module.system.dal.dataobject.logger;

import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.mybatis.core.dataobject.BaseDO;
import com.diqin.cloud.module.system.enums.logger.LoginLogTypeEnum;
import com.diqin.cloud.module.system.enums.logger.LoginResultEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 登录日志表
 * 注意，包括登录和登出两种行为
 *
 * @author hanson
 */
@TableName("system_login_log")
@KeySequence("system_login_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoginLogDO extends BaseDO {

    /**
     * 日志主键
     */
    private Long id;
    /**
     * 日志类型
     * 枚举 {@link LoginLogTypeEnum}
     */
    private Integer logType;
    /**
     * 链路追踪编号
     */
    private String traceId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型
     * 枚举 {@link UserTypeEnum}
     */
    private Integer userType;
    /**
     * 用户账号
     * 冗余，因为账号可以变更
     */
    private String username;
    /**
     * 登录结果
     * 枚举 {@link LoginResultEnum}
     */
    private Integer result;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 浏览器 UA
     */
    private String userAgent;

    /**
     * 终端类型
     * <p>
     * 枚举 {@link com.diqin.cloud.framework.common.enums.TerminalEnum}
     */
    private Integer terminal;

    /**
     * 登录状态：1-在线 2-已离线 3-已强制下线
     * <p>
     * 枚举 {@link com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum}
     */
    private Integer status;

    /**
     * 登出时间（主动 / 强制 / 超时）
     */
    private LocalDateTime logoutTime;

}
