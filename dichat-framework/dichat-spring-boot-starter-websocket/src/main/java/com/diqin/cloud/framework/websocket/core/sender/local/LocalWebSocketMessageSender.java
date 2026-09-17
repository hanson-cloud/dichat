package com.diqin.cloud.framework.websocket.core.sender.local;

import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类
 * 注意：仅仅适合单机场景！！！
 *
 * @author hanson
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager, WsOfflineMessageService wsOfflineMessageService, WebSocketProperties webSocketProperties) {
        super(sessionManager,wsOfflineMessageService,webSocketProperties);
    }

}
