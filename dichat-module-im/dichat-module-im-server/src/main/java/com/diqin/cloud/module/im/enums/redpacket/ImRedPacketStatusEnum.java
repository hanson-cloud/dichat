package com.diqin.cloud.module.im.enums.redpacket;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * IM 红包状态枚举（主表 status）
 * <p>
 * 状态机：PENDING(待领取) → GRABBED_OUT(已领完) / EXPIRED(已过期) → REFUNDED(已退款)
 *
 * @author dichat
 */
@RequiredArgsConstructor
@Getter
public enum ImRedPacketStatusEnum implements ArrayValuable<Integer> {

    PENDING(10, "待领取"),
    GRABBED_OUT(20, "已领完"),
    EXPIRED(30, "已过期"),
    REFUNDED(40, "已退款");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImRedPacketStatusEnum::getStatus).toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static boolean isPending(Integer status) {
        return Objects.equals(PENDING.status, status);
    }

    public static boolean isGrabbedOut(Integer status) {
        return Objects.equals(GRABBED_OUT.status, status);
    }

    public static boolean isExpired(Integer status) {
        return Objects.equals(EXPIRED.status, status);
    }

    public static boolean isRefunded(Integer status) {
        return Objects.equals(REFUNDED.status, status);
    }

}
