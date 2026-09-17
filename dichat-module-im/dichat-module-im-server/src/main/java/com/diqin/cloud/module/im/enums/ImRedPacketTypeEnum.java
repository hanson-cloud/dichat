package com.diqin.cloud.module.im.enums;

import lombok.Getter;

/**
 * IM 红包类型枚举
 * <p>
 * 对应 {@code im_red_packet.type} 字段。普通红包每人金额均等；拼手气红包按「二倍均值」预拆分。
 *
 * @author dichat
 */
@Getter
public enum ImRedPacketTypeEnum {

    NORMAL(0, "普通红包"),
    LUCKY(1, "拼手气红包");

    private final Integer type;
    private final String desc;

    ImRedPacketTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ImRedPacketTypeEnum of(Integer type) {
        for (ImRedPacketTypeEnum e : values()) {
            if (e.getType().equals(type)) {
                return e;
            }
        }
        return null;
    }

}
