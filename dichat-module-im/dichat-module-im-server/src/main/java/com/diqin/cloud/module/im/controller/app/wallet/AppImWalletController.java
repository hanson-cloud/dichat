package com.diqin.cloud.module.im.controller.app.wallet;

import cn.hutool.core.util.IdUtil;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRespDTO;
import com.diqin.cloud.module.pay.enums.wallet.PayWalletBizTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 钱包（余额委托支付模块 {@link PayWalletApi}）
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 钱包")
@RestController
@RequestMapping("/im/wallet")
@Validated
public class AppImWalletController {

    @Resource
    private PayWalletApi payWalletApi;

    @GetMapping("/balance")
    @Operation(summary = "获取我的钱包余额（委托支付模块）")
    public CommonResult<PayWalletRespDTO> getBalance() {
        return success(payWalletApi.getOrCreateWallet(SecurityFrameworkUtils.getLoginUserId(),
                UserTypeEnum.MEMBER.getValue()).getCheckedData());
    }

    @PostMapping("/recharge")
    @Operation(summary = "模拟充值（直接到账，演示/测试用）")
    public CommonResult<PayWalletRespDTO> mockRecharge(@Valid @RequestBody MockRechargeReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        // 模拟充值：直接调用钱包 API 增加余额，不走支付订单流程
        payWalletApi.addWalletBalance(new PayWalletAddBalanceReqDTO()
                .setUserId(userId)
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setBizType(PayWalletBizTypeEnum.RECHARGE.getType())
                .setBizId("mock_recharge_" + IdUtil.fastSimpleUUID())
                .setPrice(reqVO.getAmount()));
        // 返回更新后的钱包余额
        return success(payWalletApi.getOrCreateWallet(userId,
                UserTypeEnum.MEMBER.getValue()).getCheckedData());
    }

    /**
     * 模拟充值请求 VO
     */
    @Data
    public static class MockRechargeReqVO {
        @NotNull(message = "充值金额不能为空")
        @Min(value = 1, message = "充值金额必须大于零")
        private Integer amount;
    }

}
