package com.diqin.cloud.module.im.controller.app.wallet;

import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.wallet.vo.AppImWalletRechargeCreateReqVO;
import com.diqin.cloud.module.im.controller.app.wallet.vo.AppImWalletRechargeCreateRespVO;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRechargeCreateReqDTO;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRechargeCreateRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - IM 钱包充值")
@RestController
@RequestMapping("/im/wallet/recharge")
@Validated
public class AppImWalletRechargeController {

    @Resource
    private PayWalletApi payWalletApi;

    @PostMapping("/create")
    @Operation(summary = "发起钱包充值（委托支付模块创建充值单）")
    public CommonResult<AppImWalletRechargeCreateRespVO> create(@Valid @RequestBody AppImWalletRechargeCreateReqVO reqVO) {
        PayWalletRechargeCreateRespDTO resp = payWalletApi.createRecharge(new PayWalletRechargeCreateReqDTO()
                .setUserId(getLoginUserId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setPayPrice(reqVO.getPayPrice())
                .setPackageId(reqVO.getPackageId())).getCheckedData();
        return success(BeanUtils.toBean(resp, AppImWalletRechargeCreateRespVO.class));
    }

}
