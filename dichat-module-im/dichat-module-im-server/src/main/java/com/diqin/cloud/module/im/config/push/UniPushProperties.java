package com.diqin.cloud.module.im.config.push;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * UniPush（个推）服务端推送配置
 *
 * <p>
 * 配置项：</p>
 * <pre>
 * dichat.im.push.unipush:
 *   enabled: true
 *   app-id: your-app-id
 *   app-key: your-app-key
 *   app-secret: your-app-secret
 *   master-secret: your-master-secret
 *   base-url: https://restapi.getui.com/v2/
 *   offline-message-expire-hours: 72
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "dichat.im.push.unipush")
@Data
public class UniPushProperties {

    /**
     * 是否启用 UniPush 推送
     */
    private boolean enabled = false;

    /**
     * UniPush AppId（DCloud 开发者中心获取）
     */
    private String appId;

    /**
     * UniPush AppKey（DCloud 开发者中心获取）
     */
    private String appKey;

    /**
     * UniPush AppSecret（DCloud 开发者中心获取）
     */
    private String appSecret;

    /**
     * UniPush MasterSecret（DCloud 开发者中心获取）
     */
    private String masterSecret;

    /**
     * 个推 REST API 基础地址
     */
    private String baseUrl = "https://restapi.getui.com/v2/";

    /**
     * 离线消息有效时长（小时），超过此时间的消息不再推送
     */
    private int offlineMessageExpireHours = 72;
}
