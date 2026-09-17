package com.diqin.cloud.framework.env.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置
 *
 * @author hanson
 */
@ConfigurationProperties(prefix = "dichat.env")
@Data
public class EnvProperties {

    public static final String TAG_KEY = "dichat.env.tag";

    /**
     * 环境标签
     */
    private String tag;

}
