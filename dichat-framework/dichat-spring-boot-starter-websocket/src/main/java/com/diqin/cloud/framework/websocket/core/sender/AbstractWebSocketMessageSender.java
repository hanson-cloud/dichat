package com.diqin.cloud.framework.websocket.core.sender;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.diqin.cloud.framework.common.enums.MessageTypeConstants;
import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.message.JsonWebSocketMessage;
import com.diqin.cloud.framework.websocket.core.offline.dal.dataobject.WsOfflineMessageDO;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocketMessageSender 实现类
 *
 * @author hanson
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractWebSocketMessageSender implements WebSocketMessageSender {

    private final WebSocketSessionManager sessionManager;
    private final WsOfflineMessageService wsOfflineMessageService;
    private final WebSocketProperties webSocketProperties;

    @Override
    public int getConnectionCount(Integer userType){
        return sessionManager.getConnectionCount(userType);
    }

    @Override
    public ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> getSessionMap(Integer userType){
        return sessionManager.getSessionMap(userType);
    }

    @Override
    public void send(Integer userType, Long userId, String messageType, String messageContent) {
        send(null, userType, userId, messageType, messageContent);
    }

    @Override
    public void send(Integer userType, String messageType, String messageContent) {
        send(null, userType, null, messageType, messageContent);
    }

    @Override
    public void send(String sessionId, String messageType, String messageContent) {
        send(sessionId, null, null, messageType, messageContent);
    }

    /**
     * 发送消息
     *
     * @param sessionId      Session 编号
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    public void send(String sessionId, Integer userType, Long userId, String messageType, String messageContent) {
        // 1. 获得 Session 列表
        List<WebSocketSession> sessions = Collections.emptyList();
        if (CharSequenceUtil.isNotEmpty(sessionId)) {
            WebSocketSession session = sessionManager.getSession(sessionId);
            if (session != null) {
                sessions = Collections.singletonList(session);
            }
        } else if (userType != null && userId != null) {
            sessions = (List<WebSocketSession>) sessionManager.getSessionList(userType, userId);
        } else if (userId != null) {
            sessions = (List<WebSocketSession>) sessionManager.getSessions(userId);
        }else if (userType != null) {
            sessions = (List<WebSocketSession>) sessionManager.getSessionList(userType);
        }

        // 2. 检查是否需要处理离线消息
        boolean needHandleOfflineMessage = webSocketProperties.getOfflineMessageEnabled()
                                                                                //离线消息不保存登出或挤出登录状态的消息，避免session的异常关闭
                && userType != null && userId != null && sessions.isEmpty() && !messageType.equals(MessageTypeConstants.LOGOUT.getCode());

        if (CollUtil.isEmpty(sessions)) {
            if (!MessageTypeConstants.LOGOUT.getCode().equals(messageType) && needHandleOfflineMessage) {
                // 用户不在线，保存为离线消息
                handleOfflineMessage(userType, userId, messageType, messageContent);
            }
            return;
        }

        // 3. 执行发送
        doSend(sessions, messageType, messageContent);
    }

    /**
     * 处理离线消息
     */
    private void handleOfflineMessage(Integer userType, Long userId, String messageType, String messageContent) {
        try {
            Integer expireDays = webSocketProperties.getOfflineMessage().getExpireDays();
            WsOfflineMessageDO offlineMessage = new WsOfflineMessageDO()
                    .setUserType(userType)
                    .setUserId(userId)
                    .setMessageType(messageType)
                    .setMessageContent(messageContent)
                    .setExpireTime(expireDays != null ? LocalDateTime.now().plusDays(expireDays) : null)
                    .setSent(false)
                    .setRetryCount(0)
                    .setMaxRetryCount(webSocketProperties.getOfflineMessage().getMaxRetryCount());
            wsOfflineMessageService.save(offlineMessage);
            log.info("[handleOfflineMessage] 用户不在线，消息已保存为离线消息，userType: {}, userId: {}, messageType: {}",
                    userType, userId, messageType);
        } catch (Exception e) {
            log.error("[handleOfflineMessage] 保存离线消息失败，userType: {}, userId: {}, messageType: {}",
                    userType, userId, messageType, e);
        }
    }

    /**
     * 发送消息的具体实现
     *
     * @param sessions       Session 列表
     * @param messageType    消息类型
     * @param messageContent 消息内容
     */
    public void doSend(Collection<WebSocketSession> sessions, String messageType, String messageContent) {
        JsonWebSocketMessage message = new JsonWebSocketMessage().setType(messageType).setContent(messageContent);
        String payload = JsonUtils.toJsonString(message);
        sessions.forEach(session -> {
            // 1. 各种校验，保证 Session 可以被发送
            if (session == null) {
                log.error("[doSend][session 为空, message({})]", message);
                return;
            }
            if (!session.isOpen()) {
                log.error("[doSend][session({}) 已关闭, message({})]", session.getId(), message);
                return;
            }
            // 2. 执行发送
            try {
                session.sendMessage(new TextMessage(payload));
                if (MessageTypeConstants.LOGOUT.getCode().equals(messageType)){
                    session.close();
                }
                log.info("[doSend][session({}) 发送消息成功，message({})]", session.getId(), message);
            } catch (IOException ex) {
                log.error("[doSend][session({}) 发送消息失败，message({})]", session.getId(), message, ex);
            }
        });
    }
}
