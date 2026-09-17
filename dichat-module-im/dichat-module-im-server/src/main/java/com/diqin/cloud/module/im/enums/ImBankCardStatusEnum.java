package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 银行卡状态枚举
 * <p>
 * 对应 {@code im_bank_card.status} 字段。解绑为软删除（status 置 {@code UNBOUND}），保留对账。
 *
 * @author dichat
 */
@Getter
public enum ImBankCardStatusEnum {

    NORMAL(0, "正常"),
    UNBOUND(1, "已解绑");

    private final Integer status;
    private final String desc;

    ImBankCardStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static ImBankCardStatusEnum of(Integer status) {
        for (ImBankCardStatusEnum e : values()) {
            if (e.getStatus().equals(status)) {
                return e;
            }
        }
        return null;
    }

}
