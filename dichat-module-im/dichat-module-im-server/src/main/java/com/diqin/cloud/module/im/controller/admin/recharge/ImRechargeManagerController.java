package com.diqin.cloud.module.im.controller.admin.recharge;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.recharge.vo.ImRechargeManagerPageReqVO;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRechargePageReqDTO;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRechargeRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - IM 充值")
@RestController
@RequestMapping("/im/manager/recharge")
@Validated
public class ImRechargeManagerController {

    @Resource
    private PayWalletApi payWalletApi;

    @GetMapping("/page")
    @Operation(summary = "获得充值记录分页（管理后台）")
    @PreAuthorize("@ss.hasPermission('im:manager:recharge:query')")
    public CommonResult<PageResult<PayWalletRechargeRespDTO>> getRechargePage(ImRechargeManagerPageReqVO reqVO) {
        PayWalletRechargePageReqDTO reqDTO = BeanUtils.toBean(reqVO, PayWalletRechargePageReqDTO.class);
        return payWalletApi.getRechargePage(reqDTO);
    }

}
