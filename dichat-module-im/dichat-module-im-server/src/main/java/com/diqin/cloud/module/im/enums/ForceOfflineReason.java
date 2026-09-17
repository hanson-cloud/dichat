package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * 强制下线原因枚举
 *
 * <p>后端在 WebSocket 下发 {@code im-force-offline} 时，通过 {@code content.reason} 区分触发来源，
 * 客户端据此展示不同文案（管理员强制下线 / 其他设备登录 / 账号注销 / 账号被封禁）。</p>
 */
@Getter
public enum ForceOfflineReason {

    /** 管理员在后台手动强制下线某个终端 */
    ADMIN("管理员强制下线"),
    /** 用户在「设备管理」页踢掉其他终端 / 新设备登录互踢 */
    SELF("其他设备登录退出"),
    /** 用户主动注销账号（软注销） */
    DEREGISTER("账号注销"),
    /** 管理端封号（禁用账号 + 全端强踢），登录网关会拦截禁用账号阻断二次登录 */
    BANNED("账号被封禁");

    /** 默认文案（客户端可覆盖，服务端仅用于日志/审计） */
    private final String desc;

    ForceOfflineReason(String desc) {
        this.desc = desc;
    }

}
