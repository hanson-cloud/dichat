package com.diqin.cloud.module.im.api.message.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 会员用户创建消息
 *
 * @author owen
 */
@Data
@Accessors(chain = true)
public class ImUserCreateMessage {

    /**
     * 用户编号
     */
    @NotNull(message = "用户编号不能为空")
    private Long userId;

}
