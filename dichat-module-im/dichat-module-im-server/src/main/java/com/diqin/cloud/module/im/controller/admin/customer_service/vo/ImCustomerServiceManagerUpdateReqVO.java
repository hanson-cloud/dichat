package com.diqin.cloud.module.im.controller.admin.customer_service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - IM 客服更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImCustomerServiceManagerUpdateReqVO extends ImCustomerServiceManagerSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "编号不能为空")
    private Long id;

}
