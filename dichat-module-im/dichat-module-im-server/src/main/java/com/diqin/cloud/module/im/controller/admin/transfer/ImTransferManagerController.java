package com.diqin.cloud.module.im.controller.admin.transfer;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.transfer.vo.ImTransferManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.transfer.vo.ImTransferManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.transfer.ImTransferDO;
import com.diqin.cloud.module.im.service.transfer.ImTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 转账")
@RestController
@RequestMapping("/im/manager/transfer")
@Validated
public class ImTransferManagerController {

    @Resource
    private ImTransferService transferService;

    @GetMapping("/page")
    @Operation(summary = "获得转账分页")
    @PreAuthorize("@ss.hasPermission('im:manager:transfer:query')")
    public CommonResult<PageResult<ImTransferManagerRespVO>> getTransferPage(ImTransferManagerPageReqVO reqVO) {
        PageResult<ImTransferDO> page = transferService.getTransferPage(reqVO);
        return success(new PageResult<>(BeanUtils.toBean(page.getList(), ImTransferManagerRespVO.class), page.getTotal()));
    }

    @GetMapping("/get")
    @Operation(summary = "获得转账详情")
    @PreAuthorize("@ss.hasPermission('im:manager:transfer:query')")
    public CommonResult<ImTransferManagerRespVO> getTransfer(@RequestParam("id") @NotNull(message = "转账编号不能为空") Long id) {
        return success(BeanUtils.toBean(transferService.getTransfer(id), ImTransferManagerRespVO.class));
    }

}
