package com.diqin.cloud.framework.websocket.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * WebSocket 配置项
 *
 * @author xingyu4j
 */
@ConfigurationProperties("dichat.websocket")
@Data
@Accessors(chain = true)
@Validated
public class WebSocketProperties {

    /**
     * 是否开启
     */
    private boolean enable = false;

    /**
     * WebSocket 的连接路径
     */
    @NotEmpty(message = "WebSocket 的连接路径不能为空")
    private String path = "/ws/**";

    /**
     * 消息发送器的类型
     * 可选值：local、redis、rocketmq、kafka、rabbitmq
     */
    @NotNull(message = "WebSocket 的消息发送者不能为空")
    private String senderType = "local";

    /**
     * 是否启用离线消息存储
     */
    private Boolean offlineMessageEnabled = false;

    /**
     * 离线消息配置
     */
    private OfflineMessageProperties offlineMessage = new OfflineMessageProperties();

    /**
     * 心跳配置
     */
    private HeartbeatProperties heartbeat = new HeartbeatProperties();

    private SenderRocketmqProperties senderRocketmq = new SenderRocketmqProperties();

    private SenderRabbitmqProperties senderRabbitmq = new SenderRabbitmqProperties();

    private SenderKafkaProperties senderKafka = new SenderKafkaProperties();

    @Data
    @Accessors(chain = true)
    public static class HeartbeatProperties {
        /**
         * 是否启用心跳检测
         */
        private Boolean enabled = true;

        /**
         * 心跳间隔（秒）
         */
        private Long interval = 30L;

        /**
         * 心跳超时时间（秒）
         */
        private Long timeout = 60L;
    }

    @Data
    @Accessors(chain = true)
    public static class OfflineMessageProperties {
        /**
         * 离线消息过期时间（天），默认7天
         */
        private Integer expireDays = 7;

        /**
         * 离线消息最大重试次数
         */
        private Integer maxRetryCount = 3;

        /**
         * 离线消息重试间隔（分钟）
         */
        private Integer retryIntervalMinutes = 5;

        /**
         * 每次拉取离线消息的最大数量
         */
        private Integer maxBatchSize = 100;
    }

    @Data
    @Accessors(chain = true)
    public static class SenderRocketmqProperties {
        /**
         * RocketMQ 的 topic
         */
        private String topic = "dichat-websocket";
        /**
         * RocketMQ 的 consumer group
         */
        private String consumerGroup = "dichat-websocket-consumer";
    }

    @Data
    @Accessors(chain = true)
    public static class SenderRabbitmqProperties {
        /**
         * RabbitMQ 的 exchange
         */
        private String exchange = "dichat-websocket-exchange";
        /**
         * RabbitMQ 的 routing key
         */
        private String queue = "dichat-websocket-queue";
    }

    @Data
    @Accessors(chain = true)
    public static class SenderKafkaProperties {
        /**
         * Kafka 的 topic
         */
        private String topic = "dichat-websocket";
        /**
         * Kafka 的 consumer group
         */
        private String consumerGroup = "dichat-websocket-consumer";
    }
}
