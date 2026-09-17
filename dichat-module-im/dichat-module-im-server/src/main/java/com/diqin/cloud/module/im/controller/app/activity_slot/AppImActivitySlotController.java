package com.diqin.cloud.module.im.controller.app.activity_slot;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.controller.app.activity_slot.vo.AppImActivitySlotRespVO;
import com.diqin.cloud.module.im.service.activity_slot.ImActivitySlotAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "用户APP - IM 运营活动位")
@RestController
@RequestMapping("/im/activity-slot")
@Validated
public class AppImActivitySlotController {

    @Resource
    private ImActivitySlotAppService activitySlotAppService;

    @GetMapping("/list")
    @Operation(summary = "获得当前生效的运营活动位列表", description = "按展示位查询当前启用且在生效时间内的活动位，按 sort 升序返回；用于客户端发现页Banner/聊天列表/朋友圈等运营位展示")
    public CommonResult<List<AppImActivitySlotRespVO>> getActivitySlots(@RequestParam("slotPosition") Integer slotPosition) {
        return success(activitySlotAppService.getActivitySlots(slotPosition));
    }

}
