package com.diqin.cloud.module.im.controller.app.payment;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.payment.vo.AppImPaymentQrCreateReqVO;
import com.diqin.cloud.module.im.controller.app.payment.vo.AppImPaymentQrRespVO;
import com.diqin.cloud.module.im.dal.dataobject.payment.ImPaymentQrDO;
import com.diqin.cloud.module.im.service.payment.ImPaymentQrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 收付款收款码")
@RestController
@RequestMapping("/im/payment/qr")
@Validated
public class AppImPaymentQrController {

    @Resource
    private ImPaymentQrService paymentQrService;

    @PostMapping("/create")
    @Operation(summary = "生成收款码（可选金额/备注）")
    public CommonResult<AppImPaymentQrRespVO> createReceiveQr(@Valid @RequestBody AppImPaymentQrCreateReqVO reqVO) {
        Long userId = getLoginUserId();
        ImPaymentQrDO qr = paymentQrService.createReceiveQr(userId, reqVO.getAmount(), reqVO.getRemark());
        return success(BeanUtils.toBean(qr, AppImPaymentQrRespVO.class));
    }

    @GetMapping("/{code}")
    @Operation(summary = "解析收款码（付款方扫码后调用，返回收款人信息）")
    public CommonResult<AppImPaymentQrRespVO> resolveReceiveQr(@PathVariable("code") String code) {
        ImPaymentQrDO qr = paymentQrService.getValidByCode(code);
        if (qr == null) {
            return success(null);
        }
        return success(BeanUtils.toBean(qr, AppImPaymentQrRespVO.class));
    }
}
