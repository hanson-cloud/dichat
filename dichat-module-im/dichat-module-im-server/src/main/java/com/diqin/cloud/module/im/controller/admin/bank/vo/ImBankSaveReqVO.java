package com.diqin.cloud.module.im.controller.admin.bank.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 支持的银行 保存 Request VO")
@Data
public class ImBankSaveReqVO {

    @Schema(description = "编号（更新时必填）", example = "1024")
    private Long id;

    @Schema(description = "银行编码（唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "CMB")
    @NotEmpty(message = "银行编码不能为空")
    private String bankCode;

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "招商银行")
    @NotEmpty(message = "银行名称不能为空")
    private String bankName;

    @Schema(description = "卡组织：UNIONPAY/VISA/MASTER/JCB/AMEX", example = "UNIONPAY")
    private String cardOrg;

    @Schema(description = "BIN 前缀（逗号分隔，卡号前 6 位）", example = "622575,621483")
    private String binPrefix;

    @Schema(description = "银行 LOGO 地址")
    private String logoUrl;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序（越小越靠前）", example = "50")
    private Integer sort;

}
