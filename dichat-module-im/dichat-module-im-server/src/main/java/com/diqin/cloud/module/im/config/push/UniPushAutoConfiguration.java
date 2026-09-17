package com.diqin.cloud.module.im.config.push;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * UniPush 推送自动配置
 *
 * <p>
 * 仅在 {@code dichat.im.push.unipush.enabled=true} 时生效。
 * 提供 UniPush 所需的 RestTemplate Bean。
 * </p>
 */
@Configuration
@ConditionalOnProperty(prefix = "dichat.im.push.unipush", name = "enabled", havingValue = "true")
public class UniPushAutoConfiguration {

    @Bean
    public RestTemplate uniPushRestTemplate() {
        return new RestTemplate();
    }
}
