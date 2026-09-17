package com.diqin.cloud.module.im.controller.app.message.vo.privates;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 私聊会话清空 Request VO
 * <p>
 * 语义：当前用户视角下，删除其与 peerUserId 之间的所有消息（物理删除）；
 * 1. 不影响对方本地缓存（数据库层面是物理删除；前端按 clear 标识自行清理）
 * 2. 仅影响当前用户多端，给当前用户推 CLEAR_CHAT 事件
 *
 * @author hanson
 */
@Schema(description = "管理后台 - 私聊会话清空 Request VO")
@Data
public class AppImPrivateMessageClearChatReqVO {

    @Schema(description = "会话对方用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "对方用户编号不能为空")
    private Long peerUserId;

}
