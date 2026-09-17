package com.diqin.cloud.module.im.controller.admin.usertag;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerSaveReqVO;
import com.diqin.cloud.module.im.service.usertag.ImUserTagRelationManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 用户标签关联")
@RestController
@RequestMapping("/im/manager/user-tag/relation")
@Validated
public class ImUserTagRelationManagerController {

    @Resource
    private ImUserTagRelationManagerService relationManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得标签关联分页")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:query')")
    public CommonResult<PageResult<ImUserTagRelationManagerRespVO>> getRelationPage(@Valid ImUserTagRelationManagerPageReqVO pageReqVO) {
        return success(relationManagerService.getRelationManagerPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "给用户批量打标")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:assign')")
    public CommonResult<Boolean> createRelation(@Valid @RequestBody ImUserTagRelationManagerSaveReqVO createReqVO) {
        relationManagerService.createRelation(createReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "取消用户标签")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:assign')")
    public CommonResult<Boolean> deleteRelation(@RequestParam("tagId") Long tagId,
                                                @RequestParam("userId") Long userId) {
        relationManagerService.deleteRelation(tagId, userId);
        return success(true);
    }

}
