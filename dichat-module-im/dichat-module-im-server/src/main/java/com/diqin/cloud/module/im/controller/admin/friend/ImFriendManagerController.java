package com.diqin.cloud.module.im.controller.admin.friend;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendManagerRespVO;
import com.diqin.cloud.module.im.service.friend.ImFriendManagerService;
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

@Tag(name = "管理后台 - IM 好友关系管理")
@RestController
@RequestMapping("/im/manager/friend")
@Validated
public class ImFriendManagerController {

    @Resource
    private ImFriendManagerService friendManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得好友关系分页")
    @PreAuthorize("@ss.hasPermission('im:manager:friend:query')")
    public CommonResult<PageResult<ImFriendManagerRespVO>> getFriendPage(@Valid ImFriendManagerPageReqVO pageReqVO) {
        return success(friendManagerService.getFriendManagerPage(pageReqVO));
    }

}
