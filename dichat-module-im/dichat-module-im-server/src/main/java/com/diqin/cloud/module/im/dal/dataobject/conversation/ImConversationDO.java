package com.diqin.cloud.module.im.dal.dataobject.conversation;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.tenant.core.db.TenantBaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * IM 私聊会话 DO
 * <p>
 * 以「参与方配对」为维度聚合：参与方 A 固定为服务侧（机器人/客服，类型值更大者），
 * B 固定为 C 端用户（im_users.id）。会话列表 / 未读角标 / 置顶 / 软删均直接读此表，
 * 避免对 im_private_message 做运行时聚合。
 *
 * @author 速构构
 */
@TableName("im_conversation")
@KeySequence("im_conversation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImConversationDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 参与方A类型：1-用户 2-机器人 3-人工客服
     */
    private Integer participantAType;

    /**
     * 参与方A编号（服务侧：机器人/客服自身 id）
     */
    private Long participantAId;

    /**
     * 参与方B类型：1-用户 2-机器人 3-人工客服
     */
    private Integer participantBType;

    /**
     * 参与方B编号（C 端用户：im_users.id）
     */
    private Long participantBId;

    /**
     * 最近一条消息编号
     */
    private Long lastMessageId;

    /**
     * 最近一条消息内容（文本摘要，剔除 JSON 结构）
     */
    private String lastMessageContent;

    /**
     * 最近一条消息类型
     */
    private Integer lastMessageType;

    /**
     * 最近一条消息发送时间
     */
    private LocalDateTime lastMessageTime;

    /**
     * 参与方A未读消息数（A 视角：B 发来且未读）
     */
    private Integer aUnreadCount;

    /**
     * 参与方B未读消息数（B 视角：A 发来且未读）
     */
    private Integer bUnreadCount;

    /**
     * 参与方A置顶
     */
    private Boolean aPinned;

    /**
     * 参与方B置顶
     */
    private Boolean bPinned;

    /**
     * 参与方A删除（隐藏会话）
     */
    private Boolean aDeleted;

    /**
     * 参与方B删除（隐藏会话）
     */
    private Boolean bDeleted;

}
