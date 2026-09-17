package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 转账订单状态枚举
 *
 * @author dichat
 */
@Getter
public enum ImTransferStatusEnum {

    PENDING(0, "待领取"),
    PAID(1, "已到账"),
    REFUNDED(2, "已退款"),
    EXPIRED(3, "已过期");

    private final Integer status;
    private final String desc;

    ImTransferStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ImTransferStatusEnum of(Integer status) {
        for (ImTransferStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }

}
