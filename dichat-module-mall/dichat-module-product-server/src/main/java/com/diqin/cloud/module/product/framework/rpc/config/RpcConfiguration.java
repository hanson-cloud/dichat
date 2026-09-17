package com.diqin.cloud.module.product.framework.rpc.config;

import com.diqin.cloud.module.member.api.level.MemberLevelApi;
import com.diqin.cloud.module.member.api.user.MemberUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "productRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {MemberUserApi.class, MemberLevelApi.class})
public class RpcConfiguration {
}
