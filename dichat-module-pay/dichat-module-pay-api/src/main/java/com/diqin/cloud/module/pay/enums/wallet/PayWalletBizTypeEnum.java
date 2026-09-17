package com.diqin.cloud.module.pay.enums.wallet;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 钱包交易业务分类
 *
 * @author hanson
 */
@AllArgsConstructor
@Getter
public enum PayWalletBizTypeEnum implements ArrayValuable<Integer> {

    RECHARGE(1, "充值"),
    RECHARGE_REFUND(2, "充值退款"),
    PAYMENT(3, "支付"),
    PAYMENT_REFUND(4, "支付退款"),
    UPDATE_BALANCE(5, "更新余额"),
    TRANSFER(6, "转账"),

    // ========== IM 社交支付业务类型（101-105） ==========
    RED_PACKET(101, "红包"),
    TRANSFER_IM(102, "IM转账"),
    WITHDRAW(103, "提现"),
    RED_PACKET_REFUND(104, "红包退款"),
    WITHDRAW_REFUND(105, "提现退款");

    /**
     * 业务分类
     */
    private final Integer type;
    /**
     * 说明
     */
    private final String description;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(PayWalletBizTypeEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
         return ARRAYS;
    }

    public static PayWalletBizTypeEnum valueOf(Integer type) {
        return Arrays.stream(values()).filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

}
