package com.diqin.cloud.module.im.enums.transfer;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * IM 转账状态枚举
 *
 * @author dichat
 */
@RequiredArgsConstructor
@Getter
public enum ImTransferStatusEnum implements ArrayValuable<Integer> {

    PENDING(10, "处理中"),
    SUCCESS(20, "成功"),
    FAILED(30, "失败");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImTransferStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isSuccess(Integer status) {
        return Objects.equals(SUCCESS.status, status);
    }

}
