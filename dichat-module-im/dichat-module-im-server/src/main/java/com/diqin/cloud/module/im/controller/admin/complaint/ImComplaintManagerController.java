package com.diqin.cloud.module.im.controller.admin.complaint;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerHandleReqVO;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerRespVO;
import com.diqin.cloud.module.im.service.complaint.ImComplaintManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 举报管理")
@RestController
@RequestMapping("/im/manager/complaint")
@Validated
public class ImComplaintManagerController {

    @Resource
    private ImComplaintManagerService complaintManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得举报记录分页")
    @PreAuthorize("@ss.hasPermission('im:manager:complaint:query')")
    public CommonResult<PageResult<ImComplaintManagerRespVO>> getComplaintPage(@Valid ImComplaintManagerPageReqVO pageReqVO) {
        return success(complaintManagerService.getComplaintManagerPage(pageReqVO));
    }

    @PutMapping("/handle")
    @Operation(summary = "审核处置举报记录")
    @PreAuthorize("@ss.hasPermission('im:manager:complaint:handle')")
    public CommonResult<Boolean> handleComplaint(@Valid @RequestBody ImComplaintManagerHandleReqVO reqVO) {
        complaintManagerService.handleComplaint(getLoginUserId(), reqVO);
        return success(true);
    }

}
