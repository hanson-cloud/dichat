package com.diqin.cloud.framework.websocket.core.sender;

import com.diqin.cloud.framework.common.util.json.JsonUtils;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WebSocket 消息的发送器接口
 *
 * @author hanson
 */
public interface WebSocketMessageSender {

    /**
     * 获取当前连接的数量
     * @param userType       用户类型
     * @return 返回当前连接的数量
     */
    int getConnectionCount(Integer userType);

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
     * 发送消息给指定用户
     *
     * @param userType       用户类型
     * @param userId         用户编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(Integer userType, Long userId, String messageType, String messageContent);

    /**
     * 发送消息给指定用户类型
     *
     * @param userType       用户类型
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(Integer userType, String messageType, String messageContent);

    /**
     * 发送消息给指定 Session
     *
     * @param sessionId      Session 编号
     * @param messageType    消息类型
     * @param messageContent 消息内容，JSON 格式
     */
    void send(String sessionId, String messageType, String messageContent);

    default void sendObject(Integer userType, Long userId, String messageType, Object messageContent) {
        send(userType, userId, messageType, JsonUtils.toJsonString(messageContent));
    }

    default void sendObject(Integer userType, String messageType, Object messageContent) {
        send(userType, messageType, JsonUtils.toJsonString(messageContent));
    }

    default void sendObject(String sessionId, String messageType, Object messageContent) {
        send(sessionId, messageType, JsonUtils.toJsonString(messageContent));
    }

}
