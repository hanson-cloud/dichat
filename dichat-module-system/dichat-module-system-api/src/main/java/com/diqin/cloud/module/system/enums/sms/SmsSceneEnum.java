package com.diqin.cloud.module.system.enums.sms;

import cn.hutool.core.util.ArrayUtil;
import com.diqin.cloud.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 用户短信验证码发送场景的枚举
 *
 * @author hanson
 */
@Getter
@AllArgsConstructor
public enum SmsSceneEnum implements ArrayValuable<Integer> {

    MEMBER_LOGIN(1, "user-sms-login", "会员用户 - 手机号登陆"),
    MEMBER_UPDATE_MOBILE(2, "user-update-mobile", "会员用户 - 修改手机"),
    MEMBER_UPDATE_PASSWORD(3, "user-update-password", "会员用户 - 修改密码"),
    MEMBER_RESET_PASSWORD(4, "user-reset-password", "会员用户 - 忘记密码"),
    MEMBER_REGISTER_MOBILE(5, "user-register-mobile", "会员用户 - 手机号注册"),
    MEMBER_LOG_OFF_MOBILE(6, "user-log-off-mobile", "会员用户 - 手机号账号注销"),
    MEMBER_BIND_BANK_CARD(7, "user-bind-bank-card", "会员用户 - 绑定银行卡"),
    MEMBER_RESET_PAY_PASSWORD(8, "user-reset-pay-password", "会员用户 - 重置支付密码"),

    ADMIN_MEMBER_LOGIN(21, "admin-sms-login", "后台用户 - 手机号登录"),
    ADMIN_MEMBER_REGISTER(22, "admin-sms-register", "后台用户 - 手机号注册"),
    ADMIN_MEMBER_RESET_PASSWORD(23, "admin-reset-password", "后台用户 - 忘记密码");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(SmsSceneEnum::getScene).toArray(Integer[]::new);

    /**
     * 验证场景的编号
     */
    private final Integer scene;
    /**
     * 模版编码
     */
    private final String templateCode;
    /**
     * 描述
     */
    private final String description;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static SmsSceneEnum getCodeByScene(Integer scene) {
        return ArrayUtil.firstMatch(sceneEnum -> sceneEnum.getScene().equals(scene),
                values());
    }

}
