package com.diqin.cloud.module.im.service.loginlog;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import com.diqin.cloud.framework.websocket.core.util.WebSocketFrameworkUtils;
import com.diqin.cloud.module.im.enums.ForceOfflineReason;
import com.diqin.cloud.module.system.api.logger.LoginLogApi;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogPageReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogRespDTO;
import com.diqin.cloud.module.system.api.oauth2.TokenApi;
import com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Objects;

/**
 * IM 用户登录日志管理 Service 实现类
 *
 * <p>登录日志（含在线状态、强制下线）已统一收敛到 system 模块的 {@code system_login_log}，
 * 本类不再持有独立数据表，只负责「强制下线」时对 WebSocket 会话的真实断开（WebSocket 连接只进 IM 模块），
 * 状态标记通过 {@link LoginLogApi} 落到 system。定时清理、分页查询等纯数据操作已收敛到 system 模块。</p>
 *
 * @author hanson
 */
@Slf4j
@Service
public class ImUserLoginLogManagerServiceImpl implements ImUserLoginLogManagerService {

    /** 强制下线时查询在线记录的单次上限 */
    private static final int ONLINE_PAGE_SIZE = 100;

    @Resource
    private LoginLogApi loginLogApi;

    @Resource
    private WebSocketSessionManager webSocketSessionManager;

    @Resource
    private TokenApi tokenApi;

    @Override
    public void forceOffline(Long id, ForceOfflineReason reason) {
        forceOffline(id, reason, null);
    }

    /**
     * 强制下线：标记日志为已强制下线，并关闭该用户对应终端的 WebSocket 会话（真实断开）
     *
     * <p>先下发应用层「强制下线」通知（携带 reason，客户端据此弹窗），再精确吊销该会话令牌，最后关闭会话。
     * 无活跃会话时兜底全量吊销令牌，避免残留有效令牌被继续使用。</p>
     */
    public void forceOffline(Long id, ForceOfflineReason reason, String reasonText) {
        LoginLogRespDTO logRespDTO = loginLogApi.getLoginLog(id).getCheckedData();
        if (logRespDTO == null) {
            return;
        }
        // 1. 标记记录为「已强制下线」
        loginLogApi.forceOffline(id);
        // 2. 关闭该用户对应终端的 WebSocket 会话（真实断开），并下发通知 + 精确吊销令牌
        Long userId = logRespDTO.getUserId();
        Integer terminal = logRespDTO.getTerminal();
        if (userId == null || terminal == null) {
            return;
        }
        String wsMessage = buildForceOfflineMessage(reason, reasonText);
        Collection<WebSocketSession> sessions = webSocketSessionManager.getSessionList(
                UserTypeEnum.MEMBER.getValue(), userId);
        if (CollUtil.isEmpty(sessions)) {
            // 无活跃会话：兜底吊销该用户全部令牌，避免残留有效令牌被继续使用
            tokenApi.revokeByUser(userId, UserTypeEnum.MEMBER.getValue());
            return;
        }
        boolean matched = false;
        boolean revoked = false;
        for (WebSocketSession session : sessions) {
            Integer sessionTerminal = WebSocketFrameworkUtils.getTerminal(session);
            if (!Objects.equals(sessionTerminal, terminal)) {
                continue;
            }
            matched = true;
            // 2.1 先下发应用层「强制下线」通知，让客户端即时弹窗退出
            try {
                session.sendMessage(new TextMessage(wsMessage));
            } catch (Exception e) {
                log.warn("[forceOffline] 下发强制下线通知失败，userId: {}, terminal: {}, reason: {}",
                        userId, terminal, reason, e);
            }
            // 2.2 精确吊销该会话对应的访问令牌（access + refresh 一并失效）
            String token = WebSocketFrameworkUtils.getAccessToken(session);
            if (token != null) {
                try {
                    tokenApi.revokeToken(token);
                    revoked = true;
                } catch (Exception e) {
                    log.warn("[forceOffline] 吊销令牌失败，userId: {}, terminal: {}", userId, terminal, e);
                }
            }
            // 2.3 关闭 WebSocket 会话
            try {
                session.close();
            } catch (Exception e) {
                log.warn("[forceOffline] 关闭用户会话失败，userId: {}, terminal: {}", userId, terminal, e);
            }
        }
        // 匹配到会话但未能取到令牌（极端情况），兜底全量吊销，避免残留有效令牌
        if (matched && !revoked) {
            tokenApi.revokeByUser(userId, UserTypeEnum.MEMBER.getValue());
        }
    }

    @Override
    public void forceOfflineByUserId(Long userId, ForceOfflineReason reason, String reasonText) {
        if (userId == null) {
            return;
        }
        LoginLogPageReqDTO rpcReq = new LoginLogPageReqDTO();
        rpcReq.setPageNo(1);
        rpcReq.setPageSize(ONLINE_PAGE_SIZE);
        rpcReq.setUserType(UserTypeEnum.MEMBER.getValue());
        rpcReq.setUserId(userId);
        rpcReq.setOnlineStatus(LoginLogStatusEnum.ONLINE.getStatus());

        PageResult<LoginLogRespDTO> pageResult = loginLogApi.getLoginLogPage(rpcReq).getCheckedData();
        if (pageResult == null || CollUtil.isEmpty(pageResult.getList())) {
            return;
        }
        for (LoginLogRespDTO log : pageResult.getList()) {
            forceOffline(log.getId(), reason, reasonText);
        }
    }

    /**
     * 构造强制下线 WebSocket 消息（envelope: {type, content}）。
     * <p>client 约定：{@code content} 必须是 JSON 字符串（前端会 {@code JSON.parse(envelope.content)} 得到对象），
     * 因此内层对象需整体转义后嵌入为字符串。{@code reason} 为必填，{@code reasonText} 可选（如封号原因）。</p>
     */
    private static String buildForceOfflineMessage(ForceOfflineReason reason, String reasonText) {
        StringBuilder inner = new StringBuilder("{\"reason\":\"")
                .append(reason.name()).append("\"");
        if (StrUtil.isNotBlank(reasonText)) {
            // 转义双引号与反斜杠，避免破坏 JSON 结构
            String escaped = StrUtil.replace(reasonText, "\\", "\\\\");
            escaped = StrUtil.replace(escaped, "\"", "\\\"");
            inner.append(",\"reasonText\":\"").append(escaped).append("\"");
        }
        inner.append("}");
        // content 必须是 JSON 字符串，故对内层 JSON 做一次整体转义嵌入
        String escapedInner = StrUtil.replace(inner.toString(), "\\", "\\\\");
        escapedInner = StrUtil.replace(escapedInner, "\"", "\\\"");
        return "{\"type\":\"im-force-offline\",\"content\":\"" + escapedInner + "\"}";
    }

}
