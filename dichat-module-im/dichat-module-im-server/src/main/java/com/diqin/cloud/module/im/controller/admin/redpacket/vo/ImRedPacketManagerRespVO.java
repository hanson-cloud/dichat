package com.diqin.cloud.module.im.controller.admin.redpacket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - IM 红包 Response VO")
@Data
public class ImRedPacketManagerRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "红包单号", example = "uuid-xxx")
    private String no;

    @Schema(description = "发送人用户编号", example = "1024")
    private Long senderUserId;

    @Schema(description = "会话类型", example = "1")
    private Integer conversationType;

    @Schema(description = "私聊红包领取人编号", example = "2048")
    private Long receiverUserId;

    @Schema(description = "群编号", example = "3072")
    private Long groupId;

    @Schema(description = "红包类型", example = "2")
    private Integer type;

    @Schema(description = "红包总金额，单位：分", example = "1000")
    private Integer totalAmount;

    @Schema(description = "红包总个数", example = "10")
    private Integer totalCount;

    @Schema(description = "剩余金额，单位：分", example = "320")
    private Integer remainAmount;

    @Schema(description = "剩余个数", example = "3")
    private Integer remainCount;

    @Schema(description = "祝福语", example = "恭喜发财")
    private String greeting;

    @Schema(description = "红包状态", example = "10")
    private Integer status;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "已领取金额，单位：分", example = "680")
    private Integer grabbedAmount;

    @Schema(description = "已领取个数", example = "7")
    private Integer grabbedCount;

    @Schema(description = "领取明细")
    private List<ImRedPacketGrabManagerRespVO> grabs;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
