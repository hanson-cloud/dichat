package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 钱包状态枚举
 *
 * @author dichat
 */
@Getter
public enum ImWalletStatusEnum {

    NORMAL(0, "正常"),
    FREEZE(1, "冻结");

    private final Integer status;
    private final String desc;

    ImWalletStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ImWalletStatusEnum of(Integer status) {
        for (ImWalletStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }

}
