package com.diqin.cloud.module.im.controller.admin.redpacket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 红包领取明细 Response VO")
@Data
public class ImRedPacketGrabManagerRespVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "红包单号", example = "uuid-xxx")
    private String redPacketNo;

    @Schema(description = "领取人用户编号", example = "2048")
    private Long userId;

    @Schema(description = "领取金额，单位：分", example = "88")
    private Integer amount;

    @Schema(description = "是否手气最佳", example = "false")
    private Boolean isBestLuck;

    @Schema(description = "领取时间")
    private LocalDateTime grabTime;

}
