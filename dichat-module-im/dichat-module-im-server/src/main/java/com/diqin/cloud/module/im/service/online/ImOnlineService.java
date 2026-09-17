package com.diqin.cloud.module.im.service.online;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IM 在线状态服务接口
 * <p>
 * 提供基于 WebSocket 会话的在线状态查询能力。
 */
public interface ImOnlineService {

    /**
     * 批量查询用户在线终端
     *
     * @param userIds 用户编号集合
     * @return 用户在线终端映射（key = 用户编号，value = 在线终端列表）；不在线的用户不会出现在 map 中
     */
    Map<Long, List<Integer>> getOnlineTerminalMap(Collection<Long> userIds);

    /**
     * 查询指定用户的在线终端列表
     *
     * @param userId 用户编号
     * @return 在线终端列表；不在线返回空列表
     */
    List<Integer> getOnlineTerminals(Long userId);

    /**
     * 判断用户是否在线
     *
     * @param userId 用户编号
     * @return true 表示在线
     */
    boolean isOnline(Long userId);

    /**
     * 从用户列表中过滤出在线用户
     *
     * @param userIds 用户编号集合
     * @return 在线用户编号列表
     */
    List<Long> filterOnlineUserIds(Collection<Long> userIds);

}
