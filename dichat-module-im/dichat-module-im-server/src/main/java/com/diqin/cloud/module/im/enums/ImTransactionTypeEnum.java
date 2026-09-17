package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 资金交易类型枚举
 * <p>
 * 对应 {@code im_transaction_log.type} 字段；金额方向：正数=收入，负数=支出。
 *
 * @author dichat
 */
@Getter
public enum ImTransactionTypeEnum {

    RECHARGE(1, "充值"),
    TRANSFER_OUT(2, "转账支出"),
    TRANSFER_IN(3, "转账收入"),
    RED_SEND(4, "发红包"),
    RED_RECEIVE(5, "领红包"),
    WITHDRAW(6, "提现");

    private final Integer type;
    private final String desc;

    ImTransactionTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ImTransactionTypeEnum of(Integer type) {
        for (ImTransactionTypeEnum e : values()) {
            if (e.getType().equals(type)) {
                return e;
            }
        }
        return null;
    }

}
