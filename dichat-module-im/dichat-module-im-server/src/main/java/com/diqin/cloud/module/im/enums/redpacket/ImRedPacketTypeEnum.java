package com.diqin.cloud.module.im.enums.redpacket;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * IM 红包类型枚举
 *
 * @author dichat
 */
@RequiredArgsConstructor
@Getter
public enum ImRedPacketTypeEnum implements ArrayValuable<Integer> {

    NORMAL(1, "普通红包"), // 群红包均分；私聊红包整包领取
    LUCKY(2, "拼手气红包"); // 群红包随机金额

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImRedPacketTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
