package com.diqin.cloud.module.im.dal.dataobject.message;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import com.diqin.cloud.module.im.enums.message.ImMessageStatusEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 私聊消息 DO
 *
 * @author hanson
 */
@TableName("im_private_message")
@KeySequence("im_private_message_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImPrivateMessageDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 客户端消息编号，用于发送幂等
     */
    private String clientMessageId;
    /**
     * 发送人编号
     * 关联 ImUserDO 的 id 字段
     */
    private Long senderId;
    /**
     * 接收人编号
     * 关联 ImUserDO 的 id 字段
     */
    private Long receiverId;
    /**
     * 发送方类型：1-用户 2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     * <p>方案 C 引入：消息参与方不再限定为 im_users，客服/机器人作为独立参与方需携带类型维度寻址。
     */
    private Integer senderType;
    /**
     * 接收方类型：1-用户 2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     */
    private Integer receiverType;
    /**
     * 消息类型
     * <p>
     * 枚举 {@link ImMessageTypeEnum}
     */
    private Integer type;
    /**
     * 消息内容，JSON 格式
     * 参考 content 包下的 TextMessage、ImageMessage 等结构化模型
     */
    private String content;
    /**
     * 消息状态
     * <p>
     * 枚举 {@link ImMessageStatusEnum}
     */
    private Integer status;
    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

}
