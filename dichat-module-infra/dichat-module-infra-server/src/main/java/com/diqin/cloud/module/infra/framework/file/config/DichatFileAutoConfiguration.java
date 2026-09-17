package com.diqin.cloud.module.infra.framework.file.config;

import com.diqin.cloud.module.infra.framework.file.core.client.FileClientFactory;
import com.diqin.cloud.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author hanson
 */
@Configuration(proxyBeanMethods = false)
public class DichatFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
