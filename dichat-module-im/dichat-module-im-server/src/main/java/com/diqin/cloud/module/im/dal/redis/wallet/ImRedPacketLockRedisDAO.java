package com.diqin.cloud.module.im.dal.redis.wallet;

import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.diqin.cloud.module.im.dal.redis.RedisKeyConstants.IM_RED_PACKET_LOCK;

/**
 * IM 红包抢领并发分布式锁 Redis DAO
 * <p>
 * 同一红包的多人抢领用一把按 {@code redPacketId} 的锁串行，配合明细 {@code WHERE status = 0}
 * 行级条件 + 乐观锁，彻底防同一份红包被两人同时抢中（超领）。
 *
 * @author dichat
 */
@Repository
@Slf4j
public class ImRedPacketLockRedisDAO {

    /**
     * 等待获取锁的最长时间；超时抛 WALLET_BUSY（复用钱包繁忙码，语义一致）
     */
    private static final long LOCK_WAIT_MS = 5_000L;
    /**
     * 持有锁的最长时间；自动释放兜底；给 DB / 推送偶发慢留余地
     */
    private static final long LOCK_LEASE_MS = 30_000L;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 红包级锁；按 redPacketId 拼 key
     */
    public <V> V lockPacket(Long redPacketId, Callable<V> callable) throws Exception {
        String key = String.format(IM_RED_PACKET_LOCK, redPacketId);
        return doLock(key, callable);
    }

    /**
     * tryLock(waitTime, leaseTime, unit)：waitTime 内拿不到锁直接抛繁忙；拿到后 leaseTime 自动释放
     * <p>
     * unlock 前用 isHeldByCurrentThread 兜底；业务超过 leaseTime 时锁已自动释放，不再抛 IllegalMonitorStateException
     */
    private <V> V doLock(String lockKey, Callable<V> callable) throws Exception {
        RLock lock = redissonClient.getLock(lockKey);
        boolean acquired = lock.tryLock(LOCK_WAIT_MS, LOCK_LEASE_MS, TimeUnit.MILLISECONDS);
        if (!acquired) {
            log.error("[doLock][lockKey={} 等待 {}ms 仍未获取到锁]", lockKey, LOCK_WAIT_MS);
            throw exception(ErrorCodeConstants.WALLET_BUSY);
        }
        try {
            return callable.call();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            } else {
                log.error("[doLock][lockKey={} 业务超过 leaseTime={}ms，锁已被 Redisson 自动释放]", lockKey, LOCK_LEASE_MS);
            }
        }
    }

}
