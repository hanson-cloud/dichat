package com.diqin.cloud.framework.websocket.core.util;

import com.diqin.cloud.framework.security.core.LoginUser;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

/**
 * 专属于 web 包的工具类
 *
 * @author hanson
 */
public class WebSocketFrameworkUtils {

    public static final String ATTRIBUTE_LOGIN_USER = "LOGIN_USER";

    /**
     * 终端类型属性名
     * <p>
     * 在 WebSocket 握手阶段由 {@link com.diqin.cloud.framework.websocket.core.security.LoginUserHandshakeInterceptor}
     * 从请求头 {@code terminal} 中解析并写入 session attributes，供后续在线状态查询使用。
     *
     * @see com.diqin.cloud.framework.common.enums.TerminalEnum
     */
    public static final String ATTRIBUTE_TERMINAL = "TERMINAL";

    /**
     * 访问令牌属性名
     * <p>
     * 在 WebSocket 握手阶段由 {@link com.diqin.cloud.framework.websocket.core.security.LoginUserHandshakeInterceptor}
     * 从请求 URL 的 {@code ?token=} 中解析并写入 session attributes，供「强制下线」时精确吊销单台设备的令牌。
     */
    public static final String ATTRIBUTE_ACCESS_TOKEN = "ACCESS_TOKEN";

    private WebSocketFrameworkUtils() {}
    /**
     * 设置当前用户
     *
     * @param loginUser 登录用户
     * @param attributes Session
     */
    public static void setLoginUser(LoginUser loginUser, Map<String, Object> attributes) {
        attributes.put(ATTRIBUTE_LOGIN_USER, loginUser);
    }

    /**
     * 设置终端类型
     *
     * @param terminal 终端类型值，关联 {@link com.diqin.cloud.framework.common.enums.TerminalEnum}
     * @param attributes Session attributes
     */
    public static void setTerminal(Integer terminal, Map<String, Object> attributes) {
        attributes.put(ATTRIBUTE_TERMINAL, terminal);
    }

    /**
     * 获取当前用户
     *
     * @return 当前用户
     */
    public static LoginUser getLoginUser(WebSocketSession session) {
        return (LoginUser) session.getAttributes().get(ATTRIBUTE_LOGIN_USER);
    }

    /**
     * 获取当前终端类型
     *
     * @param session WebSocket 会话
     * @return 终端类型值；未设置时返回 {@code null}
     */
    public static Integer getTerminal(WebSocketSession session) {
        return (Integer) session.getAttributes().get(ATTRIBUTE_TERMINAL);
    }

    /**
     * 获取当前会话绑定的访问令牌
     * <p>
     * 仅在握手阶段由 {@link com.diqin.cloud.framework.websocket.core.security.LoginUserHandshakeInterceptor}
     * 写入，用于「强制下线」时精确吊销该设备对应的令牌。未设置时返回 {@code null}。
     *
     * @param session WebSocket 会话
     * @return 访问令牌；未设置时返回 {@code null}
     */
    public static String getAccessToken(WebSocketSession session) {
        return (String) session.getAttributes().get(ATTRIBUTE_ACCESS_TOKEN);
    }

    /**
     * 获得当前用户的编号
     *
     * @return 用户编号
     */
    public static Long getLoginUserId(WebSocketSession session) {
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * 获得当前用户的类型
     *
     * @return 用户编号
     */
    public static Integer getLoginUserType(WebSocketSession session) {
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getUserType() : null;
    }

    /**
     * 获得当前用户的租户编号
     *
     * @param session Session
     * @return 租户编号
     */
    public static Long getTenantId(WebSocketSession session) {
        LoginUser loginUser = getLoginUser(session);
        return loginUser != null ? loginUser.getTenantId() : null;
    }

}
