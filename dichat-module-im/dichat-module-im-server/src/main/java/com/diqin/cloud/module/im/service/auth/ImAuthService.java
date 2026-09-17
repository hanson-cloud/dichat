package com.diqin.cloud.module.im.service.auth;

import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginReqVO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginRespVO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthRegisterReqVO;
import jakarta.validation.Valid;

/**
 * 会员的认证 Service 接口
 * 提供用户的账号密码登录、token 的校验等认证相关的功能
 *
 * @author hanson
 */
public interface ImAuthService {

    /**
     * 手机号注册接口
     *
     * @param reqVO 注册请求参数，包含手机号、验证码等信息，使用@Valid注解进行参数校验
     * @return 返回注册结果，包含token等认证信息
     */
    AppImAuthLoginRespVO register(@Valid AppImAuthRegisterReqVO reqVO);

    /**
     * 账号 + 密码登录
     *
     * @param reqVO 登录信息
     * @return 登录结果
     */
    AppImAuthLoginRespVO login(@Valid AppImAuthLoginReqVO reqVO);

    /**
     * 基于 token 退出登录
     *
     * @param token token
     */
    void logout(String token);

    /**
     * 刷新访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AppImAuthLoginRespVO refreshToken(String refreshToken);

}
