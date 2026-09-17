package com.diqin.cloud.module.im.convert.auth;

import com.diqin.cloud.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginRespVO;
import com.diqin.cloud.module.system.api.social.dto.SocialWxJsapiSignatureRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ImAuthConvert {

    ImAuthConvert INSTANCE = Mappers.getMapper(ImAuthConvert.class);

//    SocialUserBindReqDTO convert(Long userId, Integer userType, AppAuthSocialLoginReqVO reqVO);
//    SocialUserUnbindReqDTO convert(Long userId, Integer userType, AppSocialUserUnbindReqVO reqVO);
//
//    SmsCodeSendReqDTO convert(AppAuthSmsSendReqVO reqVO);
//    SmsCodeUseReqDTO convert(AppMemberUserResetPasswordReqVO reqVO, SmsSceneEnum scene, String usedIp);
//    SmsCodeUseReqDTO convert(AppAuthSmsLoginReqVO reqVO, Integer scene, String usedIp);

    AppImAuthLoginRespVO convert(OAuth2AccessTokenRespDTO bean, String openid);

//    SmsCodeValidateReqDTO convert(AppAuthSmsValidateReqVO bean);

    SocialWxJsapiSignatureRespDTO convert(SocialWxJsapiSignatureRespDTO bean);

}
