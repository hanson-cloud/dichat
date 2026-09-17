package com.diqin.cloud.module.im.dal.dataobject.review;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 消息审核 DO
 *
 * <p>多媒体消息审核队列记录。图片(102)、语音(103)、视频(104)、文件(105) 类消息
 * 在发送后自动入此队列，由管理后台审核人处理。零改已有消息表。
 *
 * @author 速构构
 */
@TableName(value = "im_message_review", autoResultMap = true)
@KeySequence("im_message_review_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImMessageReviewDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 消息编号（im_private_message / im_group_message 的 id）
     */
    private Long messageId;
    /**
     * 会话类型：1-私聊 2-群聊
     */
    private Integer chatType;
    /**
     * 消息类型：102-图片 103-语音 104-视频 105-文件
     */
    private Integer msgType;
    /**
     * 发送人编号
     */
    private Long senderId;
    /**
     * 内容预览（原始内容 JSON，前端解析展示）
     */
    private String contentPreview;
    /**
     * 审核状态：0-待审核 1-审核通过 2-审核驳回
     */
    private Integer reviewStatus;
    /**
     * 审核人编号
     */
    private Long reviewerId;
    /**
     * 驳回原因
     */
    private String reviewReason;
    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

}
