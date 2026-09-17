package com.diqin.cloud.module.im.service.message.dto;

import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * IM 私聊消息发送 DTO
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
public class ImPrivateMessageSendDTO {

    /**
     * 接收人编号
     */
    private Long receiverId;
    /**
     * 发送方类型：1-用户 2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     * <p>
     * 为 null 时由 service 按默认 USER 处理；机器人 / 人工客服主动发送时必须显式指定
     */
    private Integer senderType;
    /**
     * 接收方类型：1-用户 2-机器人 3-人工客服（{@link com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum}）
     * <p>
     * 为 null 时由 service 按默认 USER 处理
     */
    private Integer receiverType;
    /**
     * 消息类型
     * <p>
     * 枚举 {@link ImMessageTypeEnum}
     */
    private Integer type;
    /**
     * 消息内容
     * <p>
     * 支持 String / POJO；非 String 时由 service 序列化为 JSON
     */
    private Object content;
    /**
     * 是否持久化 + 推送给接收方（单边语义开关）
     * <p>
     * null：默认按 {@link ImMessageTypeEnum#isPersistent()} 决定是否入库 + 双向 WS 推送（保持原行为）<br>
     * false：覆盖为单边——不入库、仅推 sender 多端，对方不感知（如「你已删除 XXX」这类仅自己可见的 TIP）<br>
     * true：覆盖为双向 + 入库
     */
    private Boolean persistent;

}
