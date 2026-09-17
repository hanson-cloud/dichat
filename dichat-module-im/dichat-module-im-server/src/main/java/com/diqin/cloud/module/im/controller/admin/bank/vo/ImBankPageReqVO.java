package com.diqin.cloud.module.im.controller.admin.bank.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - IM 支持的银行 分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImBankPageReqVO extends PageParam {

    @Schema(description = "银行编码（模糊匹配）", example = "CMB")
    private String bankCode;

    @Schema(description = "银行名称（模糊匹配）", example = "招商")
    private String bankName;

    @Schema(description = "状态：0-停用 1-启用", example = "1")
    private Integer status;

}
