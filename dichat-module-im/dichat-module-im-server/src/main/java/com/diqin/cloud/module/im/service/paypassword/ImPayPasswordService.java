package com.diqin.cloud.module.im.service.paypassword;

import com.diqin.cloud.module.im.dto.paypassword.*;

/**
 * IM 支付密码 Service 接口
 *
 * @author dichat
 */
public interface ImPayPasswordService {

    /**
     * 是否已设置支付密码
     *
     * @param userId 用户编号
     * @return 是否已设置
     */
    boolean hasPassword(Long userId);

    /**
     * 设置 / 重置支付密码（BCrypt 哈希存储）
     *
     * @param userId 用户编号
     * @param reqDTO 支付密码
     */
    void setPassword(Long userId, ImPayPasswordSetReqDTO reqDTO);

    /**
     * 校验支付密码（未设置 / 错误均抛异常）
     *
     * @param userId   用户编号
     * @param password 明文支付密码
     */
    void verifyPassword(Long userId, String password);

    /**
     * 发送「重置支付密码」短信验证码（手机号需与账户绑定手机号一致）
     *
     * @param userId 用户编号
     * @param phone  绑定手机号
     */
    void sendResetSms(Long userId, String phone);

    /**
     * 校验短信验证码并重置支付密码
     *
     * @param userId 用户编号
     * @param reqDTO 手机号 + 验证码 + 新密码
     */
    void resetPassword(Long userId, ImPayPasswordResetReqDTO reqDTO);

    /**
     * 设置手势密码（存储 SHA-256 哈希）
     *
     * @param userId 用户编号
     * @param reqDTO 手势图案哈希
     */
    void setGesturePattern(Long userId, ImPayPasswordGestureSetReqDTO reqDTO);

    /**
     * 验证手势密码（支付时调用）
     *
     * @param userId 用户编号
     * @param reqDTO 待验证手势图案哈希
     * @return 是否匹配
     */
    boolean verifyGesturePattern(Long userId, ImPayPasswordGestureVerifyReqDTO reqDTO);

    /**
     * 清除手势密码
     *
     * @param userId 用户编号
     */
    void clearGesturePattern(Long userId);

    /**
     * 开关指纹支付
     *
     * @param userId 用户编号
     * @param reqDTO 开关状态
     */
    void toggleFingerprint(Long userId, ImPayPasswordFingerprintToggleReqDTO reqDTO);

    /**
     * 获取支付安全状态汇总
     *
     * @param userId 用户编号
     * @return 安全状态
     */
    ImPaySecurityStatusRespDTO getSecurityStatus(Long userId);

}
