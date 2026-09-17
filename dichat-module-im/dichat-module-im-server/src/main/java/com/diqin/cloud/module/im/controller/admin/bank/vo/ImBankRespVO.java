package com.diqin.cloud.module.im.controller.admin.bank.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 支持的银行 Response VO")
@Data
public class ImBankRespVO {

    @Schema(description = "编号", example = "1024")
    private Long id;

    @Schema(description = "银行编码", example = "CMB")
    private String bankCode;

    @Schema(description = "银行名称", example = "招商银行")
    private String bankName;

    @Schema(description = "卡组织：UNIONPAY/VISA/MASTER/JCB/AMEX", example = "UNIONPAY")
    private String cardOrg;

    @Schema(description = "BIN 前缀（逗号分隔，卡号前 6 位）", example = "622575,621483")
    private String binPrefix;

    @Schema(description = "银行 LOGO 地址")
    private String logoUrl;

    @Schema(description = "状态：0-停用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "排序（越小越靠前）", example = "50")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
