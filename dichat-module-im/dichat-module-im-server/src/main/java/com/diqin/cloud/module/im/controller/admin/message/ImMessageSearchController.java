package com.diqin.cloud.module.im.controller.admin.message;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchReqVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchRespVO;
import com.diqin.cloud.module.im.service.message.ImMessageSearchService;
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

@Tag(name = "管理后台 - IM 消息搜索")
@RestController
@RequestMapping("/im/manager/message/search")
@Validated
public class ImMessageSearchController {

    @Resource
    private ImMessageSearchService messageSearchService;

    @GetMapping("/page")
    @Operation(summary = "全局搜索消息（跨私聊+群聊）")
    @PreAuthorize("@ss.hasPermission('im:manager:message:search')")
    public CommonResult<PageResult<ImMessageSearchRespVO>> searchMessages(@Valid ImMessageSearchReqVO reqVO) {
        return success(messageSearchService.searchMessages(reqVO));
    }

}
