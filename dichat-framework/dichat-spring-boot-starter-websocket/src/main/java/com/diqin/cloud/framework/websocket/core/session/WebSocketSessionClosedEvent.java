package com.diqin.cloud.framework.websocket.core.session;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * WebSocket 会话关闭事件
 *
 * <p>在 {@link WebSocketSessionManagerImpl#removeSession} 中发布，供业务模块监听并处理
 * 用户下线后的清理逻辑（例如：更新登录日志的离线状态）。</p>
 *
 * @author hanson
 */
@Getter
public class WebSocketSessionClosedEvent extends ApplicationEvent {

    /**
     * 用户类型
     *
     * <p>关联 {@link com.diqin.cloud.framework.common.enums.UserTypeEnum}</p>
     */
    private final Integer userType;

    /**
     * 用户编号
     */
    private final Long userId;

    /**
     * 终端类型
     *
     * <p>关联 {@link com.diqin.cloud.framework.common.enums.TerminalEnum}</p>
     */
    private final Integer terminal;

    /**
     * 会话编号
     */
    private final String sessionId;

    /**
     * 关闭时间
     */
    private final LocalDateTime closeTime;

    public WebSocketSessionClosedEvent(Object source, Integer userType, Long userId,
                                       Integer terminal, String sessionId) {
        super(source);
        this.userType = userType;
        this.userId = userId;
        this.terminal = terminal;
        this.sessionId = sessionId;
        this.closeTime = LocalDateTime.now();
    }

}
