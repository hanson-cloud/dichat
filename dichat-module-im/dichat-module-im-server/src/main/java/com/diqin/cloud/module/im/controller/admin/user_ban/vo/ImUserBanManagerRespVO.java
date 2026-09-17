package com.diqin.cloud.module.im.controller.admin.user_ban.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 封禁台账 Response VO")
@Data
public class ImUserBanManagerRespVO {
    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED) private Long id;
    @Schema(description = "被处罚用户编号", requiredMode = Schema.RequiredMode.REQUIRED) private Long userId;
    @Schema(description = "处罚类型：1-全局禁言 2-封号", requiredMode = Schema.RequiredMode.REQUIRED) private Integer banType;
    @Schema(description = "处罚原因") private String reason;
    @Schema(description = "执行人编号") private Long bannedBy;
    @Schema(description = "处罚开始时间") private LocalDateTime banStartTime;
    @Schema(description = "处罚结束时间（空=永久）") private LocalDateTime banEndTime;
    @Schema(description = "状态：0-生效中 1-已解封") private Integer status;
    @Schema(description = "解封人编号") private Long unbannedBy;
    @Schema(description = "解封时间") private LocalDateTime unbanTime;
    @Schema(description = "解封原因") private String unbanReason;
    @Schema(description = "创建时间") private LocalDateTime createTime;
}
