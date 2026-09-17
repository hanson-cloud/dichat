package com.diqin.cloud.module.im.controller.app.message;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.message.vo.privates.*;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 私聊消息")
@RestController
@RequestMapping("/im/message/private")
@Validated
public class AppImPrivateMessageController {

    @Resource
    private ImPrivateMessageService privateMessageService;

    @PostMapping("/send")
    @Operation(summary = "发送私聊消息")
    public CommonResult<AppImPrivateMessageRespVO> sendPrivateMessage(
            @Valid @RequestBody AppImPrivateMessageSendReqVO reqVO) {
        ImPrivateMessageDO message = privateMessageService.sendPrivateMessage(getLoginUserId(), reqVO);
        return success(BeanUtils.toBean(message, AppImPrivateMessageRespVO.class));
    }

    @GetMapping("/pull")
    @Operation(summary = "拉取私聊消息（增量）")
    @Parameter(name = "minId", description = "最小消息 id", required = true, example = "0")
    @Parameter(name = "size", description = "拉取数量", required = true, example = "100")
    public CommonResult<List<AppImPrivateMessageRespVO>> pullPrivateMessageList(
            @RequestParam("minId") Long minId,
            @RequestParam("size") @Min(value = 1, message = "拉取数量最小值为 1") @Max(value = 200,message = "拉取数量最大值为 200") Integer size) {
        List<ImPrivateMessageDO> messages = privateMessageService.pullPrivateMessageList(getLoginUserId(), minId, size);
        return success(BeanUtils.toBean(messages, AppImPrivateMessageRespVO.class));
    }

    @PutMapping("/read")
    @Operation(summary = "标记私聊消息已读")
    @Parameter(name = "receiverId", description = "接收方用户编号（对方）", required = true, example = "2")
    @Parameter(name = "messageId", description = "已读位置（含），通常是会话内最大消息编号", required = true, example = "100")
    public CommonResult<Boolean> readPrivateMessages(@RequestParam("receiverId") Long receiverId,
                                                     @RequestParam("messageId") Long messageId) {
        privateMessageService.readPrivateMessages(getLoginUserId(), receiverId, messageId);
        return success(true);
    }

    @GetMapping("/max-read-message-id")
    @Operation(summary = "查询对方已读到我发的最大消息 id",
            description = "用于多端 / 离线场景下的已读位置补齐：进入会话或断线重连后调用，结果用于翻转本地自发消息状态")
    @Parameter(name = "peerId", description = "对方用户编号", required = true, example = "2")
    public CommonResult<Long> getMaxReadMessageId(@RequestParam("peerId") Long peerId) {
        return success(privateMessageService.getMaxReadMessageId(getLoginUserId(), peerId));
    }

    @DeleteMapping("/recall")
    @Operation(summary = "撤回私聊消息")
    @Parameter(name = "id", description = "消息编号", required = true, example = "1")
    public CommonResult<AppImPrivateMessageRespVO> recallPrivateMessage(@RequestParam("id") Long id) {
        ImPrivateMessageDO message = privateMessageService.recallPrivateMessage(getLoginUserId(), id);
        return success(BeanUtils.toBean(message, AppImPrivateMessageRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "查询私聊历史消息")
    public CommonResult<List<AppImPrivateMessageRespVO>> getPrivateMessageList(@Valid AppImPrivateMessageListReqVO reqVO) {
        List<ImPrivateMessageDO> messages = privateMessageService.getPrivateMessageList(getLoginUserId(), reqVO);
        return success(BeanUtils.toBean(messages, AppImPrivateMessageRespVO.class));
    }

    // ==================== 物理删除 / 清空会话 ====================

    @DeleteMapping("/delete")
    @Operation(summary = "物理删除若干条自己发送的私聊消息",
            description = "仅允许删除发送人为当前用户的消息；删除后给当前用户多端推 PRIVATE_MESSAGE_DELETE 事件，对方本地缓存由 DB 物理删除后下次拉取时自然缺失")
    public CommonResult<Integer> deletePrivateMessages(@Valid @RequestBody AppImPrivateMessageDeleteReqVO reqVO) {
        return success(privateMessageService.deletePrivateMessages(getLoginUserId(),
                reqVO.getPeerUserId(), reqVO.getMessageIds()));
    }

    @DeleteMapping("/clear-chat")
    @Operation(summary = "清空当前用户与指定好友的私聊会话",
            description = "物理删除 userId ↔ peerUserId 之间所有（双向）消息；删除后给当前用户多端推 PRIVATE_CHAT_CLEAR 事件，对方本地缓存由 DB 物理删除后下次拉取时自然缺失")
    public CommonResult<Integer> clearPrivateChat(@Valid @RequestBody AppImPrivateMessageClearChatReqVO reqVO) {
        return success(privateMessageService.clearPrivateChat(getLoginUserId(), reqVO.getPeerUserId()));
    }

}
