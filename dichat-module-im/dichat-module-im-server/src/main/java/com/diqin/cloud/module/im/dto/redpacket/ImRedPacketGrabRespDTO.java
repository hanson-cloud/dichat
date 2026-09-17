package com.diqin.cloud.module.im.dto.redpacket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * APP - 抢红包 Response VO
 *
 * @author dichat
 */
@Schema(description = "APP - 抢红包 Response VO")
@Data
public class ImRedPacketGrabRespDTO {

    @Schema(description = "抢到的金额，单位：分")
    private Long amount;

    @Schema(description = "本红包是否已抢完")
    private Boolean finished;

    @Schema(description = "当前用户是否已抢过（true 时 amount 为之前抢到的金额）")
    private Boolean alreadyGrabbed;

    @Schema(description = "红包祝福语")
    private String blessing;

}
