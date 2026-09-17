package com.diqin.cloud.module.im.enums.bankcard;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * IM 银行卡状态枚举
 * <p>
 * 状态机：PENDING(待审核) → NORMAL(正常) / FROZEN(冻结) → UNBIND(已解绑)
 *
 * @author dichat
 */
@RequiredArgsConstructor
@Getter
public enum ImBankCardStatusEnum implements ArrayValuable<Integer> {

    PENDING(10, "待审核"),
    NORMAL(20, "正常"),
    FROZEN(30, "冻结"),
    UNBIND(40, "已解绑");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImBankCardStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isPending(Integer status) {
        return Objects.equals(PENDING.status, status);
    }

    public static boolean isNormal(Integer status) {
        return Objects.equals(NORMAL.status, status);
    }

    public static boolean isUnbind(Integer status) {
        return Objects.equals(UNBIND.status, status);
    }

}
