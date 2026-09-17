package com.diqin.cloud.config;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class GlobalTransactionFallbackConfig {

    @Bean
    public Advisor globalTransactionalAdvisor(TransactionManager transactionManager) {
        // 1. 创建 Spring 事务拦截器，并注入事务管理器
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager);
        interceptor.setTransactionAttributeSource(new GlobalTransactionalAttributeSource());

        // 2. 定义切点：匹配所有标注了 @GlobalTransactional 的方法（包括类级别）
        Pointcut pointcut = new AnnotationMatchingPointcut(null, GlobalTransactional.class);

        // 3. 返回 Advisor
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }

    /**
     * 自定义属性源：将 @GlobalTransactional 的属性映射为 Spring 的 TransactionAttribute
     */
    private static class GlobalTransactionalAttributeSource implements TransactionAttributeSource {

        @Override
        public TransactionAttribute getTransactionAttribute(@NotNull Method method, Class<?> targetClass) {
            // 查找方法上的注解，若没有则查找类上的注解（与 @Transactional 行为一致）
            GlobalTransactional gt = AnnotatedElementUtils.findMergedAnnotation(method, GlobalTransactional.class);
            if (gt == null && targetClass != null) {
                gt = AnnotatedElementUtils.findMergedAnnotation(targetClass, GlobalTransactional.class);
            }
            if (gt == null) {
                return null; // 该无事务需求
            }

            // 创建事务属性（默认 REQUIRED）
            DefaultTransactionAttribute attr = new DefaultTransactionAttribute();
            attr.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

            // 映射超时时间（毫秒 -> 秒）
            if (gt.timeoutMills() > 0) {
                attr.setTimeout(gt.timeoutMills() / 1000);
            }

            // 映射回滚异常列表（默认只回滚 RuntimeException 和 Error）
            List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
            for (Class<? extends Throwable> rollbackClass : gt.rollbackFor()) {
                rollbackRules.add(new RollbackRuleAttribute(rollbackClass));
            }
            // 如果用户明确指定了 rollbackFor，则覆盖 Spring 的默认回滚规则
            if (!rollbackRules.isEmpty()) {
                // 使用 RuleBasedTransactionAttribute 以支持回滚规则
                RuleBasedTransactionAttribute ruleAttr = new RuleBasedTransactionAttribute();
                ruleAttr.setPropagationBehavior(attr.getPropagationBehavior());
                ruleAttr.setTimeout(attr.getTimeout());
                ruleAttr.setRollbackRules(rollbackRules);
                return ruleAttr;
            }

            // 如果未指定 rollbackFor，则保持默认（即 RuntimeException 和 Error 回滚）
            return attr;
        }
    }
}