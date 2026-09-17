package com.diqin.cloud.module.im.controller.app.message.vo.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 群会话清空 Request VO
 * <p>
 * 语义：当前用户视角下，删除其在 groupId 群中可见的所有消息（物理删除）；
 * 仅影响当前用户多端，给当前用户推 GROUP_CHAT_CLEAR 事件
 *
 * @author hanson
 */
@Schema(description = "管理后台 - 群会话清空 Request VO")
@Data
public class AppImGroupMessageClearChatReqVO {

    @Schema(description = "群编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "群编号不能为空")
    private Long groupId;

}
