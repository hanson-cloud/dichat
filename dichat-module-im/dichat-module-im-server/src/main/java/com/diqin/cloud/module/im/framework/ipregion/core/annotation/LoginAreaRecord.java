package com.diqin.cloud.module.im.framework.ipregion.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录区域回写注解，标记在登录方法上。
 * <p>
 * 由 {@link com.diqin.cloud.module.im.framework.ipregion.core.aop.LoginAreaAspect} 拦截，
 * 在登录成功（方法正常返回、事务提交前）基于登录客户端 IP 异步回写区域编号 area_id 到 im_users，
 * 整个过程不阻塞登录响应、不影响登录结果。
 * </p>
 * <p>
 * 真正的 IP→area_id 查询（走 system 远程 {@code AreaApi}）与落库，由
 * {@link com.diqin.cloud.module.im.framework.ipregion.core.aop.LoginAreaWritebackListener}
 * 在事务提交后异步执行，规避「@Async 标在自身方法、被 this 自调用导致异步静默失效」的陷阱。
 * </p>
 *
 * @author hanson
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface LoginAreaRecord {

    /**
     * 用户编号在方法参数中的下标（默认 0，对齐 {@code ImUserServiceImpl#updateUserLogin(Long, String, ...)}）
     */
    int userIdIndex() default 0;

    /**
     * 登录 IP 在方法参数中的下标（默认 1）
     */
    int loginIpIndex() default 1;

}
