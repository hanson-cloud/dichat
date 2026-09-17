package com.diqin.cloud.module.im.dal.dataobject.robot;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

/**
 * IM 机器人自动回复规则 DO
 *
 * <p>每条规则绑定到一个机器人，命中关键词后由机器人自动回复 {@link #replyContent}。
 *
 * @author 速构构
 */
@TableName(value = "im_robot_reply_rule", autoResultMap = true)
@KeySequence("im_robot_reply_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImRobotReplyRuleDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 机器人编号
     */
    private Long robotId;
    /**
     * 触发关键词
     */
    private String keyword;
    /**
     * 匹配方式：1-精确匹配 2-包含匹配 3-正则匹配
     */
    private Integer matchType;
    /**
     * 回复类型：1-文本 2-转人工 3-常见问题
     */
    private Integer replyType;
    /**
     * 常见问题问题文本（reply_type=3 时展示）
     */
    private String question;
    /**
     * 回复内容（reply_type=1/3 时作为答案，reply_type=2 时可空）
     */
    private String replyContent;
    /**
     * 状态：0-停用 1-启用
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;

}
