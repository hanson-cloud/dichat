package com.diqin.cloud.module.im.controller.admin.customer_service;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 客服")
@RestController
@RequestMapping("/im/manager/customer-service")
@Validated
public class ImCustomerServiceManagerController {

    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得客服分页")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:query')")
    public CommonResult<PageResult<ImCustomerServiceManagerRespVO>> getCustomerServicePage(@Valid ImCustomerServiceManagerPageReqVO pageReqVO) {
        return success(customerServiceManagerService.getCustomerServiceManagerPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得客服详情")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:query')")
    public CommonResult<ImCustomerServiceManagerRespVO> getCustomerService(@RequestParam("id") Long id) {
        return success(customerServiceManagerService.getCustomerService(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建客服")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:create')")
    public CommonResult<Long> createCustomerService(@Valid @RequestBody ImCustomerServiceManagerSaveReqVO createReqVO) {
        return success(customerServiceManagerService.createCustomerService(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客服")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:update')")
    public CommonResult<Boolean> updateCustomerService(@Valid @RequestBody ImCustomerServiceManagerUpdateReqVO updateReqVO) {
        customerServiceManagerService.updateCustomerService(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客服")
    @PreAuthorize("@ss.hasPermission('im:manager:customer-service:delete')")
    public CommonResult<Boolean> deleteCustomerService(@RequestParam("id") Long id) {
        customerServiceManagerService.deleteCustomerService(id);
        return success(true);
    }

}
