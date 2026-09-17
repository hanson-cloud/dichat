package com.diqin.cloud.module.im.dto.transfer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * APP - 转账订单 Response VO
 *
 * @author dichat
 */
@Schema(description = "APP - 转账订单 Response VO")
@Data
public class ImTransferRespDTO {

    @Schema(description = "转账订单编号", example = "1")
    private Long id;

    @Schema(description = "转账方用户编号")
    private Long fromUserId;

    @Schema(description = "收款方用户编号")
    private Long toUserId;

    @Schema(description = "转账金额，单位：分")
    private Long amount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "转账流水号")
    private String transferNo;

    @Schema(description = "状态：0-待领取 1-已到账 2-已退款 3-已过期")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

}
