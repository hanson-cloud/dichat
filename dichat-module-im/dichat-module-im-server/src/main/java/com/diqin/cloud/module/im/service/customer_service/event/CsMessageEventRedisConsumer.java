package com.diqin.cloud.module.im.service.customer_service.event;

import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.framework.mq.redis.core.pubsub.AbstractRedisChannelMessageListener;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * {@link CsMessageEventRedisMessage} 的消费者：收到 Redis 广播后，转交<b>本实例</b>的
 * {@link CsMessageEventBus#publishLocal(Long, ImPrivateMessageDO)} 做 SSE 本地投递。
 * <p>必须声明为 Spring Bean（@Component），才能被 {@code DichatRedisMQConsumerAutoConfiguration}
 * 收集并自动注册到对应 Redis 频道；否则不会消费消息，SSE 实时推送将彻底失效。</p>
 *
 * @author 速构构
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CsMessageEventRedisConsumer extends AbstractRedisChannelMessageListener<CsMessageEventRedisMessage> {

    private final CsMessageEventBus csMessageEventBus;

    @Override
    public void onMessage(CsMessageEventRedisMessage message) {
        // 跳过「自己发出的」消息：发布实例已通过 CsMessageEventBus.publishLocal 直推过本实例，
        // 若再经 Redis 回收到一遍会重复投递。
        if (message.getInstanceId() != null
                && message.getInstanceId().equals(csMessageEventBus.getInstanceId())) {
            return;
        }
        try {
            ImPrivateMessageDO msg = JsonUtils.parseObject(message.getMessageJson(), ImPrivateMessageDO.class);
            csMessageEventBus.publishLocal(message.getCsId(), msg);
        } catch (Exception e) {
            log.error("[CsMessageEventRedisConsumer][消费客服消息失败 csId({})]", message.getCsId(), e);
        }
    }

}
