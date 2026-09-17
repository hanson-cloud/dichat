package com.diqin.cloud.module.im.framework.ipregion.core.aop;

import com.diqin.cloud.framework.tenant.core.context.TenantContextHolder;
import com.diqin.cloud.module.im.framework.ipregion.core.annotation.LoginAreaRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 登录区域回写的 Aspect，基于 {@link LoginAreaRecord} 注解实现。
 * <p>
 * 使用 {@code @AfterReturning} + 最高优先级 {@link Ordered#HIGHEST_PRECEDENCE}：
 * 保证本切面位于事务切面的外层，从而在事务提交之后才触发回写，避免读到未提交数据或回滚后的脏投递。
 * </p>
 * <p>
 * 本切面自身只做「取值 + publish 事件」两件事，不发起任何 RPC；
 * 真正的 IP 归属地查询与落库，交给 {@link LoginAreaWritebackListener} 在事务提交后异步执行。
 * 落库目标为 im_users.area_id。
 * </p>
 *
 * @author hanson
 */
@Aspect
@Component
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoginAreaAspect {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    /**
     * 登录成功后，投递区域回写任务
     *
     * @param loginAreaRecord 登录区域回写注解
     * @param joinPoint       切点
     */
    @AfterReturning(value = "@annotation(loginAreaRecord)", argNames = "loginAreaRecord,joinPoint")
    public void afterLoginSuccess(LoginAreaRecord loginAreaRecord, JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Long userId = argAt(args, loginAreaRecord.userIdIndex(), Long.class);
            String ip = argAt(args, loginAreaRecord.loginIpIndex(), String.class);
            if (userId == null || ip == null || ip.isBlank()) {
                return;
            }
            // 在请求线程内获取租户编号，透传给异步线程显式设置租户上下文
            Long tenantId = TenantContextHolder.getTenantId();
            // publish 事件，真正的 IP 归属地查询与落库由 LoginAreaWritebackListener 在事务提交后异步执行；
            // 任何 publish 异常都不得影响登录响应
            eventPublisher.publishEvent(new LoginAreaWritebackEvent(this, userId, ip.trim(), tenantId));
        } catch (Exception e) {
            log.warn("[login-area] 投递区域回写事件失败 userId={}", e.getMessage());
        }
    }

    /**
     * 按下标安全读取方法参数
     */
    private static <T> T argAt(Object[] args, int index, Class<T> type) {
        if (args == null || index < 0 || index >= args.length) {
            return null;
        }
        Object value = args[index];
        return type.isInstance(value) ? type.cast(value) : null;
    }

}
