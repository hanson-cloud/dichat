package com.diqin.cloud.module.system.enums.logger;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 登录日志的会话状态枚举
 *
 * <p>用于标记一条登录记录当前代表的会话是否仍在线，支撑「我的登录设备 / 强制下线」能力。
 * 注意与 {@link LoginResultEnum}（登录结果：成功/失败）区分，二者语义不同。</p>
 *
 * @author hanson
 */
@RequiredArgsConstructor
@Getter
public enum LoginLogStatusEnum implements ArrayValuable<Integer> {

    ONLINE(1, "在线"),
    FORCE_OFFLINE(2, "已强制下线"),
    OFFLINE(3, "已离线"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(LoginLogStatusEnum::getStatus)
            .toArray(Integer[]::new);

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
