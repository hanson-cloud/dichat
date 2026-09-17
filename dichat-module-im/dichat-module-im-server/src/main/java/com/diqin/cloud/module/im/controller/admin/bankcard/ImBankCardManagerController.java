package com.diqin.cloud.module.im.controller.admin.bankcard;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.bankcard.vo.ImBankCardAuditReqVO;
import com.diqin.cloud.module.im.controller.admin.bankcard.vo.ImBankCardManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.bankcard.vo.ImBankCardManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.bankcard.ImBankCardDO;
import com.diqin.cloud.module.im.service.bankcard.ImBankCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 银行卡")
@RestController
@RequestMapping("/im/manager/bank-card")
@Validated
public class ImBankCardManagerController {

    @Resource
    private ImBankCardService bankCardService;

    @GetMapping("/page")
    @Operation(summary = "获得银行卡分页")
    @PreAuthorize("@ss.hasPermission('im:manager:bank-card:query')")
    public CommonResult<PageResult<ImBankCardManagerRespVO>> getBankCardPage(ImBankCardManagerPageReqVO reqVO) {
        PageResult<ImBankCardDO> page = bankCardService.getBankCardPage(reqVO);
        return success(new PageResult<>(BeanUtils.toBean(page.getList(), ImBankCardManagerRespVO.class), page.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获得银行卡详情")
    @PreAuthorize("@ss.hasPermission('im:manager:bank-card:query')")
    public CommonResult<ImBankCardManagerRespVO> getBankCard(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(bankCardService.getBankCard(id), ImBankCardManagerRespVO.class));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核银行卡（通过 / 拒绝）")
    @PreAuthorize("@ss.hasPermission('im:manager:bank-card:audit')")
    public CommonResult<Boolean> auditBankCard(@Valid @RequestBody ImBankCardAuditReqVO reqVO) {
        bankCardService.auditBankCard(getLoginUserId(), reqVO.getId(), reqVO.getApprove(), reqVO.getReason());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "解绑银行卡")
    @PreAuthorize("@ss.hasPermission('im:manager:bank-card:delete')")
    public CommonResult<Boolean> deleteBankCard(@RequestParam("id") @NotNull(message = "银行卡编号不能为空") Long id) {
        bankCardService.deleteBankCardAdmin(id);
        return success(true);
    }

}
