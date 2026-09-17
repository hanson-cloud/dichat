package com.diqin.cloud.module.im.controller.admin.user_ban.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 封禁新增 Request VO")
@Data
public class ImUserBanManagerSaveReqVO {
    @Schema(description = "被处罚用户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户编号不能为空") private Long userId;
    @Schema(description = "处罚类型：1-全局禁言 2-封号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "处罚类型不能为空") private Integer banType;
    @Schema(description = "处罚原因") private String reason;
    @Schema(description = "处罚结束时间（空=永久）") private LocalDateTime banEndTime;
}
