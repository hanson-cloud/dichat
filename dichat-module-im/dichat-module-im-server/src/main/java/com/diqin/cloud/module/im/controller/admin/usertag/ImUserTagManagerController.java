package com.diqin.cloud.module.im.controller.admin.usertag;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.usertag.ImUserTagManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 用户标签")
@RestController
@RequestMapping("/im/manager/user-tag")
@Validated
public class ImUserTagManagerController {

    @Resource
    private ImUserTagManagerService tagManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得用户标签分页")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:query')")
    public CommonResult<PageResult<ImUserTagManagerRespVO>> getTagPage(@Valid ImUserTagManagerPageReqVO pageReqVO) {
        return success(tagManagerService.getTagManagerPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "创建用户标签")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:create')")
    public CommonResult<Long> createTag(@Valid @RequestBody ImUserTagManagerSaveReqVO createReqVO) {
        return success(tagManagerService.createTag(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户标签")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:update')")
    public CommonResult<Boolean> updateTag(@Valid @RequestBody ImUserTagManagerUpdateReqVO updateReqVO) {
        tagManagerService.updateTag(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户标签（级联删除关联关系）")
    @PreAuthorize("@ss.hasPermission('im:manager:user-tag:delete')")
    public CommonResult<Boolean> deleteTag(@RequestParam("id") Long id) {
        tagManagerService.deleteTag(id);
        return success(true);
    }

}
