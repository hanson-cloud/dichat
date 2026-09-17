package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import com.diqin.cloud.framework.common.validation.InEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 客服工作台代发消息 Request VO
 *
 * @author 速构构
 */
@Schema(description = "管理后台 - 客服工作台代发消息 Request VO")
@Data
public class ImCsMessageSendReqVO {

    @Schema(description = "接收方用户编号（im_users.id，即会话对端）", requiredMode = Schema.RequiredMode.REQUIRED, example = "225")
    @NotNull(message = "接收方用户编号不能为空")
    private Long peerId;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
    @NotNull(message = "消息类型不能为空")
    @InEnum(ImMessageTypeEnum.class)
    private Integer type;

    @Schema(description = "消息内容，JSON 格式（与 APP 端一致，如文本为 {\"content\":\"你好\"}）",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "{\"content\":\"您好\"}")
    @NotEmpty(message = "消息内容不能为空")
    private String content;

    /**
     * 仅允许用户可发送（normal）类型
     */
    @AssertTrue(message = "消息类型不允许")
    @Schema(hidden = true)
    public boolean isTypeNormal() {
        if (type == null) {
            return false;
        }
        for (ImMessageTypeEnum e : ImMessageTypeEnum.values()) {
            if (e.getType().equals(type)) {
                return e.isNormal();
            }
        }
        return false;
    }

}
