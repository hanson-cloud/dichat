package com.diqin.cloud.module.ai.framework.rpc.config;

import com.diqin.cloud.module.infra.api.file.FileApi;
import com.diqin.cloud.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "aiRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, AdminUserApi.class})
public class RpcConfiguration {
}
