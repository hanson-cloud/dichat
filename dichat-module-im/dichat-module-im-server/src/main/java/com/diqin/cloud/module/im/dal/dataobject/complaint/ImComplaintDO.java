package com.diqin.cloud.module.im.dal.dataobject.complaint;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 用户投诉（举报） DO
 *
 * @author dichat
 */
@TableName(value = "im_complaint", autoResultMap = true)
@KeySequence("im_complaint_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImComplaintDO extends TenantBaseDO {

    /**
     * 投诉编号
     */
    @TableId
    private Long id;

    /**
     * 投诉人用户编号
     */
    private Long complainantId;

    /**
     * 被投诉人用户编号
     */
    private Long respondentId;

    /**
     * 投诉类别
     * <p>
     * 1=骚扰，2=诈骗，3=色情，4=虚假信息，5=其他违规
     */
    private Integer category;

    /**
     * 投诉详细描述
     */
    private String content;

    /**
     * 证据截图 / 文件 URL（逗号分隔）
     */
    private String evidenceUrls;

    /**
     * 关联的消息编号（针对某条消息的投诉）
     */
    private Long sourceMsgId;

    /**
     * 关联的会话编号
     */
    private Long sourceChatId;

    /**
     * 处理状态
     * <p>
     * 0=待处理，1=处理中，2=已处理，3=已驳回
     */
    private Integer status;

    /**
     * 处理结果描述
     */
    private String handleResult;

    /**
     * 处罚措施
     * <p>
     * 1=警告，2=禁言，3=封号，4=无处罚
     */
    private Integer punishment;

    /**
     * 处理人管理员编号
     */
    private Long handleUserId;

    /**
     * 处理完成时间
     */
    private LocalDateTime handleTime;

}
