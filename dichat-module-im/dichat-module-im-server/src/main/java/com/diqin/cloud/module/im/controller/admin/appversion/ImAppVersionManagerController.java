package com.diqin.cloud.module.im.controller.admin.appversion;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.appversion.vo.ImAppVersionManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.appversion.ImAppVersionManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 应用版本")
@RestController
@RequestMapping("/im/manager/app-version")
@Validated
public class ImAppVersionManagerController {

    @Resource
    private ImAppVersionManagerService appVersionManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得应用版本分页")
    @PreAuthorize("@ss.hasPermission('im:manager:app-version:query')")
    public CommonResult<PageResult<ImAppVersionManagerRespVO>> getAppVersionPage(@Valid ImAppVersionManagerPageReqVO pageReqVO) {
        return success(appVersionManagerService.getAppVersionManagerPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用版本详情")
    @PreAuthorize("@ss.hasPermission('im:manager:app-version:query')")
    public CommonResult<ImAppVersionManagerRespVO> getAppVersion(@RequestParam("id") Long id) {
        return success(appVersionManagerService.getAppVersion(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用版本")
    @PreAuthorize("@ss.hasPermission('im:manager:app-version:create')")
    public CommonResult<Long> createAppVersion(@Valid @RequestBody ImAppVersionManagerSaveReqVO createReqVO) {
        return success(appVersionManagerService.createAppVersion(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用版本")
    @PreAuthorize("@ss.hasPermission('im:manager:app-version:update')")
    public CommonResult<Boolean> updateAppVersion(@Valid @RequestBody ImAppVersionManagerUpdateReqVO updateReqVO) {
        appVersionManagerService.updateAppVersion(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用版本")
    @PreAuthorize("@ss.hasPermission('im:manager:app-version:delete')")
    public CommonResult<Boolean> deleteAppVersion(@RequestParam("id") Long id) {
        appVersionManagerService.deleteAppVersion(id);
        return success(true);
    }

}
