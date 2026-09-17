package com.diqin.cloud.module.im.service.customer_service.event;

import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.framework.mq.redis.core.RedisMQTemplate;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceConsoleServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客服消息 SSE 广播总线
 * <p>按客服编号（csId）维护 {@link SseEmitter} 集合；{@link CsMessageEventListener} 收到
 * {@link CsMessageEvent} 后调用 {@link #publish(CsMessageEvent)} 向<b>所有实例</b>广播，各实例的
 * {@link CsMessageEventRedisConsumer} 收到后再调用 {@link #publishLocal(Long, ImPrivateMessageDO)} 做本实例的 SSE 本地投递。</p>
 *
 * <p><b>修复说明（2026-07-25）</b>：原实现为纯进程内内存广播——A 实例处理消息、B 实例承载
 * 管理员工作台 SSE 连接时，实时推送会丢失，管理端只能靠刷新从 DB 重查才显示。现桥接到
 * Redis Pub/Sub（与项目既有的 WebSocket 多实例方案 {@code RedisWebSocketMessage} 同构），使客服工作台
 * SSE 在单实例 / 多实例下均能实时送达。</p>
 *
 * @author 速构构
 */
@Slf4j
@Component
public class CsMessageEventBus {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 每个客服编号（csId）当前的 SSE 连接数。
     * <p>用于实现「工作台打开即上线、全部关闭即下线」的连接计数：从 0→1 视为首次连上（自动进入忙碌），
     * 从 1→0 视为全部断开（自动下线）。多标签页 / 重连场景下据此避免「一个标签页关掉就把其他在线坐席踢下线」。</p>
     */
    private final Map<Long, Integer> connCounts = new ConcurrentHashMap<>();

    /**
     * 连接状态监听器：由 {@link ImCustomerServiceConsoleServiceImpl} 注册，承载「上线/下线」对
     * {@code im_customer_service.status} 的副作用写库（保持本总线仅负责 SSE 投递，不耦合业务状态）。
     */
    @Setter
    private CsConnStateListener connListener;

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 本实例唯一编号：随 Bean 创建时生成，用于 Redis 广播时标识「消息由哪个实例发出」，
     * 消费端据此跳过自己发出的消息，避免重复投递。
     */
    @Getter
    private final String instanceId = UUID.randomUUID().toString();

    /**
     * SSE 心跳保活调度器（守护线程）。
     * <p>管理端 SSE 经网关（grayLb://im-server）转发，空闲连接可能被网关 / 反向代理的
     * idle 超时掐断；断流后管理端收不到实时消息，只能刷新重连。每 20s 发一个 SSE comment
     * 保活，使空闲连接不被误杀。</p>
     */
    private final ScheduledExecutorService heartbeat =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "cs-sse-heartbeat");
                t.setDaemon(true);
                return t;
            });

    @PostConstruct
    public void init() {
        heartbeat.scheduleAtFixedRate(() -> {
            try {
                for (Map.Entry<Long, CopyOnWriteArrayList<SseEmitter>> entry : emitters.entrySet()) {
                    for (SseEmitter emitter : entry.getValue()) {
                        try {
                            emitter.send(SseEmitter.event().comment("ping"));
                        } catch (Exception e) {
                            // 连接已断开：complete() 关闭 HTTP 流，令客户端感知并重连
                            removeAndComplete(entry.getKey(), emitter);
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("[CsMessageEventBus][心跳异常]", e);
            }
        }, 20, 20, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void destroy() {
        heartbeat.shutdownNow();
    }

    public void register(Long csId, SseEmitter emitter) {
        emitters.computeIfAbsent(csId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        // 连接数 0→1 视为「首次连上工作台」：通知监听器自动置为忙碌(2)
        boolean[] first = {false};
        connCounts.compute(csId, (k, v) -> {
            if (v == null) {
                first[0] = true;
                return 1;
            }
            return v + 1;
        });
        if (first[0] && connListener != null) {
            connListener.onFirstConnect(csId);
        }
    }

    public void unregister(Long csId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(csId);
        if (list != null) {
            list.remove(emitter);
            // 列表空了就回收 key，避免 emitters Map 随 csId 数量无限增长
            if (list.isEmpty()) {
                emitters.remove(csId, list);
            }
        }
        // 连接数 1→0 视为「全部断开（关页 / 心跳丢失 / 网关断流）」：通知监听器自动下线(0)。
        // 仅最后一个连接断开时才触发，避免多标签页时一个关掉就误踢其他在线坐席。
        boolean[] last = {false};
        connCounts.compute(csId, (k, v) -> {
            if (v == null) {
                return null;
            }
            if (v <= 1) {
                last[0] = true;
                return null;
            }
            return v - 1;
        });
        if (last[0] && connListener != null) {
            connListener.onLastDisconnect(csId);
        }
    }

    /**
     * 安全移除并关闭失效的 SSE 连接。
     * <p><b>这是修复「用户没结束、客服没关闭，但后续消息收不到」的关键。</b>失效的 emitter
     * 必须先 {@link SseEmitter#complete()} 关闭底层 HTTP 响应，客户端（fetch-event-source）才会感知到
     * 流结束并自动按退避重连、重新注册 emitter；若只 {@code unregister} 而不断开 HTTP，连接会处于
     * 半开悬挂状态，客户端无法感知、永不重试，实时推送就此静默失效。</p>
     */
    private void removeAndComplete(Long csId, SseEmitter emitter) {
        try {
            emitter.complete();
        } catch (Exception ignored) {
            // 已完成 / 已超时等状态下 complete() 可能抛 IllegalStateException，忽略即可
        }
        unregister(csId, emitter);
    }

    /**
     * 进程内事件入口（由 {@link CsMessageEventListener} 调用）。
     * <p><b>投递策略（单实例 / 多实例均正确，且不重复）：</b></p>
     * <ol>
     *   <li>本实例<b>先进程内直推</b>（{@link #publishLocal}），保证连到本实例的客服工作台
     *       无论 Redis 是否可用都能实时收到——这是上一版「纯 Redis 广播」在 Redis 未启动时
     *       整条静默失效的根因修复；</li>
     *   <li>再经 Redis Pub/Sub 广播到<b>其它实例</b>，由其 {@link CsMessageEventRedisConsumer} 回收到
     *       {@link #publishLocal} 完成本地 SSE 投递；</li>
     *   <li>消息携带 {@code instanceId}，消费端收到「自己发出的」消息时跳过，避免同一条被投递两次。</li>
     * </ol>
     */
    public void publish(CsMessageEvent event) {
        // 1. 本实例直推（不依赖 Redis，单实例下也能实时送达）
        publishLocal(event.getCsId(), event.getMessage());
        // 2. 广播给其它实例（Redis 不可用时不阻塞本实例投递）
        CsMessageEventRedisMessage mq = new CsMessageEventRedisMessage()
                .setCsId(event.getCsId())
                .setInstanceId(instanceId)
                .setMessageJson(JsonUtils.toJsonString(event.getMessage()));
        try {
            redisMQTemplate.send(mq);
        } catch (Exception e) {
            log.warn("[CsMessageEventBus][Redis 广播失败（仅影响其它实例）csId({})]", event.getCsId(), e);
        }
    }

    /**
     * 本实例 SSE 本地投递（由 {@link CsMessageEventRedisConsumer} 调用）。
     *
     * @param csId   客服编号（im_customer_service.id）
     * @param message 落库后的消息（ImPrivateMessageDO）
     */
    public void publishLocal(Long csId, ImPrivateMessageDO message) {
        List<SseEmitter> list = emitters.get(csId);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event().data(message));
            } catch (Exception e) {
                // 连接已断开 / 写失败：complete() 关闭 HTTP 响应，客户端才会重连并重新注册；
                // 仅 unregister 会让连接半开悬挂，导致后续消息静默丢失。
                removeAndComplete(csId, emitter);
            }
        }
    }

    /**
     * SSE 连接状态监听器。由业务 Service 注册，承载「连线/断线」对客服在线状态的副作用，
     * 使本总线保持「只管 SSE 投递」的单一职责。
     */
    public interface CsConnStateListener {
        /** 该客服首次连上工作台（连接数 0→1）——实现自动进入忙碌 */
        void onFirstConnect(Long csId);

        /** 该客服全部连接断开（连接数 1→0）——实现自动下线 */
        void onLastDisconnect(Long csId);
    }

}
