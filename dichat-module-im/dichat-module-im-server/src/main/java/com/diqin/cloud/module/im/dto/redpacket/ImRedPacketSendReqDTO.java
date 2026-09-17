package com.diqin.cloud.module.im.dto.redpacket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * APP - 发红包 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 发红包 Request VO")
@Data
public class ImRedPacketSendReqDTO {

    @Schema(description = "会话类型：1-私聊 2-群聊", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "会话类型不能为空")
    private Integer conversationType;

    @Schema(description = "私聊红包领取人用户编号；私聊必填")
    private Long receiverUserId;

    @Schema(description = "群编号；群聊必填")
    private Long groupId;

    @Schema(description = "红包总金额，单位：分", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "红包金额不能为空")
    @Positive(message = "红包金额必须大于 0")
    private Long totalAmount;

    @Schema(description = "红包总个数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "红包个数不能为空")
    @Min(value = 1, message = "红包个数至少为 1")
    @Max(value = 100, message = "红包个数不能超过 100")
    private Integer totalCount;

    @Schema(description = "类型：0-普通红包 1-拼手气红包", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "红包类型不能为空")
    private Integer type;

    @Schema(description = "祝福语")
    @Size(max = 128, message = "祝福语不能超过 128 字")
    private String blessing;

    @Schema(description = "口令红包密码（可选；非空则成为口令红包）")
    @Size(max = 64, message = "口令不能超过 64 字")
    private String password;

    @Schema(description = "支付密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "支付密码不能为空")
    private String payPassword;

}
