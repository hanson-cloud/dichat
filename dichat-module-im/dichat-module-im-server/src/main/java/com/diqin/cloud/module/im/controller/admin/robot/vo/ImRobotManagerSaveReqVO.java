package com.diqin.cloud.module.im.controller.admin.robot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 机器人新增 Request VO")
@Data
public class ImRobotManagerSaveReqVO {

    @Schema(description = "机器人登录账号（全局唯一）", requiredMode = Schema.RequiredMode.REQUIRED, example = "robot_001")
    @NotBlank(message = "机器人登录账号不能为空")
    private String username;

    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小助手")
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @Schema(description = "头像地址", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "描述", example = "智能客服机器人")
    private String description;

    @Schema(description = "回调地址", example = "https://callback.example.com/robot")
    private String webhookUrl;

    @Schema(description = "是否开启自动回复：0-关闭 1-开启", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "自动回复开关不能为空")
    private Boolean autoReplyEnabled;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

}
