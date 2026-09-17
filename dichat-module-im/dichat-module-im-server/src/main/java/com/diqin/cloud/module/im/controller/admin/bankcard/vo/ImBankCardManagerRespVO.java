package com.diqin.cloud.module.im.controller.admin.bankcard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 银行卡 Response VO")
@Data
public class ImBankCardManagerRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "银行名称", example = "招商银行")
    private String bankName;

    @Schema(description = "银行编码", example = "CMB")
    private String bankCode;

    @Schema(description = "脱敏卡号", example = "****0123")
    private String cardNoMask;

    @Schema(description = "持卡人姓名", example = "张三")
    private String cardholder;

    @Schema(description = "脱敏证件号", example = "****1234")
    private String idCardMask;

    @Schema(description = "银行预留手机号", example = "13800138000")
    private String phone;

    @Schema(description = "卡片类型", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "10")
    private Integer status;

    @Schema(description = "是否默认卡", example = "false")
    private Boolean isDefault;

    @Schema(description = "绑定时间")
    private LocalDateTime bindTime;

    @Schema(description = "审核人编号", example = "1")
    private Long auditUserId;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "审核原因", example = "资料不全")
    private String auditReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
