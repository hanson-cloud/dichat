package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 红包状态枚举
 * <p>
 * 对应 {@code im_red_packet.status} 字段。
 *
 * @author dichat
 */
@Getter
public enum ImRedPacketStatusEnum {

    PENDING(0, "待领取"),
    GRABBED(1, "已抢完"),
    REFUNDED(2, "已退款"),
    EXPIRED(3, "已过期");

    private final Integer status;
    private final String desc;

    ImRedPacketStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ImRedPacketStatusEnum of(Integer status) {
        for (ImRedPacketStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }

}
