package com.diqin.cloud.module.im.controller.admin.robot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 机器人 Response VO")
@Data
public class ImRobotManagerRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "机器人登录账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "robot_001")
    private String username;

    @Schema(description = "昵称", example = "小助手")
    private String nickname;

    @Schema(description = "头像地址", example = "https://xxx.png")
    private String avatar;

    @Schema(description = "描述", example = "智能客服机器人")
    private String description;

    @Schema(description = "回调地址", example = "https://callback.example.com/robot")
    private String webhookUrl;

    @Schema(description = "是否开启自动回复：0-关闭 1-开启", example = "1")
    private Boolean autoReplyEnabled;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
