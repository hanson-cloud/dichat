package com.diqin.cloud.framework.websocket.config.sender;

import com.diqin.cloud.framework.mq.redis.core.RedisMQTemplate;
import com.diqin.cloud.framework.websocket.config.DichatWebSocketAutoConfiguration;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.redis.RedisWebSocketMessageConsumer;
import com.diqin.cloud.framework.websocket.core.sender.redis.RedisWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Redis 消息发送配置
 * @author hanson
 */
@AutoConfiguration(after = DichatWebSocketAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dichat.websocket", name = "sender-type", havingValue = "redis")
@Slf4j
public class RedisSenderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WebSocketMessageSender.class)
    public WebSocketMessageSender webSocketMessageSender(WebSocketSessionManager sessionManager,
                                                         RedisMQTemplate template,
                                                         WsOfflineMessageService wsOfflineMessageService,
                                                         WebSocketProperties webSocketProperties) {
        log.info("WebSocket使用：redis消息发送器");
        return new RedisWebSocketMessageSender(sessionManager, template, wsOfflineMessageService, webSocketProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisWebSocketMessageConsumer webSocketMessageConsumer(RedisWebSocketMessageSender webSocketMessageSender) {
        return new RedisWebSocketMessageConsumer(webSocketMessageSender);
    }
}
