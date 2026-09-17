package com.diqin.cloud.module.im.service.push;

import com.diqin.cloud.module.im.config.push.UniPushProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * UniPush（个推）服务端推送客户端
 *
 * <p>
 * 封装个推 REST API v2，支持：
 * <ul>
 *   <li>OAuth2 鉴权（获取 access_token）</li>
 *   <li>单推（按 alias — userId）</li>
 *   <li>通知栏消息构建</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "dichat.im.push.unipush", name = "enabled", havingValue = "true")
public class UniPushClient {

    @Resource
    private UniPushProperties properties;

    @Resource
    private RestTemplate restTemplate;

    /** token 缓存 */
    private String cachedToken;
    private LocalDateTime tokenExpireTime;

    /**
     * 获取鉴权 token（自动缓存，过期前 10 分钟刷新）
     */
    public synchronized String getAccessToken() {
        if (cachedToken != null && tokenExpireTime != null
                && LocalDateTime.now().isBefore(tokenExpireTime.minusMinutes(10))) {
            return cachedToken;
        }

        String url = properties.getBaseUrl() + properties.getAppId() + "/auth";
        Map<String, Object> body = new HashMap<>();
        body.put("appkey", properties.getAppKey());
        body.put("masterSecret", properties.getMasterSecret());

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
            if (response.getBody() != null && (int) response.getBody().get("code") == 0) {
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                String token = (String) data.get("token");
                int expireIn = (int) data.get("expire_time"); // 秒
                cachedToken = token;
                tokenExpireTime = LocalDateTime.now().plusSeconds(expireIn);
                log.info("[UniPush] 获取 token 成功，有效期 {} 秒", expireIn);
                return token;
            }
            log.warn("[UniPush] 获取 token 失败: {}", response.getBody());
        } catch (Exception e) {
            log.error("[UniPush] 获取 token 异常", e);
        }
        return null;
    }

    /**
     * 向指定用户（alias = userId）推送通知栏消息
     *
     * @param userId      用户编号（会作为 alias 推送）
     * @param title       通知标题（发送者昵称或系统通知标题）
     * @param body        通知正文
     * @param payload     透传数据（JSON string，包含 convKey, messageId, action 等）
     * @return true 表示推送成功
     */
    public boolean pushToSingleByAlias(Long userId, String title, String body, String payload) {
        String token = getAccessToken();
        if (token == null) {
            log.warn("[UniPush] 无有效 token，跳过推送 userId={}", userId);
            return false;
        }

        String url = properties.getBaseUrl() + properties.getAppId() + "/push/single/alias";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        // 构建推送请求体（个推 v2 格式）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("request_id", System.currentTimeMillis() + "_" + userId);

        // 推送目标
        Map<String, Object> alias = new HashMap<>();
        alias.put("alias", String.valueOf(userId));
        requestBody.put("audience", Map.of("alias", alias));

        // 通知栏消息
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("body", body);
        notification.put("click_type", "payload");
        notification.put("payload", payload != null ? payload : "{}");

        // 推送通道策略
        Map<String, Object> settings = new HashMap<>();
        settings.put("ttl", properties.getOfflineMessageExpireHours() * 3600 * 1000); // 毫秒

        // 策略：在线走 WS（由业务逻辑处理），离线走通知栏
        Map<String, Object> strategy = new HashMap<>();
        strategy.put("default", 1); // 1=通知栏
        requestBody.put("strategy", strategy);

        // 消息体
        Map<String, Object> message = new HashMap<>();
        message.put("notification", notification);
        message.put("settings", settings);
        requestBody.put("push_message", message);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            if (response.getBody() != null && (int) response.getBody().get("code") == 0) {
                log.debug("[UniPush] 推送成功 userId={} title={}", userId, title);
                return true;
            }
            log.warn("[UniPush] 推送失败 userId={} response={}", userId, response.getBody());
        } catch (Exception e) {
            log.error("[UniPush] 推送异常 userId={}", userId, e);
        }
        return false;
    }
}
