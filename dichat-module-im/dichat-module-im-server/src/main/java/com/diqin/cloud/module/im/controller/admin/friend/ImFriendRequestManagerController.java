package com.diqin.cloud.module.im.controller.admin.friend;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendRequestManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendRequestManagerRespVO;
import com.diqin.cloud.module.im.service.friend.ImFriendRequestManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 好友申请管理")
@RestController
@RequestMapping("/im/manager/friend-request")
@Validated
public class ImFriendRequestManagerController {

    @Resource
    private ImFriendRequestManagerService friendRequestManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得好友申请分页")
    @PreAuthorize("@ss.hasPermission('im:manager:friend-request:query')")
    public CommonResult<PageResult<ImFriendRequestManagerRespVO>> getFriendRequestPage(
            @Valid ImFriendRequestManagerPageReqVO pageReqVO) {
        return success(friendRequestManagerService.getFriendRequestManagerPage(pageReqVO));
    }

}
