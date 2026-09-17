package com.diqin.cloud.module.im.controller.app.message.vo.privates;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 私聊消息物理删除 Request VO
 * <p>
 * 语义：当前用户视角下，删除其与 peerUserId 之间的若干条消息；
 * 1. 仅当消息发送人是当前用户时才能删除（避免越权删除对方消息）
 * 2. 物理删除，删除后不可恢复；如需"撤回"语义请走 /recall 接口
 * 3. 消息删除后会给当前用户多端推 DELETE 事件，对方多端不动（与微信行为一致）
 *
 * @author hanson
 */
@Schema(description = "管理后台 - 私聊消息物理删除 Request VO")
@Data
public class AppImPrivateMessageDeleteReqVO {

    @Schema(description = "会话对方用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "对方用户编号不能为空")
    private Long peerUserId;

    @Schema(description = "待删除的消息编号列表；必须全部由当前用户发送",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "消息编号列表不能为空")
    private List<Long> messageIds;

}
