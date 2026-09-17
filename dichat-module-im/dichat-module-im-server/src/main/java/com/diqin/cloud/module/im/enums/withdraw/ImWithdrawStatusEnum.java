package com.diqin.cloud.module.im.enums.withdraw;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * IM 提现状态枚举
 *
 * @author dichat
 */
@RequiredArgsConstructor
@Getter
public enum ImWithdrawStatusEnum implements ArrayValuable<Integer> {

    PENDING(10, "待审核"),
    PROCESSING(20, "处理中"),
    SUCCESS(30, "成功"),
    FAILED(40, "失败"),
    CANCELLED(50, "已撤销");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImWithdrawStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isSuccess(Integer status) {
        return Objects.equals(SUCCESS.status, status);
    }

    public static boolean isPending(Integer status) {
        return Objects.equals(PENDING.status, status);
    }

}
