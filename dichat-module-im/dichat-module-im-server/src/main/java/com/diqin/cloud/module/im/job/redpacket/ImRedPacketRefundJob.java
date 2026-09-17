package com.diqin.cloud.module.im.job.redpacket;

import com.diqin.cloud.framework.tenant.core.job.TenantJob;
import com.diqin.cloud.module.im.service.redpacket.ImRedPacketService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 红包超时自动退款 Job：兜底「发出后无人抢完」的红包。
 * <p>
 * 业务入口 {@link ImRedPacketService#refundExpiredPackets()} 会捞出所有
 * 仍处于 {@code PENDING} 且 {@code remainCount > 0} 且已过期的红包，
 * 在红包级分布式锁内把「未领取明细」原路退回归款人余额，并写一笔
 * {@code RED_SEND}（红包超时退款）交易流水。明细以乐观行锁保证不会重复退。
 * <p>
 * 调度方式：在 xxl-job-admin 中注册 JobHandler={@code imRedPacketRefundJob}，
 * 推荐 cron {@code 0 0/5 * * * ?}（每 5 分钟扫一次）。
 *
 * @author dichat
 */
@Component
@Slf4j
public class ImRedPacketRefundJob {

    @Resource
    private ImRedPacketService redPacketService;

    /**
     * 执行超时红包退款
     *
     * @param param 预留扩展参数（当前未使用，留空即可）
     */
    @XxlJob("imRedPacketRefundJob")
    @TenantJob
    public void execute(String param) {
        int refunded = redPacketService.refundExpiredPackets();
        log.info("[execute][红包超时自动退款 成功处理 ({}) 个]", refunded);
        XxlJobHelper.handleSuccess(String.format("红包超时自动退款 %s 个", refunded));
    }

}
