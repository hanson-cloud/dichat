package com.diqin.cloud.module.im.controller.app.message.vo.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 群消息物理删除 Request VO
 * <p>
 * 语义：当前用户视角下，删除其在 groupId 群中自己发送的若干条 messageIds；
 * 1. 仅当消息发送人是当前用户时才能删除（避免越权删除他人消息）
 * 2. 群主 / 管理员可删除他人消息（与 box-im 群场景一致）
 * 3. 物理删除，删除后不可恢复；如需"撤回"语义请走 /recall 接口
 *
 * @author hanson
 */
@Schema(description = "管理后台 - 群消息物理删除 Request VO")
@Data
public class AppImGroupMessageDeleteReqVO {

    @Schema(description = "群编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "群编号不能为空")
    private Long groupId;

    @Schema(description = "待删除的消息编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "消息编号列表不能为空")
    private List<Long> messageIds;

}
