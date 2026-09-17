package com.diqin.cloud.module.im.framework.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * IM 异步线程池配置
 * <p>
 * 提供名为 {@code loginAreaExecutor} 的线程池，专供
 * {@code LoginAreaWritebackListener} 的 {@code @Async("loginAreaExecutor")}
 * 使用，让「登录事务提交后回写区域编号」的慢 RPC 跑在独立线程，不阻塞登录主线程。
 * 队列满时采用 CallerRunsPolicy 兜底，避免异步回写丢失（最多退化为调用方线程同步执行）。
 *
 * @author hanson
 */
@Configuration(proxyBeanMethods = false)
public class ImAsyncConfiguration {

    @Bean("loginAreaExecutor")
    public Executor loginAreaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("login-area-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
