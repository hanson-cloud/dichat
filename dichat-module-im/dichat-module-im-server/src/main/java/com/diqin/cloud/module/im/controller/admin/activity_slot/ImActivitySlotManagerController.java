package com.diqin.cloud.module.im.controller.admin.activity_slot;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.activity_slot.vo.ImActivitySlotManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.activity_slot.ImActivitySlotManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 运营活动位")
@RestController
@RequestMapping("/im/manager/activity-slot")
@Validated
public class ImActivitySlotManagerController {

    @Resource
    private ImActivitySlotManagerService activitySlotManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得运营活动位分页")
    @PreAuthorize("@ss.hasPermission('im:manager:activity-slot:query')")
    public CommonResult<PageResult<ImActivitySlotManagerRespVO>> getActivitySlotPage(@Valid ImActivitySlotManagerPageReqVO pageReqVO) {
        return success(activitySlotManagerService.getActivitySlotPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得运营活动位详情")
    @PreAuthorize("@ss.hasPermission('im:manager:activity-slot:query')")
    public CommonResult<ImActivitySlotManagerRespVO> getActivitySlot(@RequestParam("id") Long id) {
        return success(activitySlotManagerService.getActivitySlot(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建运营活动位")
    @PreAuthorize("@ss.hasPermission('im:manager:activity-slot:create')")
    public CommonResult<Long> createActivitySlot(@Valid @RequestBody ImActivitySlotManagerSaveReqVO createReqVO) {
        return success(activitySlotManagerService.createActivitySlot(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新运营活动位")
    @PreAuthorize("@ss.hasPermission('im:manager:activity-slot:update')")
    public CommonResult<Boolean> updateActivitySlot(@Valid @RequestBody ImActivitySlotManagerUpdateReqVO updateReqVO) {
        activitySlotManagerService.updateActivitySlot(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除运营活动位")
    @PreAuthorize("@ss.hasPermission('im:manager:activity-slot:delete')")
    public CommonResult<Boolean> deleteActivitySlot(@RequestParam("id") Long id) {
        activitySlotManagerService.deleteActivitySlot(id);
        return success(true);
    }

}
