package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 银行卡类型枚举
 * <p>
 * 对应 {@code im_bank_card.card_type} 字段。
 *
 * @author dichat
 */
@Getter
public enum ImBankCardTypeEnum {

    DEBIT(1, "借记卡"),
    CREDIT(2, "信用卡");

    private final Integer type;
    private final String desc;

    ImBankCardTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ImBankCardTypeEnum of(Integer type) {
        for (ImBankCardTypeEnum e : values()) {
            if (e.getType().equals(type)) {
                return e;
            }
        }
        return null;
    }

}
