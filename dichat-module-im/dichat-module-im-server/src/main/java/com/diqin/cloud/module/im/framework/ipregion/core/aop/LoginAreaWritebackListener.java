package com.diqin.cloud.module.im.framework.ipregion.core.aop;

import com.diqin.cloud.framework.tenant.core.util.TenantUtils;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.user.ImUserMapper;
import com.diqin.cloud.module.system.api.area.AreaApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 登录区域回写的事件监听者。
 * <p>
 * 通过 {@code @TransactionalEventListener(phase = AFTER_COMMIT)} 保证「仅在登录事务成功提交后才执行」，
 * 异步任务因此能看到已提交的数据，且不会因事务回滚而误触发；
 * 通过 {@code @Async("loginAreaExecutor")} 让真正耗时的 IP 归属地 RPC 跑在独立线程池，
 * 登录主线程 publish 完事件即返回，零等待。
 * </p>
 * <p>
 * 真正的解析由远程 {@link AreaApi#getByIp} 完成（system 模块基于 ip-api.com + 静态区域树，
 * 不使用 ip2region），解析结果（area_id）回写到 im_users.area_id。
 * </p>
 *
 * @author hanson
 */
@Slf4j
@Component
public class LoginAreaWritebackListener {

    @Resource
    private AreaApi areaApi;

    @Resource
    private ImUserMapper userMapper;

    @Async("loginAreaExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLoginSuccess(LoginAreaWritebackEvent event) {
        try {
            // 租户上下文缺失（理论上登录都有租户）时无法安全更新 im_users，跳过本次回写
            if (event.getTenantId() == null) {
                log.warn("[login-area] 租户上下文缺失，跳过区域回写 userId={}", event.getUserId());
                return;
            }
            // 显式设置租户上下文，避免异步线程内租户丢失导致更新命中不到数据
            TenantUtils.execute(event.getTenantId(), () -> {
                // 1. 查询 IP 归属地对应的区域编号（远程 AreaApi，不使用 ip2region）
                Long areaId = areaApi.getByIp(event.getIp()).getData();
                // 2. 查询不到区域时不做任何更新，保留历史值
                if (areaId == null) {
                    return;
                }
                // 3. 回写 im_users.area_id（仅更新 id + area_id 两列）
                ImUserDO update = new ImUserDO();
                update.setId(event.getUserId());
                update.setAreaId(areaId);
                userMapper.updateById(update);
            });
        } catch (Exception e) {
            // 旁路逻辑，失败仅告警，不影响登录主流程
            log.warn("[login-area] 回写区域 ID 失败 userId={} ip={}", event.getUserId(), event.getIp(), e);
        }
    }

}
