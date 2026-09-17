package com.diqin.cloud.module.im.controller.admin.user_ban.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - IM 封禁更新 Request VO")
@Data @EqualsAndHashCode(callSuper = true)
public class ImUserBanManagerUpdateReqVO extends ImUserBanManagerSaveReqVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号不能为空") private Long id;
}
