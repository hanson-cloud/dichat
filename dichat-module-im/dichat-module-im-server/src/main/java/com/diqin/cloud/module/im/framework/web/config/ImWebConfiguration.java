package com.diqin.cloud.module.im.framework.web.config;

import com.diqin.cloud.framework.swagger.config.DichatSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * im 模块的 web 组件的 Configuration
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
public class ImWebConfiguration {

    /**
     * im 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi imGroupedOpenApi() {
        return DichatSwaggerAutoConfiguration.buildGroupedOpenApi("im");
    }

}
