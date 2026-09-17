package com.diqin.cloud.framework.websocket.core.sender.redis;

import com.diqin.cloud.framework.mq.redis.core.RedisMQTemplate;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Redis 的 {@link WebSocketMessageSender} 实现类
 *
 * @author hanson
 */
@Slf4j
public class RedisWebSocketMessageSender extends AbstractWebSocketMessageSender {

    private final RedisMQTemplate redisMQTemplate;

    public RedisWebSocketMessageSender(WebSocketSessionManager sessionManager,
                                       RedisMQTemplate template, WsOfflineMessageService wsOfflineMessageService, WebSocketProperties webSocketProperties) {
        super(sessionManager,wsOfflineMessageService, webSocketProperties);
        this.redisMQTemplate = template;
    }

    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        sendRedisMessage(null, userId, userType, messageType, messageContent);
    }

    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        sendRedisMessage(null, null, userType, messageType, messageContent);
    }

    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        sendRedisMessage(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 通过 Redis 广播消息
     *
     * @param sessionId Session 编号
     * @param userId 用户编号
     * @param userType 用户类型
     * @param messageType 消息类型
     * @param messageContent 消息内容
     */
    private void sendRedisMessage(String sessionId, Long userId, Integer userType,
                                  String messageType, String messageContent) {
        RedisWebSocketMessage mqMessage = new RedisWebSocketMessage()
                .setSessionId(sessionId).setUserId(userId).setUserType(userType)
                .setMessageType(messageType).setMessageContent(messageContent);
        log.debug("Send redis message: {}", mqMessage);
        redisMQTemplate.send(mqMessage);
    }

}
