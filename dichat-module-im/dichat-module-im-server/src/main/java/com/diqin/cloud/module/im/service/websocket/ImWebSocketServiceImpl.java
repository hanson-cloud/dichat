package com.diqin.cloud.module.im.service.websocket;

import com.diqin.cloud.module.im.service.websocket.dto.ImChannelMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImGroupMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

/**
 * IM WebSocket 事务调度层
 *
 * <p>
 * 职责：
 * 1. 控制事务提交后再触发 WebSocket 推送
 * 2. 不直接执行 IO
 * 3. 不包含 @Async
 * 4. 不直接依赖 SpringUtil
 * 改造点：
 * ❌ 移除 SpringUtil.getBean
 * ❌ 移除 getSelf
 * ❌ 移除 AOP 自调用
 * ✔ 改为调用 ImWebSocketAsyncService（真正执行发送）
 *
 * @author hanson
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class ImWebSocketServiceImpl implements ImWebSocketService {

    private final ImWebSocketAsyncService asyncService;

    @Override
    public void sendPrivateMessageAsync(Collection<Long> userIds, ImPrivateMessageDTO dto) {
        executeAfterTransaction(() -> asyncService.sendPrivateMessage(userIds, dto));
    }

    @Override
    public void sendGroupMessageAsync(Collection<Long> userIds, ImGroupMessageDTO dto) {
        executeAfterTransaction(() -> asyncService.sendGroupMessage(userIds, dto));
    }

    @Override
    public void sendChannelMessageAsync(Collection<Long> userIds, ImChannelMessageDTO dto) {
        executeAfterTransaction(() -> asyncService.sendChannelMessage(userIds, dto));
    }

    @Override
    public void broadcastChannelMessageAsync(ImChannelMessageDTO dto) {
        executeAfterTransaction(() -> asyncService.broadcastChannelMessage(dto));
    }

    @Override
    public void broadcastToMembersAsync(ImPrivateMessageDTO dto) {
        executeAfterTransaction(() -> asyncService.broadcastToMembers(dto));
    }

    /**
     * 事务后执行策略
     * 1. 如果没有事务 → 直接执行
     * 2. 如果有事务 → afterCommit 执行
     */
    private void executeAfterTransaction(Runnable task) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            task.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            task.run();
                        } catch (Exception e) {
                            log.error("[ImWebSocket] afterCommit 执行失败", e);
                        }
                    }
                }
        );
    }

}