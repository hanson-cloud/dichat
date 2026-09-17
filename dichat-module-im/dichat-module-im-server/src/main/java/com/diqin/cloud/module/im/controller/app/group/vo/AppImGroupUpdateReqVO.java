package com.diqin.cloud.module.im.controller.app.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 群更新 Request VO")
@Data
public class AppImGroupUpdateReqVO {

    @Schema(description = "群编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1003")
    @NotNull(message = "群编号不能为空")
    private Long id;

    @Schema(description = "群名称", example = "芋道技术交流群")
    @Size(max = 64, message = "群名称长度不能超过 64")
    private String name;

    @Schema(description = "群头像")
    @Size(max = 512, message = "群头像长度不能超过 512")
    private String avatar;

    @Schema(description = "群公告")
    @Size(max = 2048, message = "群公告长度不能超过 2048")
    private String notice;

    @Schema(description = "进群是否需群主 / 管理员审批", example = "true")
    private Boolean joinApproval;

}
