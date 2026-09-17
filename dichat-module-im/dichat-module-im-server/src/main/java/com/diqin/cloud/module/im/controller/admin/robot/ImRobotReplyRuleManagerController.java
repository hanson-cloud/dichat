package com.diqin.cloud.module.im.controller.admin.robot;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.robot.ImRobotReplyRuleManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 机器人自动回复规则")
@RestController
@RequestMapping("/im/manager/robot/reply-rule")
@Validated
public class ImRobotReplyRuleManagerController {

    @Resource
    private ImRobotReplyRuleManagerService replyRuleManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得机器人自动回复规则分页")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:rule:query')")
    public CommonResult<PageResult<ImRobotReplyRuleManagerRespVO>> getReplyRulePage(@Valid ImRobotReplyRuleManagerPageReqVO pageReqVO) {
        return success(replyRuleManagerService.getReplyRuleManagerPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "创建机器人自动回复规则")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:rule:create')")
    public CommonResult<Long> createReplyRule(@Valid @RequestBody ImRobotReplyRuleManagerSaveReqVO createReqVO) {
        return success(replyRuleManagerService.createReplyRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新机器人自动回复规则")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:rule:update')")
    public CommonResult<Boolean> updateReplyRule(@Valid @RequestBody ImRobotReplyRuleManagerUpdateReqVO updateReqVO) {
        replyRuleManagerService.updateReplyRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除机器人自动回复规则")
    @PreAuthorize("@ss.hasPermission('im:manager:robot:rule:delete')")
    public CommonResult<Boolean> deleteReplyRule(@RequestParam("id") Long id) {
        replyRuleManagerService.deleteReplyRule(id);
        return success(true);
    }

}
