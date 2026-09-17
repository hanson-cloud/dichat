package com.diqin.cloud.module.pay.api.wallet;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.pay.api.wallet.dto.*;
import com.diqin.cloud.module.pay.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME) 
@Tag(name = "RPC 服务 - 钱包")
public interface PayWalletApi {

    String PREFIX = ApiConstants.PREFIX + "/wallet";

    @PostMapping(PREFIX + "/add-balance")
    @Operation(summary = "添加钱包余额")
    CommonResult<Boolean> addWalletBalance(@Valid @RequestBody PayWalletAddBalanceReqDTO reqDTO);

    @GetMapping(PREFIX + "/get-or-create")
    @Operation(summary = "获取钱包信息")
    @Parameters({
            @Parameter(name = "userId", description = "用户编号", required = true, example = "1024"),
            @Parameter(name = "userType", description = "用户类型", required = true, example = "1")
    })
    CommonResult<PayWalletRespDTO> getOrCreateWallet(@RequestParam("userId") Long userId,
                                                     @RequestParam("userType") Integer userType);

    @PostMapping(PREFIX + "/recharge/create")
    @Operation(summary = "创建钱包充值（发起充值）")
    CommonResult<PayWalletRechargeCreateRespDTO> createRecharge(@Valid @RequestBody PayWalletRechargeCreateReqDTO reqDTO);

    @PostMapping(PREFIX + "/transaction/page")
    @Operation(summary = "获得钱包余额流水分页")
    CommonResult<PageResult<PayWalletTransactionRespDTO>> getWalletTransactionPage(
            @RequestParam("userId") Long userId,
            @RequestParam("userType") Integer userType,
            @Valid @RequestBody PayWalletTransactionPageReqDTO reqDTO);

    @PostMapping(PREFIX + "/recharge/page")
    @Operation(summary = "获得钱包充值记录分页（管理后台，全部用户）")
    CommonResult<PageResult<PayWalletRechargeRespDTO>> getRechargePage(@Valid @RequestBody PayWalletRechargePageReqDTO reqDTO);


}
