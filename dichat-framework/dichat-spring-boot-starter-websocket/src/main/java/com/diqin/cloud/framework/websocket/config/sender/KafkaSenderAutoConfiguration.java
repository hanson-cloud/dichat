package com.diqin.cloud.framework.websocket.config.sender;

import com.diqin.cloud.framework.websocket.config.DichatWebSocketAutoConfiguration;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.kafka.KafkaWebSocketMessageConsumer;
import com.diqin.cloud.framework.websocket.core.sender.kafka.KafkaWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Kafka 消息发送配置
 * @author hanson
 */
@AutoConfiguration(after = DichatWebSocketAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dichat.websocket", name = "sender-type", havingValue = "kafka")
public class KafkaSenderAutoConfiguration {

    @Resource
    private WebSocketProperties properties;

    @Bean
    @ConditionalOnMissingBean(WebSocketMessageSender.class)
    public WebSocketMessageSender webSocketMessageSender(WebSocketSessionManager sessionManager,
                                                         KafkaTemplate<Object, Object> kafkaTemplate,
                                                         WsOfflineMessageService wsOfflineMessageService,
                                                         WebSocketProperties webSocketProperties) {
        return new KafkaWebSocketMessageSender(sessionManager, kafkaTemplate, properties.getSenderKafka().getTopic(), wsOfflineMessageService, webSocketProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaWebSocketMessageConsumer webSocketMessageConsumer(WebSocketMessageSender sender) {
        return new KafkaWebSocketMessageConsumer((KafkaWebSocketMessageSender) sender);
    }
}
