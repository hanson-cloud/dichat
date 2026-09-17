package com.diqin.cloud.module.im.controller.app.message.vo.privates;

import com.diqin.cloud.framework.common.validation.InEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 私聊消息发送 Request VO
 */
@Schema(description = "管理后台 - 私聊消息发送 Request VO")
@Data
public class AppImPrivateMessageSendReqVO {

    @Schema(description = "客户端消息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-xxx")
    @NotEmpty(message = "客户端消息编号不能为空")
    private String clientMessageId;

    @Schema(description = "接收人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "接收人编号不能为空")
    private Long receiverId;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
    @NotNull(message = "消息类型不能为空")
    @InEnum(ImMessageTypeEnum.class)
    private Integer type;

    @Schema(description = "消息内容，JSON 格式", requiredMode = Schema.RequiredMode.REQUIRED, example = "{\"content\":\"你好\"}")
    @NotEmpty(message = "消息内容不能为空")
    private String content;

    // ========== 以下字段仅群聊使用，私聊忽略 ==========
    // atUserIds: 私聊不需要 @ 功能，该字段仅存在于群聊发送 VO 中
    // receipt: 私聊已读回执通过 /im/message/private/read 接口实现，此字段保留供未来扩展

    @Schema(description = "是否需要回执（保留字段，暂未启用）", example = "false")
    private Boolean receipt;

    /**
     * 仅允许用户消息（normal）类型
     * 使用循环代替 validate()，避免 type 不合法时抛异常导致校验器无法访问此方法（HV000090）
     */
    @AssertTrue(message = "消息类型不允许")
    @JsonIgnore
    public boolean isTypeNormal() {
        if (type == null) return false;
        for (ImMessageTypeEnum e : ImMessageTypeEnum.values()) {
            if (e.getType().equals(type)) {
                return e.isNormal();
            }
        }
        return false;
    }

}
