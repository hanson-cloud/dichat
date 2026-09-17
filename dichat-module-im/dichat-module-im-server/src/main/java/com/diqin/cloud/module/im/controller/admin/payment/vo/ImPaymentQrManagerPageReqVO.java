package com.diqin.cloud.module.im.controller.admin.payment.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - IM 收付款收款码分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImPaymentQrManagerPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "收款码（模糊匹配）", example = "PAY")
    private String code;

    @Schema(description = "状态：0-有效 1-已使用 2-已作废", example = "0")
    private Integer status;
}
