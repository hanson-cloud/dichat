package com.diqin.cloud.module.im.framework.ipregion.core.aop;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 登录成功后的「区域回写」事件载荷。
 * <p>
 * 由 {@link LoginAreaAspect} 在请求线程内 publish（此时登录客户端 IP、租户等上下文仍可用）；
 * 真正的 IP 归属地查询与落库，由 {@link LoginAreaWritebackListener} 在事务提交后异步执行。
 * </p>
 * <p>这样设计可同时保证：
 * <ol>
 *   <li>登录主线程仅完成「取值 + publish」即返回，零等待慢 RPC（{@code areaApi.getByIp}）；</li>
 *   <li>回写仅在登录事务成功提交后触发，异步任务能看到已提交的数据，且不会因事务回滚而误触发；</li>
 *   <li>本监听者与切面分属不同 Bean，且事件由事务同步器在提交后再派发，
 *       彻底规避「@Async 标在自身方法、被 this 自调用导致异步静默失效」的陷阱。</li>
 * </ol>
 *
 * @author hanson
 */
@Getter
public class LoginAreaWritebackEvent extends ApplicationEvent {

    /**
     * 登录用户编号（即 im_users.id）
     */
    private final Long userId;

    /**
     * 登录客户端 IP
     */
    private final String ip;

    /**
     * 租户编号（用于异步线程内显式设置租户上下文）
     */
    private final Long tenantId;

    public LoginAreaWritebackEvent(Object source, Long userId, String ip, Long tenantId) {
        super(source);
        this.userId = userId;
        this.ip = ip;
        this.tenantId = tenantId;
    }

}
