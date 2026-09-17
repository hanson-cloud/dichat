package com.diqin.cloud.framework.websocket.core.session;

import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link WebSocketSession} 管理器的接口
 *
 * @author hanson
 */
public interface WebSocketSessionManager {

    /**
     * 添加 Session
     *
     * @param session Session
     */
    void addSession(WebSocketSession session);

    void updateHeartbeat(WebSocketSession session);

    void checkHeartbeat();

    /**
     * 移除 Session
     *
     * @param session Session
     */
    void removeSession(WebSocketSession session);

    /**
     * 获得指定编号的 Session
     *
     * @param id Session 编号
     * @return Session
     */
    WebSocketSession getSession(String id);

    /**
     * 获得指定用户类型的 Session 列表
     *
     * @param userType 用户类型
     * @return Session 列表
     */
    Collection<WebSocketSession> getSessionList(Integer userType);

    /**
     * 获取会话映射的方法
     * 该方法返回一个并发映射，用于存储特定用户类型的WebSocket会话列表
     *
     * @param userType 用户类型，用于区分不同用户的会话
     * @return ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>>
     * 键为用户ID(Long类型)，值为该用户对应的WebSocket会话列表(CopyOnWriteArrayList类型)
     * 使用ConcurrentMap保证线程安全，CopyOnWriteArrayList保证在遍历时的线程安全
     */
    ConcurrentMap<Long, CopyOnWriteArrayList<WebSocketSession>> getSessionMap(Integer userType);


    /**
     * 获得指定用户编号的 Session 列表
     *
     * @param userType 用户类型
     * @param userId   用户编号
     * @return Session 列表
     */
    Collection<WebSocketSession> getSessionList(Integer userType, Long userId);

    /**
     * 获取指定设备的会话
     *
     * @param id 连接对象ID
     * @return WebSocketSession 对象，如果不存在则返回 null
     */
    Collection<WebSocketSession> getSessions(Long id);

    /**
     * 获取当前连接的数量
     *
     * @param userType 用户类型
     * @return 返回当前连接的数量
     */
    int getConnectionCount(Integer userType);
}
