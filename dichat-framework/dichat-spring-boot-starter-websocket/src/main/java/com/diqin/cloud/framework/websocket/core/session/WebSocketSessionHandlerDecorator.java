package com.diqin.cloud.framework.websocket.core.session;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

/**
 * {@link WebSocketHandler} 的装饰类，实现了以下功能：
 * 1. {@link WebSocketSession} 连接或关闭时，使用 {@link #sessionManager} 进行管理
 * 2. 封装 {@link WebSocketSession} 支持并发操作
 * 3. 集成心跳管理
 *
 * @author hanson
 */
@Slf4j
public class WebSocketSessionHandlerDecorator extends WebSocketHandlerDecorator {

    /**
     * 发送时间的限制，单位：毫秒
     */
    private static final Integer SEND_TIME_LIMIT = 1000 * 5;
    /**
     * 发送消息缓冲上线，单位：bytes
     */
    private static final Integer BUFFER_SIZE_LIMIT = 1024 * 100;

    private final WebSocketSessionManager sessionManager;

    public WebSocketSessionHandlerDecorator(WebSocketHandler delegate,
                                            WebSocketSessionManager sessionManager) {
        super(delegate);
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        // 实现 session 支持并发
        session = new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
        // 添加到 WebSocketSessionManager 中
        sessionManager.addSession(session);

        log.info("[afterConnectionEstablished][session({}) 连接建立]", session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        // 从会话管理器中移除
        sessionManager.removeSession(session);

        log.info("[afterConnectionClosed][session({}) 连接关闭，状态: {}]",
                session.getId(), closeStatus);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        boolean expectClosed = exception instanceof ClosedChannelException
                || (exception instanceof IOException
                && "Connection reset by peer".equalsIgnoreCase(exception.getMessage()))
                || !session.isOpen();
        if (expectClosed) {
            log.debug("[handleTransportError][session({}) 连接已断开，忽略正常的传输错误: {}]",
                    session.getId(), exception.toString());
            return;
        }
        log.error("[handleTransportError][session({}) 传输错误][错误信息:({})]",
                session.getId(), exception.getMessage(), exception);
    }
}
