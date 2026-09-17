package com.diqin.cloud.module.im.controller.admin.user_ban;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.admin.user_ban.vo.*;
import com.diqin.cloud.module.im.service.user_ban.ImUserBanManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 封禁台账")
@RestController
@RequestMapping("/im/manager/user-ban")
@Validated
public class ImUserBanManagerController {

    @Resource private ImUserBanManagerService userBanManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得封禁台账分页")
    @PreAuthorize("@ss.hasPermission('im:manager:user-ban:query')")
    public CommonResult<PageResult<ImUserBanManagerRespVO>> getUserBanPage(@Valid ImUserBanManagerPageReqVO pageReqVO) {
        return success(userBanManagerService.getUserBanPage(pageReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得封禁台账详情")
    @PreAuthorize("@ss.hasPermission('im:manager:user-ban:query')")
    public CommonResult<ImUserBanManagerRespVO> getUserBan(@RequestParam("id") Long id) {
        return success(userBanManagerService.getUserBan(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建封禁记录")
    @PreAuthorize("@ss.hasPermission('im:manager:user-ban:create')")
    public CommonResult<Long> createUserBan(@Valid @RequestBody ImUserBanManagerSaveReqVO createReqVO) {
        Long bannedBy = SecurityFrameworkUtils.getLoginUserId();
        return success(userBanManagerService.createUserBan(createReqVO, bannedBy));
    }

    @PutMapping("/update")
    @Operation(summary = "更新封禁记录")
    @PreAuthorize("@ss.hasPermission('im:manager:user-ban:update')")
    public CommonResult<Boolean> updateUserBan(@Valid @RequestBody ImUserBanManagerUpdateReqVO updateReqVO) {
        userBanManagerService.updateUserBan(updateReqVO);
        return success(true);
    }

    @PutMapping("/unban")
    @Operation(summary = "解封用户")
    @PreAuthorize("@ss.hasPermission('im:manager:user-ban:update')")
    public CommonResult<Boolean> unbanUserBan(@Valid @RequestBody ImUserBanManagerUnbanReqVO unbanReqVO) {
        Long unbannedBy = SecurityFrameworkUtils.getLoginUserId();
        userBanManagerService.unbanUserBan(unbanReqVO, unbannedBy);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除封禁记录")
    @PreAuthorize("@ss.hasPermission('im:manager:user-ban:delete')")
    public CommonResult<Boolean> deleteUserBan(@RequestParam("id") Long id) {
        userBanManagerService.deleteUserBan(id);
        return success(true);
    }
}
