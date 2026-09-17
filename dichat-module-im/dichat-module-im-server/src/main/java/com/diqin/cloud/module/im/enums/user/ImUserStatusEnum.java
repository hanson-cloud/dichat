package com.diqin.cloud.module.im.enums.user;

import cn.hutool.core.util.ArrayUtil;
import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * IM 用户状态枚举
 *
 * <p>对应 im_users.status 字段；与 AdminUserApi 的 status 字段语义对齐（0=正常 1=停用），
 * 不复用 {@code CommonStatusEnum} 是为了避免 IM 模块被 system 枚举反向耦合。
 *
 * @author hanson
 */
@Getter
@AllArgsConstructor
public enum ImUserStatusEnum implements ArrayValuable<Integer> {

    NORMAL(0, "正常"),
    DISABLED(1, "停用");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImUserStatusEnum::getValue).toArray(Integer[]::new);

    private final Integer value;
    private final String name;

    public static ImUserStatusEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(o -> o.getValue().equals(value), ImUserStatusEnum.values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
