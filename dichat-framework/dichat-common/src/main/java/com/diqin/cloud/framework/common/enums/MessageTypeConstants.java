package com.diqin.cloud.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Member 字典类型的枚举类
 *
 * @author owen
 */
@Getter
@AllArgsConstructor
public enum MessageTypeConstants {

    LOGOUT("logout", "账号已在其他设备登录");

    private final String code;
    private final String desc;

}
