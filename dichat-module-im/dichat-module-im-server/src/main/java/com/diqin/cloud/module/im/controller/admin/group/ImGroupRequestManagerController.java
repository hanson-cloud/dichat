package com.diqin.cloud.module.im.controller.admin.group;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupRequestManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupRequestManagerRespVO;
import com.diqin.cloud.module.im.service.group.ImGroupRequestManagerService;
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

@Tag(name = "管理后台 - IM 加群申请管理")
@RestController
@RequestMapping("/im/manager/group-request")
@Validated
public class ImGroupRequestManagerController {

    @Resource
    private ImGroupRequestManagerService groupRequestManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得加群申请分页")
    @PreAuthorize("@ss.hasPermission('im:manager:group-request:query')")
    public CommonResult<PageResult<ImGroupRequestManagerRespVO>> getGroupRequestPage(
            @Valid ImGroupRequestManagerPageReqVO pageReqVO) {
        return success(groupRequestManagerService.getGroupRequestManagerPage(pageReqVO));
    }

}
