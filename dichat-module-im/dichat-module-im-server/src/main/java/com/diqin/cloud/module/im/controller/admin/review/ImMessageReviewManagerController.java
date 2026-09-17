package com.diqin.cloud.module.im.controller.admin.review;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerReviewReqVO;
import com.diqin.cloud.module.im.service.review.ImMessageReviewManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 消息审核")
@RestController
@RequestMapping("/im/manager/message-review")
@Validated
public class ImMessageReviewManagerController {

    @Resource
    private ImMessageReviewManagerService messageReviewManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得消息审核分页")
    @PreAuthorize("@ss.hasPermission('im:manager:message-review:query')")
    public CommonResult<PageResult<ImMessageReviewManagerRespVO>> getMessageReviewPage(@Valid ImMessageReviewManagerPageReqVO pageReqVO) {
        return success(messageReviewManagerService.getMessageReviewPage(pageReqVO));
    }

    @PostMapping("/review")
    @Operation(summary = "审核消息（通过/驳回）")
    @PreAuthorize("@ss.hasPermission('im:manager:message-review:review')")
    public CommonResult<Boolean> reviewMessage(@Valid @RequestBody ImMessageReviewManagerReviewReqVO reviewReqVO) {
        Long reviewerId = SecurityFrameworkUtils.getLoginUserId();
        messageReviewManagerService.reviewMessage(reviewReqVO, reviewerId);
        return success(true);
    }

}
