package com.diqin.cloud.framework.websocket.config;

import com.diqin.cloud.framework.mq.redis.config.DichatRedisMQConsumerAutoConfiguration;
import com.diqin.cloud.framework.websocket.config.sender.*;
import com.diqin.cloud.framework.websocket.core.handler.JsonWebSocketMessageHandler;
import com.diqin.cloud.framework.websocket.core.listener.WebSocketMessageListener;
import com.diqin.cloud.framework.websocket.core.offline.init.OfflineMessageTableInitializer;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageServiceImpl;
import com.diqin.cloud.framework.websocket.core.security.LoginUserHandshakeInterceptor;
import com.diqin.cloud.framework.websocket.core.security.WebSocketAuthorizeRequestsCustomizer;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionHandlerDecorator;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.WebSocketHandler;

import javax.sql.DataSource;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

/**
 * WebSocket 自动配置
 *
 * @author xingyu4j
 */
@AutoConfiguration(before = DichatRedisMQConsumerAutoConfiguration.class) // before DichatRedisMQConsumerAutoConfiguration 的原因是，需要保证 RedisWebSocketMessageConsumer 先创建，才能创建 RedisMessageListenerContainer
@MapperScan(basePackages = "com.diqin.cloud.framework.websocket.core.offline.dal.mysql")
@EnableScheduling
@EnableWebSocket // 开启 websocket
@ConditionalOnProperty(prefix = "dichat.websocket", value = "enable", matchIfMissing = true) // 允许使用 dichat.websocket.enable=false 禁用 websocket
@EnableConfigurationProperties(WebSocketProperties.class)
@Slf4j
@Import({
        LocalSenderAutoConfiguration.class,
        RedisSenderAutoConfiguration.class,
        RocketMQSenderAutoConfiguration.class,
        RabbitMQSenderAutoConfiguration.class,
        KafkaSenderAutoConfiguration.class
})
@ComponentScan(basePackages = "com.diqin.cloud.framework.websocket.core.offline")
public class DichatWebSocketAutoConfiguration {

    /** 注册 WebSocket Endpoint */
    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor[] interceptors,
                                                   WebSocketHandler handler,
                                                   WebSocketProperties properties) {
        return registry -> registry
                .addHandler(handler, properties.getPath())
                .addInterceptors(interceptors)
                .setAllowedOriginPatterns("*");
    }

    /** 离线消息表自动初始化（通过 @Bean 注册，避免 starter 中 @Component 无法被组件扫描的问题） */
    @Bean
    @ConditionalOnProperty(prefix = "dichat.websocket", name = "offline-message-enabled", havingValue = "true")
    public OfflineMessageTableInitializer offlineMessageTableInitializer(DataSource dataSource) {
        return new OfflineMessageTableInitializer(dataSource);
    }

    /** 离线消息服务 */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "dichat.websocket", name = "offline-message-enabled", havingValue = "true")
    public WsOfflineMessageService wsOfflineMessageService(WebSocketProperties webSocketProperties) {
        log.info("离线消息功能是否启用: {}", webSocketProperties.getOfflineMessageEnabled());
        return new WsOfflineMessageServiceImpl(webSocketProperties);
    }

    /** 登录用户握手拦截器 */
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new LoginUserHandshakeInterceptor();
    }

    @Bean
    public WebSocketSessionManager webSocketSessionManager(WebSocketProperties webSocketProperties,
                                                           WsOfflineMessageService wsOfflineMessageService) {
        return new WebSocketSessionManagerImpl(webSocketProperties, wsOfflineMessageService);
    }

    /** WebSocket 核心消息处理器 */
    @Bean
    public WebSocketHandler webSocketHandler(WebSocketSessionManager sessionManager,
                                             List<? extends WebSocketMessageListener<?>> listeners) {
        JsonWebSocketMessageHandler messageHandler = new JsonWebSocketMessageHandler(listeners,sessionManager);
        return new WebSocketSessionHandlerDecorator(messageHandler, sessionManager);
    }

    /** Spring Security 鉴权配置适配器 */
    @Bean
    public WebSocketAuthorizeRequestsCustomizer webSocketAuthorizeRequestsCustomizer(WebSocketProperties properties) {
        return new WebSocketAuthorizeRequestsCustomizer(properties);
    }

}