package com.diqin.cloud.framework.websocket.core.session;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.diqin.cloud.framework.common.enums.MessageTypeConstants;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.framework.security.core.LoginUser;
import com.diqin.cloud.framework.tenant.core.context.TenantContextHolder;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.message.JsonWebSocketMessage;
import com.diqin.cloud.framework.websocket.core.offline.dal.dataobject.WsOfflineMessageDO;
import com.diqin.cloud.framework.websocket.core.offline.service.WsOfflineMessageService;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.framework.websocket.core.util.WebSocketFrameworkUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 基于 {@link WebSocketSessionManager} 实现类
 *
 * @author hanson
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionManagerImpl implements WebSocketSessionManager, ApplicationEventPublisherAware {

    private final WebSocketProperties properties;
    private final WsOfflineMessageService wsOfflineMessageService;

    /**
     * 本地缓存：id 与 WebSocketSession 映射
     * 注意：WebSocketSession 无法序列化，所以只在本地存储
     */
    private final ConcurrentMap<String, WebSocketSession> idSessions = new ConcurrentHashMap<>();

    // 心跳管理
    private final Map<String, Long> lastHeartbeatTimeMap = new ConcurrentHashMap<>();

    /**
     * 事件发布器，用于会话关闭时通知业务侧
     */
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 连接对象 与 WebSocketSession 映射
     * key1：用户类型
     * key2：用户编号
     */
    private final ConcurrentMap<Integer, ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>> sessionsMap
            = new ConcurrentHashMap<>();

    @Override
    public void addSession(WebSocketSession session) {
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);

        if (user == null) {
            log.warn("[addSession] 会话没有用户信息，sessionId: {}", session.getId());
            try {
                session.close();
            } catch (IOException e) {
                log.error("session 关闭异常");
            }
            return;
        }

        // 添加到 idSessions 中
        idSessions.put(session.getId(), session);
        updateHeartbeat(session);

        Integer type = user.getUserType();

        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = sessionsMap.get(type);
        if (userSessionsMap == null) {
            userSessionsMap = new ConcurrentHashMap<>();
            if (sessionsMap.putIfAbsent(type, userSessionsMap) != null) {
                userSessionsMap = sessionsMap.get(type);
            }
        }
        Long id = user.getId();
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(id);
        if (sessions == null) {
            sessions = new CopyOnWriteArrayList<>();
            if (userSessionsMap.putIfAbsent(id, sessions) != null) {
                sessions = userSessionsMap.get(id);
                sessions.forEach(s -> {
                    Map<String, String> contentMap = new HashMap<>();
                    contentMap.put("message", MessageTypeConstants.LOGOUT.getDesc());

                    JsonWebSocketMessage message = new JsonWebSocketMessage().setType(MessageTypeConstants.LOGOUT.getCode()).setContent(JsonUtils.toJsonString(contentMap));
                    String payload = JsonUtils.toJsonString(message);
                    try {
                        s.sendMessage(new TextMessage(payload));
                        s.close();
                        idSessions.remove(s.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        sessions.add(session);

        // 5. 发送离线消息
        if (Boolean.TRUE.equals(properties.getOfflineMessageEnabled())) {
            sendOfflineMessagesOnConnect(user, session);
        }

        log.info("[addSession] 添加会话成功，sessionId: {}, userType: {}, 对象id: {}, 当前会话数量：{}",
                session.getId(), type, id, idSessions.size());
    }

    @Override
    public void updateHeartbeat(WebSocketSession session) {
        if (session != null && session.isOpen()) {
            lastHeartbeatTimeMap.put(session.getId(), System.currentTimeMillis());
        }
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void checkHeartbeat() {
        long now = System.currentTimeMillis();
        // 获取当前所有活跃会话（从 idSessions 中获取，避免重复维护）
        Collection<WebSocketSession> allSessions = new ArrayList<>(idSessions.values());
        for (WebSocketSession session : allSessions) {
            String sessionId = session.getId();
            Long lastTime = lastHeartbeatTimeMap.get(sessionId);
            if (lastTime == null) {
                // 从未收到过任何消息，保留但不关闭（可依据需求决定）
                continue;
            }
            if (properties.getHeartbeat().getEnabled() && now - lastTime > properties.getHeartbeat().getTimeout() * 1000L) {
                try {
                    log.warn("[checkHeartbeat] 会话 {} 心跳超时，即将关闭", sessionId);
                    session.close(CloseStatus.SESSION_NOT_RELIABLE);
                    // 关闭操作会触发 WebSocketSessionHandlerDecorator.afterConnectionClosed，
                    // 其中会调用 sessionManager.removeSession，因此无需手动移除
                } catch (IOException e) {
                    log.error("[checkHeartbeat] 关闭超时会话失败", e);
                } finally {
                    lastHeartbeatTimeMap.remove(sessionId);
                }
            }
        }
    }

    @Override
    public void removeSession(WebSocketSession session) {
        String sessionId = session.getId();
        // 1. 从本地缓存移除
        WebSocketSession removedSession = idSessions.remove(sessionId);
        lastHeartbeatTimeMap.remove(session.getId());
        // 移除从 idSessions 中
        LoginUser user = WebSocketFrameworkUtils.getLoginUser(session);
        if (user == null) {
            return;
        }
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = sessionsMap.get(user.getUserType());
        if (userSessionsMap == null) {
            return;
        }
        Long id = user.getId();
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(id);
        sessions.removeIf(session0 -> session0.getId().equals(sessionId));
        if (CollUtil.isEmpty(sessions)) {
            userSessionsMap.remove(id, sessions);
        }
        // 发布会话关闭事件，通知业务模块处理下线逻辑（如更新登录日志离线状态）
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(new WebSocketSessionClosedEvent(
                    this, user.getUserType(), user.getId(),
                    WebSocketFrameworkUtils.getTerminal(session), sessionId));
        }

        if (removedSession != null) {
            log.info("[removeSession] 移除会话成功，sessionId: {}", sessionId);
        } else {
            log.warn("[removeSession] 会话不存在于本地缓存中，sessionId: {}", sessionId);
        }
    }

    @Override
    public WebSocketSession getSession(String id) {
        return idSessions.get(id);
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer type) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = sessionsMap.get(type);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        // 避免扩容
        LinkedList<WebSocketSession> result = new LinkedList<>();
        Long contextTenantId = TenantContextHolder.getTenantId();
        for (List<WebSocketSession> sessions : userSessionsMap.values()) {
            if (CollUtil.isNotEmpty(sessions)) {
                // 特殊：如果租户不匹配，则直接排除
                if (contextTenantId == null) {
                    // 没有租户上下文限制，直接添加所有会话
                    result.addAll(sessions);
                } else {
                    Long userTenantId = WebSocketFrameworkUtils.getTenantId(sessions.getFirst());
                    if (contextTenantId.equals(userTenantId)) {
                        result.addAll(sessions);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> getSessionMap(Integer type) {
        return sessionsMap.get(type);
    }

    @Override
    public Collection<WebSocketSession> getSessionList(Integer userType, Long userId) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = sessionsMap.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return new ArrayList<>();
        }
        CopyOnWriteArrayList<WebSocketSession> sessions = userSessionsMap.get(userId);
        return CollUtil.isNotEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
    }

    @Override
    public Collection<WebSocketSession> getSessions(Long id) {
        if (id == null) {
            return new ArrayList<>();
        }
        List<WebSocketSession> sessions = getSessionByType(UserTypeEnum.MEMBER.getValue(), id);
        if (CollectionUtils.isEmpty(sessions)) {
            sessions = getSessionByType(UserTypeEnum.ADMIN.getValue(), id);
        }
        log.debug("[getUserSession] 获取用户会话成功，userId: {}, session数量: {}", id, sessions.size());
        return sessions;
    }

    @Override
    public int getConnectionCount(Integer userType) {
        if (userType == null) {
            return idSessions.size();
        }
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> userSessionsMap = sessionsMap.get(userType);
        if (CollUtil.isEmpty(userSessionsMap)) {
            return 0;
        }
        return userSessionsMap.values().stream().mapToInt(List::size).sum();
    }

    /**
     * 用户连接时发送离线消息
     */
    private void sendOfflineMessagesOnConnect(LoginUser user, WebSocketSession session) {
        Long tenantId = TenantContextHolder.getTenantId();
        Integer maxBatchSize = properties.getOfflineMessage().getMaxBatchSize();
        Long userId = user.getId();
        Integer userType = user.getUserType();
        List<WsOfflineMessageDO> messages = wsOfflineMessageService.pullAndSendOfflineMessages(
                tenantId != null ? tenantId : 0L,
                userType,
                userId,
                maxBatchSize
        );

        List<Long> successIds = new ArrayList<>();
        for (WsOfflineMessageDO message : messages) {
            try {
                WebSocketMessageSender webSocketMessageSender = SpringUtil.getApplicationContext().getBean(WebSocketMessageSender.class);
                webSocketMessageSender.send(message.getUserType(), message.getUserId(), message.getMessageType(), message.getMessageContent());
                successIds.add(message.getId());
                log.debug("[sendOfflineMessagesOnConnect] 发送离线消息成功，messageId: {}, userType: {}, userId: {}",
                        message.getId(), message.getUserType(), message.getUserId());
            } catch (Exception e) {
                wsOfflineMessageService.incrementRetryCount(message.getId());
                log.error("[sendOfflineMessagesOnConnect] 发送离线消息失败，messageId: {}", message.getId(), e);
            }
        }

        // 更新已成功发送的消息状态
        if (!successIds.isEmpty()) {
            wsOfflineMessageService.updateSentByIds(successIds);
            log.info("[sendOfflineMessagesOnConnect] 用户连接时发送离线消息 {} 条，userType: {}, userId: {}, sessionId: {}",
                    successIds.size(), userType, userId, session.getId());
        }
    }

    /**
     * 根据用户获取会话元数据
     */
    private List<WebSocketSession> getSessionByType(Integer type, Long id) {
        ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> concurrentMap = sessionsMap.get(type);
        if (concurrentMap == null) {
            return new ArrayList<>();

        }
        CopyOnWriteArrayList<WebSocketSession> sessions = concurrentMap.get(id);
        return CollUtil.isNotEmpty(sessions) ? new ArrayList<>(sessions) : new ArrayList<>();
    }
}
