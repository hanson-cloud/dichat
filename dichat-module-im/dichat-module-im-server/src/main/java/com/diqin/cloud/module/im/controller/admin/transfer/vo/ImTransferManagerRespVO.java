package com.diqin.cloud.module.im.controller.admin.transfer.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 转账 Response VO")
@Data
public class ImTransferManagerRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "转账单号", example = "uuid-xxx")
    private String no;

    @Schema(description = "转账人用户编号", example = "1024")
    private Long senderUserId;

    @Schema(description = "收款人用户编号", example = "2048")
    private Long receiverUserId;

    @Schema(description = "转账金额，单位：分", example = "1000")
    private Integer amount;

    @Schema(description = "转账备注", example = "午饭钱")
    private String remark;

    @Schema(description = "状态", example = "20")
    private Integer status;

    @Schema(description = "完成时间")
    private LocalDateTime payTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
