package com.diqin.cloud.module.im.service.auth;

import com.diqin.cloud.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.diqin.cloud.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import com.diqin.cloud.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.util.monitor.TracerUtils;
import com.diqin.cloud.framework.common.util.servlet.ServletUtils;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginReqVO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginRespVO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthRegisterReqVO;
import com.diqin.cloud.module.im.convert.auth.ImAuthConvert;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.diqin.cloud.module.system.api.logger.LoginLogApi;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.diqin.cloud.module.system.api.sms.SmsCodeApi;
import com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum;
import com.diqin.cloud.module.system.enums.logger.LoginLogTypeEnum;
import com.diqin.cloud.module.system.enums.logger.LoginResultEnum;
import com.diqin.cloud.module.system.enums.oauth2.OAuth2ClientConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.diqin.cloud.framework.common.util.servlet.ServletUtils.getClientIP;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getTerminal;
import static com.diqin.cloud.module.system.enums.ErrorCodeConstants.*;

/**
 * 会员的认证 Service 接口
 *
 * @author hanson
 */
@Service
@Slf4j
public class ImAuthServiceImpl implements ImAuthService {

    @Resource
    private ImUserService userService;
    @Resource
    private SmsCodeApi smsCodeApi;
    @Resource
    private LoginLogApi loginLogApi;
    @Resource
    private OAuth2TokenCommonApi oauth2TokenApi;

    @Override
    public AppImAuthLoginRespVO register(AppImAuthRegisterReqVO reqVO) {
        // 校验短信验证码
        String username = reqVO.getUsername();
//        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO().setMobile(username).setUsedIp(getClientIP()).setCode(reqVO.getCode()).setScene(SmsSceneEnum.MEMBER_REGISTER_MOBILE.getScene())).checkError();
        ImUserDO user = userService.getUserByUsername(username);
        if (Objects.nonNull(user)) {
            throw exception(USER_MOBILE_ALREADY_EXISTS);
        }
        user = userService.createUserIfAbsent(username,reqVO.getNickname(), reqVO.getPassword(), getClientIP());
        return createTokenAfterLoginSuccess(user, username, LoginLogTypeEnum.LOGIN_MOBILE, null);
    }

    @Override
    public AppImAuthLoginRespVO login(AppImAuthLoginReqVO reqVO) {
        // 使用手机 + 密码，进行登录。
        ImUserDO user = login0(reqVO.getUsername(), reqVO.getPassword());
        // 创建 Token 令牌，记录登录日志
        return createTokenAfterLoginSuccess(user, reqVO.getUsername(), LoginLogTypeEnum.LOGIN_MOBILE, null);
    }

    private AppImAuthLoginRespVO createTokenAfterLoginSuccess(ImUserDO user, String username,
                                                            LoginLogTypeEnum logType, String openid) {
        // 插入登陆日志
        createLoginLog(user.getId(), username, logType, LoginResultEnum.SUCCESS);
        // 创建 Token 令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi.createAccessToken(new OAuth2AccessTokenCreateReqDTO()
                .setUserId(user.getId()).setUserType(getUserType().getValue())
                .setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT)).getCheckedData();
        // 构建返回结果
        return ImAuthConvert.INSTANCE.convert(accessTokenRespDTO, openid);
    }

    private ImUserDO login0(String username, String password) {
        final LoginLogTypeEnum logTypeEnum = LoginLogTypeEnum.LOGIN_MOBILE;
        // 校验账号是否存在
        ImUserDO user = userService.getUserByUsername(username);
        if (user == null) {
            createLoginLog(null, username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        if (!userService.isPasswordMatch(password, user.getPassword())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.BAD_CREDENTIALS);
            throw exception(AUTH_LOGIN_BAD_CREDENTIALS);
        }
        // 校验是否禁用
        if (CommonStatusEnum.isDisable(user.getStatus())) {
            createLoginLog(user.getId(), username, logTypeEnum, LoginResultEnum.USER_DISABLED);
            throw exception(AUTH_LOGIN_USER_DISABLED);
        }
        return user;
    }

    private void createLoginLog(Long userId, String mobile, LoginLogTypeEnum logType, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(mobile);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(loginResult.getResult());
        // 标记为新登录会话（在线），交由 system 模块的 system_login_log 统一承载，避免与 im_user_login_log 重复记录
        reqDTO.setTerminal(getTerminal());
        reqDTO.setStatus(LoginLogStatusEnum.ONLINE.getStatus());
        loginLogApi.createLoginLog(reqDTO).checkError();
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, getClientIP(), getTerminal(), ServletUtils.getUserAgent());
        }
    }

    @Override
    public void logout(String token) {
        // 删除访问令牌
        OAuth2AccessTokenRespDTO accessTokenRespDTO = oauth2TokenApi.removeAccessToken(token).getCheckedData();
        if (accessTokenRespDTO == null) {
            return;
        }
        // 删除成功，则记录登出日志
        createLogoutLog(accessTokenRespDTO.getUserId());
    }


    @Override
    public AppImAuthLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenRespDTO accessTokenDO = oauth2TokenApi.refreshAccessToken(refreshToken,
                OAuth2ClientConstants.CLIENT_ID_DEFAULT).getCheckedData();
        return ImAuthConvert.INSTANCE.convert(accessTokenDO, null);
    }

    private void createLogoutLog(Long userId) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(LoginLogTypeEnum.LOGOUT_SELF.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(getUsername(userId));
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        reqDTO.setStatus(LoginLogStatusEnum.OFFLINE.getStatus());
        loginLogApi.createLoginLog(reqDTO).checkError();
    }

    private String getUsername(Long userId) {
        if (userId == null) {
            return null;
        }
        ImUserDO user = userService.getUser(userId);
        return user != null ? user.getUsername() : null;
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.MEMBER;
    }

}
