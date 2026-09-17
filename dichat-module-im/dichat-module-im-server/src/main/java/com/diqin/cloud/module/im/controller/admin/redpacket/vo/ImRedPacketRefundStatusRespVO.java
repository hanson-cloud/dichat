package com.diqin.cloud.module.im.controller.admin.redpacket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - IM 红包退款状态 Response VO")
@Data
public class ImRedPacketRefundStatusRespVO {

    @Schema(description = "红包编号", example = "1024")
    private Long id;

    @Schema(description = "红包状态（ImRedPacketStatusEnum）", example = "40")
    private Integer status;

    @Schema(description = "退款状态：10=已退款成功，0=待退款（已过期未领完），-1=无需退款", example = "10")
    private Integer refundStatus;

    @Schema(description = "退款状态说明", example = "已退款成功")
    private String refundStatusName;

    @Schema(description = "退款金额，单位：分（待退款时为剩余金额；已退款后归零）", example = "320")
    private Integer refundAmount;
}
