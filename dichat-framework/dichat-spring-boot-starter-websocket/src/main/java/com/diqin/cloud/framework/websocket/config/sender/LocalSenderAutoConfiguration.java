package com.diqin.cloud.framework.websocket.config.sender;

import com.diqin.cloud.framework.websocket.config.DichatWebSocketAutoConfiguration;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.local.LocalWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 本地消息发送配置
 * @author hanson
 */
@AutoConfiguration(after = DichatWebSocketAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dichat.websocket", name = "sender-type", havingValue = "local")
@Slf4j
public class LocalSenderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(WebSocketMessageSender.class)
    public WebSocketMessageSender localWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                                              WsOfflineMessageService wsOfflineMessageService,
                                                              WebSocketProperties webSocketProperties) {
        log.debug("WebSocket使用：本地消息发送器");
        return new LocalWebSocketMessageSender(sessionManager, wsOfflineMessageService, webSocketProperties);
    }
}
