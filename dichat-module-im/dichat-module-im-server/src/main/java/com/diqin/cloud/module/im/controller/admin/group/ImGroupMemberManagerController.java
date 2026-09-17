package com.diqin.cloud.module.im.controller.admin.group;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerUpdateReqVO;
import com.diqin.cloud.module.im.service.group.ImGroupMemberManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 群成员管理")
@RestController
@RequestMapping("/im/manager/group-member")
@Validated
public class ImGroupMemberManagerController {

    @Resource
    private ImGroupMemberManagerService groupMemberManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得群成员分页")
    @PreAuthorize("@ss.hasPermission('im:manager:group-member:query')")
    public CommonResult<PageResult<ImGroupMemberManagerRespVO>> getGroupMemberPage(
            @Valid ImGroupMemberManagerPageReqVO pageReqVO) {
        return success(groupMemberManagerService.getGroupMemberManagerPage(pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "添加群成员")
    @PreAuthorize("@ss.hasPermission('im:manager:group-member:create')")
    public CommonResult<Long> createGroupMember(@Valid @RequestBody ImGroupMemberManagerSaveReqVO reqVO) {
        return success(groupMemberManagerService.createGroupMember(reqVO, getLoginUserId()));
    }

    @PutMapping("/update-role")
    @Operation(summary = "修改群成员角色")
    @PreAuthorize("@ss.hasPermission('im:manager:group-member:update')")
    public CommonResult<Boolean> updateGroupMemberRole(@Valid @RequestBody ImGroupMemberManagerUpdateReqVO reqVO) {
        groupMemberManagerService.updateGroupMemberRole(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "移除群成员")
    @PreAuthorize("@ss.hasPermission('im:manager:group-member:delete')")
    public CommonResult<Boolean> deleteGroupMember(
            @RequestParam("groupId") @NotNull Long groupId,
            @RequestParam("userId") @NotNull Long userId) {
        groupMemberManagerService.deleteGroupMember(groupId, userId);
        return success(true);
    }

}
