package com.diqin.cloud.module.im.service.customer_service.event;

import jakarta.annotation.Resource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 客服消息事件监听器
 * <p>将 {@link CsMessageEvent} 转交 {@link CsMessageEventBus} 进行 SSE 广播。</p>
 *
 * @author 速构构
 */
@Component
public class CsMessageEventListener {

    @Resource
    private CsMessageEventBus csMessageEventBus;

    @EventListener(CsMessageEvent.class)
    public void onEvent(CsMessageEvent event) {
        csMessageEventBus.publish(event);
    }

}
