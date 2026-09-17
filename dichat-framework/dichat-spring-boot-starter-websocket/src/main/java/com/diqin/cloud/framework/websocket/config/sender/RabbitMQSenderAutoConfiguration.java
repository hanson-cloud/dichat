package com.diqin.cloud.framework.websocket.config.sender;

import com.diqin.cloud.framework.websocket.config.DichatWebSocketAutoConfiguration;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageConsumer;
import com.diqin.cloud.framework.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * RabbitMQ 消息发送配置
 *
 * @author hanson
 */
@AutoConfiguration(after = DichatWebSocketAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dichat.websocket", name = "sender-type", havingValue = "rabbitmq")
public class RabbitMQSenderAutoConfiguration {

    @Resource
    private WebSocketProperties properties;

    @Bean
    @ConditionalOnMissingBean
    public TopicExchange websocketTopicExchange() {
        return new TopicExchange(properties.getSenderRabbitmq().getExchange(), true, false);
    }

    @Bean
    @ConditionalOnMissingBean(WebSocketMessageSender.class)
    public WebSocketMessageSender webSocketMessageSender(WebSocketSessionManager sessionManager,
                                                         RabbitTemplate rabbitTemplate,
                                                         TopicExchange exchange,
                                                         WsOfflineMessageService wsOfflineMessageService,
                                                         WebSocketProperties webSocketProperties) {
        return new RabbitMQWebSocketMessageSender(sessionManager, rabbitTemplate, exchange, wsOfflineMessageService, webSocketProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitMQWebSocketMessageConsumer webSocketMessageConsumer(WebSocketMessageSender sender) {
        return new RabbitMQWebSocketMessageConsumer((RabbitMQWebSocketMessageSender) sender);
    }
}
