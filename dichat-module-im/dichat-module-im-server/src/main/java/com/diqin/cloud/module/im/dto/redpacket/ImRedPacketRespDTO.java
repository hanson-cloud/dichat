package com.diqin.cloud.module.im.dto.redpacket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * APP - 红包 Response VO
 *
 * @author dichat
 */
@Schema(description = "APP - 红包 Response VO")
@Data
public class ImRedPacketRespDTO {

    @Schema(description = "红包编号")
    private Long id;

    @Schema(description = "发红包用户编号")
    private Long fromUserId;

    @Schema(description = "红包总金额，单位：分")
    private Long totalAmount;

    @Schema(description = "红包总个数")
    private Integer totalCount;

    @Schema(description = "类型：0-普通红包 1-拼手气红包")
    private Integer type;

    @Schema(description = "红包流水号")
    private String transferNo;

    @Schema(description = "状态：0-待领取 1-已抢完 2-已退款 3-已过期")
    private Integer status;

    @Schema(description = "剩余金额，单位：分")
    private Long remainAmount;

    @Schema(description = "剩余个数")
    private Integer remainCount;

    @Schema(description = "祝福语")
    private String blessing;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "当前登录用户是否已抢过该红包")
    private Boolean grabbed;

    @Schema(description = "当前登录用户已抢金额（未抢为 null），单位：分")
    private Long grabbedAmount;

}
