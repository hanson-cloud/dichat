package com.diqin.cloud.module.im.service.loginlog;

import com.diqin.cloud.module.im.enums.ForceOfflineReason;

/**
 * IM 用户登录日志管理 Service
 *
 * <p>登录日志已统一收敛到 system 模块的 {@code system_login_log}。本接口仅保留「强制下线」相关的真实
 * 断开 WebSocket 能力（WebSocket 连接只进 IM 模块），状态标记通过 LoginLogApi 落到 system。
 * 分页查询、定时清理等纯数据操作已收敛到 system 模块。</p>
 *
 * @author hanson
 */
public interface ImUserLoginLogManagerService {

    /**
     * 强制下线：标记日志为已强制下线，并关闭该用户对应终端的 WebSocket 会话（真实断开）
     *
     * @param id 登录日志编号
     * @param reason 强制下线原因（区分管理员 / 用户自己 / 注销 / 封号），下发到客户端用于展示文案
     */
    void forceOffline(Long id, ForceOfflineReason reason);

    /**
     * 强制下线指定用户的全部终端（注销账号 / 封号时踢全端）
     * <p>遍历该用户所有在线登录日志，逐条调用 {@link #forceOffline(Long, ForceOfflineReason)} 真实断开 WebSocket 会话。
     *
     * @param userId 用户编号
     * @param reason 强制下线原因
     * @param reasonText 可选的补充说明（如封号原因），透传给客户端展示
     */
    void forceOfflineByUserId(Long userId, ForceOfflineReason reason, String reasonText);

}
