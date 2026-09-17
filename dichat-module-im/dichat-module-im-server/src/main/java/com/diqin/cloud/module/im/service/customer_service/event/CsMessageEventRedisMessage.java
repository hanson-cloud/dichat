package com.diqin.cloud.module.im.service.customer_service.event;

import com.diqin.cloud.framework.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 客服消息 SSE 实时推送的 Redis 广播消息
 * <p>与 {@link CsMessageEventBus} 配合，把原本「进程内内存广播」的客服 SSE 推送桥接到
 * Redis Pub/Sub，使其在<b>多实例</b>部署下（A 实例落库、B 实例承载管理员工作台 SSE 连接）
 * 也能实时送达，与项目既有的 WebSocket 多实例方案（{@code RedisWebSocketMessage}）保持一致。</p>
 *
 * @author 速构构
 */
@Data
@Accessors(chain = true)
public class CsMessageEventRedisMessage extends AbstractRedisChannelMessage {

    /**
     * 客服编号（im_customer_service.id）—— SSE 订阅按此维度路由
     */
    private Long csId;

    /**
     * 发布实例编号：消费者据此跳过「自己发出的消息」，避免发布实例既收到进程内直推、
     * 又经 Redis 回收到一遍而导致同一条消息被投递两次。
     */
    private String instanceId;

    /**
     * 落库后的消息（{@code ImPrivateMessageDO} 的 JSON 字符串），由消费者反序列化后透传给 SSE
     */
    private String messageJson;

}
