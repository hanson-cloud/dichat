package com.diqin.cloud.module.im.controller.admin.user_ban.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 封禁解封 Request VO")
@Data
public class ImUserBanManagerUnbanReqVO {
    @Schema(description = "封禁记录编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号不能为空") private Long id;
    @Schema(description = "解封原因") private String unbanReason;
}
