package com.diqin.cloud.module.im.controller.admin.blacklist;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerSaveReqVO;
import com.diqin.cloud.module.im.service.blacklist.ImBlacklistManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 全局黑名单")
@RestController
@RequestMapping("/im/manager/blacklist")
@Validated
public class ImBlacklistManagerController {

    @Resource
    private ImBlacklistManagerService blacklistManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得黑名单记录分页")
    @PreAuthorize("@ss.hasPermission('im:manager:blacklist:query')")
    public CommonResult<PageResult<ImBlacklistManagerRespVO>> getBlacklistPage(@Valid ImBlacklistManagerPageReqVO pageReqVO) {
        return success(blacklistManagerService.getBlacklistManagerPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "新增黑名单记录")
    @PreAuthorize("@ss.hasPermission('im:manager:blacklist:create')")
    public CommonResult<Long> createBlacklist(@Valid @RequestBody ImBlacklistManagerSaveReqVO reqVO) {
        return success(blacklistManagerService.createBlacklist(reqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "移除黑名单记录")
    @PreAuthorize("@ss.hasPermission('im:manager:blacklist:delete')")
    public CommonResult<Boolean> deleteBlacklist(@RequestParam("id") Long id) {
        blacklistManagerService.deleteBlacklist(id);
        return success(true);
    }

}
