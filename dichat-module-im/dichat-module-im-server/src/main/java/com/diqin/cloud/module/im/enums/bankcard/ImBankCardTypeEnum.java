package com.diqin.cloud.module.im.enums.bankcard;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * IM 银行卡类型枚举
 *
 * @author dichat
 */
@RequiredArgsConstructor
@Getter
public enum ImBankCardTypeEnum implements ArrayValuable<Integer> {

    DEBIT(1, "借记卡"),
    CREDIT(2, "信用卡");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImBankCardTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
