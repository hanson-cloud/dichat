package com.diqin.cloud.module.im.controller.app.withdraw;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.withdraw.vo.AppImWithdrawConfigRespVO;
import com.diqin.cloud.module.im.controller.app.withdraw.vo.AppImWithdrawCreateReqVO;
import com.diqin.cloud.module.im.controller.app.withdraw.vo.AppImWithdrawRespVO;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawDO;
import com.diqin.cloud.module.im.service.withdraw.ImWithdrawConfigService;
import com.diqin.cloud.module.im.service.withdraw.ImWithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - IM 提现")
@RestController
@RequestMapping("/im/withdraw")
@Validated
public class AppImWithdrawController {

    @Resource
    private ImWithdrawService withdrawService;
    @Resource
    private ImWithdrawConfigService withdrawConfigService;

    @PostMapping("/create")
    @Operation(summary = "发起提现（冻结钱包余额，待审核）")
    public CommonResult<AppImWithdrawRespVO> create(@Valid @RequestBody AppImWithdrawCreateReqVO reqVO) {
        return success(BeanUtils.toBean(withdrawService.createWithdraw(getLoginUserId(), reqVO), AppImWithdrawRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "查询提现详情")
    public CommonResult<AppImWithdrawRespVO> get(@RequestParam("id") @NotNull(message = "提现编号不能为空") Long id) {
        return success(BeanUtils.toBean(withdrawService.getWithdraw(id), AppImWithdrawRespVO.class));
    }

    @GetMapping("/page-my")
    @Operation(summary = "我的提现分页")
    public CommonResult<PageResult<AppImWithdrawRespVO>> pageMy(PageParam pageReqVO) {
        PageResult<ImWithdrawDO> page = withdrawService.getWithdrawPageMy(getLoginUserId(), pageReqVO);
        return success(new PageResult<>(BeanUtils.toBean(page.getList(), AppImWithdrawRespVO.class), page.getTotal()));
    }

    @GetMapping("/config")
    @Operation(summary = "获得提现开关配置（用户端提现页据此展示横幅/禁用按钮）")
    public CommonResult<AppImWithdrawConfigRespVO> config() {
        return success(BeanUtils.toBean(withdrawConfigService.getConfig(), AppImWithdrawConfigRespVO.class));
    }

}
