package com.diqin.cloud.module.im.controller.app.friend;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.dal.dataobject.blacklist.ImBlacklistDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.friend.ImFriendService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 黑名单")
@RestController
@RequestMapping("/im/block")
@Validated
public class AppImBlockController {

    @Resource
    private ImFriendService friendService;
    @Resource
    private ImUserService userService;

    @GetMapping("/list")
    @Operation(summary = "获取当前用户的黑名单列表")
    public CommonResult<List<BlockRespVO>> getBlockList() {
        List<ImBlacklistDO> list = friendService.getBlockList(getLoginUserId());
        List<BlockRespVO> result = list.stream().map(b -> {
            BlockRespVO vo = new BlockRespVO();
            vo.setBlockId(b.getBlockId());
            vo.setReason(b.getReason());
            vo.setCreatedTime(b.getCreatedTime());
            ImUserDO user = userService.getUser(b.getBlockId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).toList();
        return success(result);
    }

    @Data
    @Accessors(chain = true)
    public static class BlockRespVO {
        private Long blockId;
        private String reason;
        private LocalDateTime createdTime;
        private String nickname;
        private String avatar;
    }
}
