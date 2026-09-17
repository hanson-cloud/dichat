package com.diqin.cloud.framework.websocket.config.sender;

import com.diqin.cloud.framework.websocket.config.DichatWebSocketAutoConfiguration;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.rocketmq.RocketMQWebSocketMessageConsumer;
import com.diqin.cloud.framework.websocket.core.sender.rocketmq.RocketMQWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * RocketMQ 消息发送配置
 * @author hanson
 */
@AutoConfiguration(after = DichatWebSocketAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dichat.websocket", name = "sender-type", havingValue = "rocketmq")
public class RocketMQSenderAutoConfiguration {

    @Resource
    private WebSocketProperties properties;

    @Bean
    @ConditionalOnMissingBean(WebSocketMessageSender.class)
    public WebSocketMessageSender webSocketMessageSender(WebSocketSessionManager sessionManager,
                                                         RocketMQTemplate template,
                                                         WsOfflineMessageService wsOfflineMessageService,
                                                         WebSocketProperties webSocketProperties) {
        return new RocketMQWebSocketMessageSender(sessionManager, template, properties.getSenderRocketmq().getTopic(), wsOfflineMessageService, webSocketProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RocketMQWebSocketMessageConsumer webSocketMessageConsumer(WebSocketMessageSender sender) {
        return new RocketMQWebSocketMessageConsumer((RocketMQWebSocketMessageSender) sender);
    }
}
