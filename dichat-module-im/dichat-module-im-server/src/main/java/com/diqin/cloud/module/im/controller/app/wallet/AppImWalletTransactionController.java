package com.diqin.cloud.module.im.controller.app.wallet;

import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletTransactionPageReqDTO;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletTransactionRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 钱包流水（委托支付模块 {@link PayWalletApi}）
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 钱包流水")
@RestController
@RequestMapping("/im/wallet/transaction")
@Validated
public class AppImWalletTransactionController {

    @Resource
    private PayWalletApi payWalletApi;

    @GetMapping("/page")
    @Operation(summary = "我的钱包余额流水分页（委托支付模块）")
    public CommonResult<PageResult<PayWalletTransactionRespDTO>> getTransactionPage(
            @Valid PayWalletTransactionPageReqDTO reqDTO) {
        return success(payWalletApi.getWalletTransactionPage(SecurityFrameworkUtils.getLoginUserId(),
                UserTypeEnum.MEMBER.getValue(), reqDTO).getCheckedData());
    }

}
