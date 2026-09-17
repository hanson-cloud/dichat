package com.diqin.cloud.module.system.framework.rpc.config;

import com.diqin.cloud.module.im.api.user.ImUserApi;
import com.diqin.cloud.module.infra.api.config.ConfigApi;
import com.diqin.cloud.module.infra.api.file.FileApi;
import com.diqin.cloud.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class, ImUserApi.class})
public class RpcConfiguration {
}
