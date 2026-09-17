package com.diqin.cloud.module.im.controller.app.rtc;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.rtc.vo.AppImCallHistoryRespVO;
import com.diqin.cloud.module.im.dal.dataobject.rtc.ImRtcCallDO;
import com.diqin.cloud.module.im.service.rtc.ImRtcCallHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 通话记录")
@RestController
@RequestMapping("/im/rtc/call-history")
@Validated
public class AppImCallHistoryController {

    @Resource
    private ImRtcCallHistoryService callHistoryService;

    @GetMapping("/list")
    @Operation(summary = "获取当前用户的通话记录")
    public CommonResult<List<AppImCallHistoryRespVO>> getCallHistory() {
        List<ImRtcCallDO> list = callHistoryService.getCallHistory(getLoginUserId());
        return success(BeanUtils.toBean(list, AppImCallHistoryRespVO.class));
    }
}
