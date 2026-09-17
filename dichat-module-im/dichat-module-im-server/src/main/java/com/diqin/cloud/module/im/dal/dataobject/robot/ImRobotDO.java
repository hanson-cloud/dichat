package com.diqin.cloud.module.im.dal.dataobject.robot;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 机器人 DO
 *
 * <p>机器人是平台内的虚拟自动服务账号，独立于普通用户（im_users 表不做类型区分），
 * 由管理后台统一创建与配置。自动回复规则见 {@link ImRobotReplyRuleDO}。
 *
 * @author 速构构
 */
@TableName(value = "im_robot", autoResultMap = true)
@KeySequence("im_robot_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImRobotDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 机器人登录账号（全局唯一）
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * 描述
     */
    private String description;
    /**
     * 回调地址（接收平台事件推送，可选）
     */
    private String webhookUrl;
    /**
     * 是否开启自动回复：0-关闭 1-开启
     */
    private Boolean autoReplyEnabled;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
