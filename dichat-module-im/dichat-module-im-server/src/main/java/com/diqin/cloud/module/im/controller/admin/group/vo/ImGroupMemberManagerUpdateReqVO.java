package com.diqin.cloud.module.im.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 群成员修改角色 Request VO")
@Data
public class ImGroupMemberManagerUpdateReqVO {

    @Schema(description = "群编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "群编号不能为空")
    private Long groupId;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "新成员角色（1=群主 2=管理员 3=普通成员）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "成员角色不能为空")
    private Integer role;

}
