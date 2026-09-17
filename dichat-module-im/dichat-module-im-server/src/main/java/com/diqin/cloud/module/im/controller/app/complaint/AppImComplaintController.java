package com.diqin.cloud.module.im.controller.app.complaint;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.app.complaint.vo.AppImComplaintSubmitReqVO;
import com.diqin.cloud.module.im.service.complaint.ImComplaintManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 意见反馈
 *
 * @author dichat
 */
@Tag(name = "用户 APP - 意见反馈")
@RestController
@RequestMapping("/im/complaint")
@Validated
public class AppImComplaintController {

    @Resource
    private ImComplaintManagerService complaintService;

    @PostMapping("/submit")
    @Operation(summary = "提交意见反馈/举报", description = "C 端提交，写入 im_complaint，status=0 待处理")
    public CommonResult<Boolean> submit(@Valid @RequestBody AppImComplaintSubmitReqVO reqVO) {
        complaintService.submitFeedback(SecurityFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

}
