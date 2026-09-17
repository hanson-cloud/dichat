package com.diqin.cloud.module.im.controller.admin.payment;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerSaveReqVO;
import com.diqin.cloud.module.im.service.payment.ImPaymentQrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 收付款收款码")
@RestController
@RequestMapping("/im/manager/payment/qr")
@Validated
public class ImPaymentQrManagerController {

    @Resource
    private ImPaymentQrService paymentQrService;

    @GetMapping("/page")
    @Operation(summary = "获得收款码分页（可按用户/状态/码查询）")
    @PreAuthorize("@ss.hasPermission('im:manager:payment:qr:query')")
    public CommonResult<PageResult<ImPaymentQrManagerRespVO>> getPage(@Valid ImPaymentQrManagerPageReqVO pageReqVO) {
        return success(paymentQrService.getManagerPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "代用户创建收款码")
    @PreAuthorize("@ss.hasPermission('im:manager:payment:qr:create')")
    public CommonResult<Long> create(@Valid @RequestBody ImPaymentQrManagerSaveReqVO createReqVO) {
        return success(paymentQrService.createReceiveQr(
                createReqVO.getUserId(), createReqVO.getAmount(), createReqVO.getRemark()).getId());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除收款码")
    @PreAuthorize("@ss.hasPermission('im:manager:payment:qr:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        paymentQrService.deleteManager(id);
        return success(true);
    }
}
