package com.diqin.cloud.module.im.controller.app.conversation;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.app.conversation.vo.AppImConversationRespVO;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.service.conversation.ImConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * APP 端 - IM 私聊会话
 * <p>会话列表直接读 {@code im_conversation} 聚合表，避免运行时聚合 im_private_message；
 * 打开会话时清零当前用户侧未读。</p>
 *
 * @author 速构构
 */
@Tag(name = "APP 端 - IM 私聊会话")
@RestController
@RequestMapping("/im/conversation")
@Validated
public class AppImConversationController {

    @Resource
    private ImConversationService conversationService;

    @GetMapping("/list")
    @Operation(summary = "会话列表（按最近消息倒序，含未读/置顶）")
    public CommonResult<List<AppImConversationRespVO>> getConversationList() {
        return success(conversationService.getUserConversationList(getLoginUserId()));
    }

    @PostMapping("/read")
    @Operation(summary = "标记会话已读（清零当前用户侧未读）",
            description = "peerType/peerId 取自会话列表返回的 peerType/peerId（规范 id）")
    @Parameter(name = "peerType", description = "对端类型：1-用户 2-机器人 3-人工客服", required = true, example = "3")
    @Parameter(name = "peerId", description = "对端编号（规范 id）", required = true, example = "225")
    public CommonResult<Boolean> markRead(@RequestParam("peerType") Integer peerType,
                                          @RequestParam("peerId") Long peerId) {
        conversationService.markRead(ImMessageParticipantTypeEnum.USER.getType(), getLoginUserId(), peerType, peerId);
        return success(true);
    }

    private Long getLoginUserId() {
        return SecurityFrameworkUtils.getLoginUserId();
    }

}
