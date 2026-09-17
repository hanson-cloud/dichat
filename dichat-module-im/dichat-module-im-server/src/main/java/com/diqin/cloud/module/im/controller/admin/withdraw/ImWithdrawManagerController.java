package com.diqin.cloud.module.im.controller.admin.withdraw;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.*;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawDO;
import com.diqin.cloud.module.im.service.withdraw.ImWithdrawConfigService;
import com.diqin.cloud.module.im.service.withdraw.ImWithdrawService;
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

@Tag(name = "管理后台 - IM 提现")
@RestController
@RequestMapping("/im/manager/withdraw")
@Validated
public class ImWithdrawManagerController {

    @Resource
    private ImWithdrawService withdrawService;
    @Resource
    private ImWithdrawConfigService withdrawConfigService;

    @GetMapping("/page")
    @Operation(summary = "获得提现分页")
    @PreAuthorize("@ss.hasPermission('im:manager:withdraw:query')")
    public CommonResult<PageResult<ImWithdrawManagerRespVO>> getWithdrawPage(ImWithdrawManagerPageReqVO reqVO) {
        PageResult<ImWithdrawDO> page = withdrawService.getWithdrawPage(reqVO);
        return success(new PageResult<>(BeanUtils.toBean(page.getList(), ImWithdrawManagerRespVO.class), page.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获得提现详情")
    @PreAuthorize("@ss.hasPermission('im:manager:withdraw:query')")
    public CommonResult<ImWithdrawManagerRespVO> getWithdraw(@RequestParam("id") @NotNull(message = "提现编号不能为空") Long id) {
        return success(BeanUtils.toBean(withdrawService.getWithdraw(id), ImWithdrawManagerRespVO.class));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核提现（通过 / 拒绝）")
    @PreAuthorize("@ss.hasPermission('im:manager:withdraw:audit')")
    public CommonResult<Boolean> auditWithdraw(@Valid @RequestBody ImWithdrawAuditReqVO reqVO) {
        withdrawService.auditWithdraw(getLoginUserId(), reqVO.getId(), reqVO.getApprove(), reqVO.getReason());
        return success(true);
    }

    @PostMapping("/cancel")
    @Operation(summary = "撤销提现（仅待审核；退回钱包余额）")
    @PreAuthorize("@ss.hasPermission('im:manager:withdraw:cancel')")
    public CommonResult<Boolean> cancelWithdraw(@Valid @RequestBody ImWithdrawCancelReqVO reqVO) {
        withdrawService.cancelWithdraw(getLoginUserId(), reqVO.getId(), reqVO.getReason());
        return success(true);
    }

    @GetMapping("/config")
    @Operation(summary = "获得提现开关配置")
    @PreAuthorize("@ss.hasPermission('im:manager:withdraw:config')")
    public CommonResult<ImWithdrawConfigRespVO> getWithdrawConfig() {
        return success(withdrawConfigService.getConfig());
    }

    @PostMapping("/config")
    @Operation(summary = "设置提现开关配置（关闭时必填通知消息）")
    @PreAuthorize("@ss.hasPermission('im:manager:withdraw:config')")
    public CommonResult<Boolean> setWithdrawConfig(@Valid @RequestBody ImWithdrawConfigSetReqVO reqVO) {
        withdrawConfigService.setConfig(getLoginUserId(), reqVO);
        return success(true);
    }

}
