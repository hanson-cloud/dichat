package com.diqin.cloud.module.im.dto.redpacket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * APP - 抢红包 Request VO
 *
 * @author dichat
 */
@Schema(description = "APP - 抢红包 Request VO")
@Data
public class ImRedPacketGrabReqDTO {

    @Schema(description = "红包流水号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "红包流水号不能为空")
    private String redPacketNo;

    @Schema(description = "口令红包密码（口令红包必填）")
    @Size(max = 64, message = "口令不能超过 64 字")
    private String password;

}
