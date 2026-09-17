package com.diqin.cloud.module.im.controller.app.robot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * App - 常见问题 Response VO
 *
 * @author 速构构
 */
@Schema(description = "App - 常见问题 Response VO")
@Data
public class AppImCommonQuestionRespVO {

    @Schema(description = "规则编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "机器人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long robotId;

    @Schema(description = "常见问题问题文本", example = "如何修改个人资料？")
    private String question;

    @Schema(description = "答案内容", example = "请在「我 > 个人资料」中点击头像或昵称进行修改。")
    private String replyContent;

    @Schema(description = "排序", example = "0")
    private Integer sort;

}
