package com.diqin.cloud.module.im.controller.app.message;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.message.vo.group.*;
import com.diqin.cloud.module.im.dal.dataobject.message.ImGroupMessageDO;
import com.diqin.cloud.module.im.service.message.ImGroupMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 群聊消息")
@RestController
@RequestMapping("/im/message/group")
@Validated
public class AppImGroupMessageController {

    @Resource
    private ImGroupMessageService groupMessageService;

    @PostMapping("/send")
    @Operation(summary = "发送群聊消息")
    public CommonResult<AppImGroupMessageRespVO> sendGroupMessage(@Valid @RequestBody AppImGroupMessageSendReqVO reqVO) {
        ImGroupMessageDO message = groupMessageService.sendGroupMessage(getLoginUserId(), reqVO);
        return success(BeanUtils.toBean(message, AppImGroupMessageRespVO.class));
    }

    @GetMapping("/pull")
    @Operation(summary = "拉取群聊消息（增量）")
    @Parameter(name = "minId", description = "最小消息 id", required = true, example = "0")
    @Parameter(name = "size", description = "拉取数量", required = true, example = "100")
    public CommonResult<List<AppImGroupMessageRespVO>> pullGroupMessageList(
            @RequestParam("minId") Long minId,
            @RequestParam("size") @Min(value = 1, message = "拉取数量最小值为 1") Integer size) {
        List<ImGroupMessageDO> messages = groupMessageService.pullGroupMessageList(getLoginUserId(), minId, size);
        return success(BeanUtils.toBean(messages, AppImGroupMessageRespVO.class));
    }

    @PutMapping("/read")
    @Operation(summary = "标记群聊消息已读")
    @Parameter(name = "groupId", description = "群编号", required = true, example = "1")
    @Parameter(name = "messageId", description = "已读到的消息编号", required = true, example = "100")
    public CommonResult<Boolean> readGroupMessages(@RequestParam("groupId") Long groupId,
                                                   @RequestParam("messageId") Long messageId) {
        groupMessageService.readGroupMessages(getLoginUserId(), groupId, messageId);
        return success(true);
    }

    @DeleteMapping("/recall")
    @Operation(summary = "撤回群聊消息")
    @Parameter(name = "id", description = "消息编号", required = true, example = "1")
    public CommonResult<AppImGroupMessageRespVO> recallGroupMessage(@RequestParam("id") Long id) {
        ImGroupMessageDO message = groupMessageService.recallGroupMessage(getLoginUserId(), id);
        return success(BeanUtils.toBean(message, AppImGroupMessageRespVO.class));
    }

    @GetMapping("/get-read-user-ids")
    @Operation(summary = "获取群消息已读用户列表")
    @Parameter(name = "groupId", description = "群编号", required = true, example = "1")
    @Parameter(name = "messageId", description = "消息编号", required = true, example = "1")
    public CommonResult<List<Long>> getGroupReadUserIds(@RequestParam("groupId") Long groupId,
                                                        @RequestParam("messageId") Long messageId) {
        return success(groupMessageService.getGroupReadUserIds(getLoginUserId(), groupId, messageId));
    }

    @GetMapping("/list")
    @Operation(summary = "查询群聊历史消息")
    public CommonResult<List<AppImGroupMessageRespVO>> getGroupMessageList(@Valid AppImGroupMessageListReqVO reqVO) {
        List<ImGroupMessageDO> messages = groupMessageService.getGroupMessageList(getLoginUserId(), reqVO);
        return success(BeanUtils.toBean(messages, AppImGroupMessageRespVO.class));
    }

    // ==================== 物理删除 / 清空会话 ====================

    @DeleteMapping("/delete")
    @Operation(summary = "物理删除若干条群消息",
            description = "群主 / 管理员可删除他人消息；普通成员仅能删自己发送的消息；删除后给当前用户多端推 GROUP_MESSAGE_DELETE 事件，其他成员本地缓存由 DB 物理删除后下次拉取时自然缺失")
    public CommonResult<Integer> deleteGroupMessages(@Valid @RequestBody AppImGroupMessageDeleteReqVO reqVO) {
        return success(groupMessageService.deleteGroupMessages(getLoginUserId(),
                reqVO.getGroupId(), reqVO.getMessageIds()));
    }

    @DeleteMapping("/clear-chat")
    @Operation(summary = "清空当前用户在某群中的会话",
            description = "物理删除 groupId 群中所有消息；删除后给当前用户多端推 GROUP_CHAT_CLEAR 事件，其他成员本地缓存由 DB 物理删除后下次拉取时自然缺失")
    public CommonResult<Integer> clearGroupChat(@Valid @RequestBody AppImGroupMessageClearChatReqVO reqVO) {
        return success(groupMessageService.clearGroupChat(getLoginUserId(), reqVO.getGroupId()));
    }

}
