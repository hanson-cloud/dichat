package com.diqin.cloud.framework.websocket.core.offline.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.diqin.cloud.framework.websocket.config.WebSocketProperties;
import com.diqin.cloud.framework.websocket.core.offline.dal.dataobject.WsOfflineMessageDO;
import com.diqin.cloud.framework.websocket.core.offline.dal.mysql.WsOfflineMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 离线消息服务实现
 *
 * @author hanson
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WsOfflineMessageServiceImpl extends ServiceImpl<WsOfflineMessageMapper, WsOfflineMessageDO> implements WsOfflineMessageService {

    private final WebSocketProperties webSocketProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<WsOfflineMessageDO> pullAndSendOfflineMessages(Long tenantId, Integer userType, Long userId, Integer limit) {
        if (Boolean.FALSE.equals(webSocketProperties.getOfflineMessageEnabled())) {
            return Collections.emptyList();
        }

        // 1. 拉取离线消息
        Integer batchSize = limit != null ? limit : webSocketProperties.getOfflineMessage().getMaxBatchSize();
        return baseMapper.selectByUserAndNotSent(
                tenantId, userType, userId, batchSize);
    }

    @Override
    public void cleanExpiredMessages() {
        if (Boolean.FALSE.equals(webSocketProperties.getOfflineMessageEnabled())) {
            return;
        }

        LocalDateTime expireTime = LocalDateTime.now();
        int cleanedCount = baseMapper.cleanExpiredOrFailedMessages(
                expireTime,
                webSocketProperties.getOfflineMessage().getMaxRetryCount());

        if (cleanedCount > 0) {
            log.info("[cleanExpiredMessages] 清理过期离线消息 {} 条", cleanedCount);
        }
    }

    @Override
    public void incrementRetryCount(Long id) {
        baseMapper.incrementRetryCount(id);
    }

    @Override
    public void updateSentByIds(List<Long> successIds) {
        baseMapper.updateSentByIds(successIds, LocalDateTime.now());
    }

    /**
     * 定时清理过期消息（每天0点执行一次）
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledCleanExpiredMessages() {
        if (Boolean.TRUE.equals(webSocketProperties.getOfflineMessageEnabled())) {
            cleanExpiredMessages();
        }
    }
}
