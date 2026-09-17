package com.diqin.cloud.module.im.framework.rpc.config;

import com.diqin.cloud.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import com.diqin.cloud.module.infra.api.file.FileApi;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.system.api.area.AreaApi;
import com.diqin.cloud.module.system.api.logger.LoginLogApi;
import com.diqin.cloud.module.system.api.oauth2.TokenApi;
import com.diqin.cloud.module.system.api.sms.SmsCodeApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "imRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, LoginLogApi.class, OAuth2TokenCommonApi.class, SmsCodeApi.class,
        PayWalletApi.class, TokenApi.class, AreaApi.class})
public class RpcConfiguration {
}
