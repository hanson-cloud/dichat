package com.diqin.cloud.module.im.controller.app.transfer;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.dto.transfer.ImTransferReqDTO;
import com.diqin.cloud.module.im.dto.transfer.ImTransferRespDTO;
import com.diqin.cloud.module.im.service.transfer.ImTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 转账
 * <p>
 * 余额变动委托 {@link com.diqin.cloud.module.pay.api.wallet.PayWalletApi}。
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 转账")
@RestController
@RequestMapping("/im/transfer")
@Validated
public class AppImTransferController {

    @Resource
    private ImTransferService transferService;

    @PostMapping("/create")
    @Operation(summary = "发起转账（资金实时扣减，订单置「待领取」）")
    public CommonResult<ImTransferRespDTO> createTransfer(@Valid @RequestBody ImTransferReqDTO reqDTO) {
        return success(BeanUtils.toBean(transferService.createTransfer(SecurityFrameworkUtils.getLoginUserId(), reqDTO),
                ImTransferRespDTO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "转账订单详情（仅发起方 / 收款方可查）")
    public CommonResult<ImTransferRespDTO> getTransferDetail(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(transferService.getTransfer(id), ImTransferRespDTO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "我的转账记录分页")
    public CommonResult<PageResult<ImTransferRespDTO>> getTransferPage(PageParam pageReqVO) {
        return success(BeanUtils.toBean(transferService.getTransferPageMy(SecurityFrameworkUtils.getLoginUserId(),
                pageReqVO), ImTransferRespDTO.class));
    }

}
