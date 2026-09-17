package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 支付相关 WebSocket 推送类型枚举
 * <p>
 * 走私聊通道（{@code im-private-message} 帧），通过 {@code dto.type} 区分业务。
 *
 * @author dichat
 */
@Getter
public enum ImPayMessageTypeEnum {

    WALLET_CHANGE(1001, "钱包变动通知");

    private final Integer type;
    private final String desc;

    ImPayMessageTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}
