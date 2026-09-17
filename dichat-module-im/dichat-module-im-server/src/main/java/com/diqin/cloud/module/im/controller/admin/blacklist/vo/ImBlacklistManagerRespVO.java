package com.diqin.cloud.module.im.controller.admin.blacklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 全局黑名单 Response VO")
@Data
public class ImBlacklistManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "操作用户编号（谁拉的黑）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "操作用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "被拉黑用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long blockId;

    @Schema(description = "被拉黑用户昵称", example = "李四")
    private String blockNickname;

    @Schema(description = "拉黑原因")
    private String reason;

    @Schema(description = "拉黑时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
