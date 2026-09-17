package com.diqin.cloud.module.im.controller.admin.robot;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.robot.ImRobotManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 机器人")
@RestController
@RequestMapping("/im/manager/robot")
@Validated
public class ImRobotManagerController {

    @Resource
    private ImRobotManagerService robotManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得机器人分页")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:query')")
    public CommonResult<PageResult<ImRobotManagerRespVO>> getRobotPage(@Valid ImRobotManagerPageReqVO pageReqVO) {
        return success(robotManagerService.getRobotManagerPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得机器人详情")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:query')")
    public CommonResult<ImRobotManagerRespVO> getRobot(@RequestParam("id") Long id) {
        return success(robotManagerService.getRobot(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建机器人")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:create')")
    public CommonResult<Long> createRobot(@Valid @RequestBody ImRobotManagerSaveReqVO createReqVO) {
        return success(robotManagerService.createRobot(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新机器人")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:update')")
    public CommonResult<Boolean> updateRobot(@Valid @RequestBody ImRobotManagerUpdateReqVO updateReqVO) {
        robotManagerService.updateRobot(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除机器人")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:delete')")
    public CommonResult<Boolean> deleteRobot(@RequestParam("id") Long id) {
        robotManagerService.deleteRobot(id);
        return success(true);
    }

}
