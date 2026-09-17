package com.diqin.cloud.module.im.dal.dataobject.customer_service;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 客服 DO
 *
 * <p>客服是平台内的在线服务坐席账号，独立于普通用户（im_users 表不做类型区分），
 * 由管理后台统一创建与配置，可关联真实用户编号（可选）。
 *
 * @author 速构构
 */
@TableName(value = "im_customer_service", autoResultMap = true)
@KeySequence("im_customer_service_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImCustomerServiceDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 客服工号/账号（全局唯一）
     */
    private String username;
    /**
     * 客服昵称
     */
    private String nickname;
    /**
     * 头像地址
     */
    private String avatar;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 关联管理后台账号编号（system_users.id）
     * <p>管理端「客服工作台」据此识别当前登录管理员对应的客服身份；为空表示该客服仅作为通用坐席被自动分配，不绑定特定管理员。
     */
    private Long adminUserId;
    /**
     * 状态：0-离线 1-在线 2-忙碌
     */
    private Integer status;
    /**
     * 最大并发会话数
     */
    private Integer maxConcurrent;
    /**
     * 欢迎语
     */
    private String welcomeMsg;
    /**
     * 是否开启自动回复：0-关闭 1-开启
     */
    private Boolean autoReplyEnabled;
    /**
     * 是否自动分配会话：0-关闭 1-开启
     */
    private Boolean autoAssign;
    /**
     * 排序
     */
    private Integer sort;

}
