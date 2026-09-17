package com.diqin.cloud.framework.websocket.core.security;

import cn.hutool.core.util.NumberUtil;
import com.diqin.cloud.framework.common.enums.TerminalEnum;
import com.diqin.cloud.framework.security.core.LoginUser;
import com.diqin.cloud.framework.security.core.filter.TokenAuthenticationFilter;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.framework.websocket.core.util.WebSocketFrameworkUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 登录用户的 {@link HandshakeInterceptor} 实现类
 * 流程如下：
 * 1. 前端连接 websocket 时，会通过拼接 ?token={token} 到 ws:// 连接后，这样它可以被 {@link TokenAuthenticationFilter} 所认证通过
 * 2. {@link LoginUserHandshakeInterceptor} 负责把 {@link LoginUser} 添加到 {@link WebSocketSession} 中
 *
 * @author hanson
 */
public class LoginUserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        WebSocketFrameworkUtils.setLoginUser(loginUser, attributes);
        // 同时记录终端类型，供在线状态/通话等场景使用
        Integer terminal = parseTerminal(request);
        WebSocketFrameworkUtils.setTerminal(terminal, attributes);
        // 记录访问令牌，供「强制下线」时精确吊销单台设备的令牌（见 WebSocketFrameworkUtils.ATTRIBUTE_ACCESS_TOKEN）
        String token = parseAccessToken(request);
        if (token != null) {
            attributes.put(WebSocketFrameworkUtils.ATTRIBUTE_ACCESS_TOKEN, token);
        }
        return true;
    }

    /**
     * 从握手请求头中解析终端类型
     *
     * @param request WebSocket 握手请求
     * @return 终端类型值；无法解析时返回 {@link TerminalEnum#UNKNOWN}
     */
    private static Integer parseTerminal(ServerHttpRequest request) {
        String terminalValue = request.getHeaders().getFirst("terminal");
        if (terminalValue == null) {
            return TerminalEnum.UNKNOWN.getTerminal();
        }
        if (!NumberUtil.isNumber(terminalValue)) {
            return TerminalEnum.UNKNOWN.getTerminal();
        }
        return Integer.valueOf(terminalValue);
    }

    /**
     * 从握手请求 URL 的 {@code ?token=} 中解析访问令牌
     *
     * @param request WebSocket 握手请求
     * @return 访问令牌；无法解析时返回 {@code null}
     */
    private static String parseAccessToken(ServerHttpRequest request) {
        String query = request.getURI().getRawQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }
        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String key = pair.substring(0, idx);
            String value = pair.substring(idx + 1);
            if ("token".equals(key) && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                               @NonNull WebSocketHandler wsHandler, Exception exception) {
        // do nothing
    }

}
