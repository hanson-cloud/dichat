package com.diqin.cloud.module.im.controller.admin.bank;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankPageReqVO;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankRespVO;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankSaveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;
import com.diqin.cloud.module.im.service.bank.ImBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - IM 支持的银行
 *
 * @author dichat
 */
@Tag(name = "管理后台 - IM 支持的银行")
@RestController
@RequestMapping("/im/manager/bank")
@Validated
public class ImBankManagerController {

    @Resource
    private ImBankService bankService;

    @GetMapping("/page")
    @Operation(summary = "获得银行分页")
    @PreAuthorize("@ss.hasPermission('im:manager:bank:query')")
    public CommonResult<PageResult<ImBankRespVO>> getBankPage(ImBankPageReqVO reqVO) {
        PageResult<ImBankDO> page = bankService.getBankPage(reqVO);
        return success(new PageResult<>(BeanUtils.toBean(page.getList(), ImBankRespVO.class), page.getTotal()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建银行")
    @PreAuthorize("@ss.hasPermission('im:manager:bank:create')")
    public CommonResult<Long> createBank(@Valid @RequestBody ImBankSaveReqVO reqVO) {
        return success(bankService.createBank(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新银行")
    @PreAuthorize("@ss.hasPermission('im:manager:bank:update')")
    public CommonResult<Boolean> updateBank(@Valid @RequestBody ImBankSaveReqVO reqVO) {
        bankService.updateBank(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除银行")
    @PreAuthorize("@ss.hasPermission('im:manager:bank:delete')")
    public CommonResult<Boolean> deleteBank(@RequestParam("id") @NotNull(message = "银行编号不能为空") Long id) {
        bankService.deleteBank(id);
        return success(true);
    }

}
