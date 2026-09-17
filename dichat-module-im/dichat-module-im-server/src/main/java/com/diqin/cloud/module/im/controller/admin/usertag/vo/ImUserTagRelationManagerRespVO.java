package com.diqin.cloud.module.im.controller.admin.usertag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 用户标签关联 Response VO")
@Data
public class ImUserTagRelationManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String userNickname;

    @Schema(description = "标签编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long tagId;

    @Schema(description = "标签名称", example = "VIP 用户")
    private String tagName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
