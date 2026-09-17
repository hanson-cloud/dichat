package com.diqin.cloud.module.im.service.online;

import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionManager;
import com.diqin.cloud.framework.websocket.core.util.WebSocketFrameworkUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * IM 在线状态服务
 * <p>
 * 基于 WebSocketSessionManager 维护的内存会话，查询用户/好友/群成员的在线终端。
 * 注意：当前实现仅适用于单节点部署；如需多节点，应改用 Redis 集中存储在线状态。
 */
@Service
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private WebSocketSessionManager webSocketSessionManager;

    @Override
    public Map<Long, List<Integer>> getOnlineTerminalMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, List<Integer>> result = new HashMap<>(userIds.size());
        for (Long userId : userIds) {
            List<Integer> terminals = getOnlineTerminals(userId);
            if (!terminals.isEmpty()) {
                result.put(userId, terminals);
            }
        }
        return result;
    }

    @Override
    public List<Integer> getOnlineTerminals(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        Collection<WebSocketSession> sessions = webSocketSessionManager.getSessionList(UserTypeEnum.MEMBER.getValue(), userId);
        if (sessions == null || sessions.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Integer> terminals = new LinkedHashSet<>();
        for (WebSocketSession session : sessions) {
            Integer terminal = WebSocketFrameworkUtils.getTerminal(session);
            if (terminal != null) {
                terminals.add(terminal);
            }
        }
        return new ArrayList<>(terminals);
    }

    @Override
    public boolean isOnline(Long userId) {
        return !getOnlineTerminals(userId).isEmpty();
    }

    @Override
    public List<Long> filterOnlineUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userIds.stream().filter(this::isOnline).toList();
    }

}
