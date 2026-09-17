package com.diqin.cloud.module.im.controller.admin.usertag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - IM 用户标签关联新增 Request VO")
@Data
public class ImUserTagRelationManagerSaveReqVO {

    @Schema(description = "标签编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "标签编号不能为空")
    private Long tagId;

    @Schema(description = "用户编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[2048, 3096]")
    @NotEmpty(message = "用户列表不能为空")
    private List<Long> userIds;

}
