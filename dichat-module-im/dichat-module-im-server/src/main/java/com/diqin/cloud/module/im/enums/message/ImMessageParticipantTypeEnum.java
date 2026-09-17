package com.diqin.cloud.module.im.enums.message;

import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * IM 消息参与方类型枚举
 * <p>
 * 用于 {@code im_private_message} 的 {@code sender_type} / {@code receiver_type}，
 * 标识一条消息的发送/接收方是：普通 C 端用户(USER)、平台机器人(ROBOT) 还是人工客服(CS)。
 * <p>
 * 引入该枚举是方案 C（消息模型重构）的基础：把客服 / 机器人从「寄生在 im_users 表」改为
 * 「独立参与方」，从而让人工客服绑定 system_users、机器人成为平台级虚拟账号，
 * 不再需要为每个服务角色先创建一个 C 端 IM 账号。
 *
 * @author 速构构
 */
@Getter
@RequiredArgsConstructor
public enum ImMessageParticipantTypeEnum implements ArrayValuable<Integer> {

    /**
     * 普通 C 端用户（im_users.id）
     */
    USER(1, "用户"),
    /**
     * 平台机器人（im_robot.id，独立虚拟账号，不再绑定 im_users）
     */
    ROBOT(2, "机器人"),
    /**
     * 人工客服（im_customer_service.id，绑定 system_users.id 作为真实坐席身份）
     */
    CS(3, "人工客服");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImMessageParticipantTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 根据 type 解析枚举；未命中返回 null（调用方按缺省 USER 处理）
     */
    public static ImMessageParticipantTypeEnum of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ImMessageParticipantTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
