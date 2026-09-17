package com.diqin.cloud.module.im.controller.app.chatbg;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.chatbg.vo.AppImChatBgRespVO;
import com.diqin.cloud.module.im.dal.dataobject.chatbg.ImUserChatBgDO;
import com.diqin.cloud.module.im.service.chatbg.ImUserChatBgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - 聊天背景")
@RestController
@RequestMapping("/im/chat-bg")
@Validated
public class AppImChatBgController {

    @Resource
    private ImUserChatBgService chatBgService;

    @PostMapping("/sync")
    @Operation(summary = "同步聊天背景", description = "上传用户选择的聊天背景图片URL或颜色值，按 (userId, convKey) 维度存储；convKey 为空表示全局默认背景")
    public CommonResult<Boolean> syncBg(@RequestParam String bgValue,
                                        @RequestParam(required = false) Integer bgType,
                                        @RequestParam(required = false) String convKey) {
        Long userId = getLoginUserId();
        chatBgService.syncBg(userId, bgValue, bgType, convKey);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取聊天背景", description = "获取用户设置的聊天背景；会话未单独设置时回退到全局默认背景")
    public CommonResult<AppImChatBgRespVO> getBg(@RequestParam(required = false) String convKey) {
        Long userId = getLoginUserId();
        ImUserChatBgDO bg = chatBgService.getBg(userId, convKey);
        return success(bg == null ? null : BeanUtils.toBean(bg, AppImChatBgRespVO.class));
    }
}
