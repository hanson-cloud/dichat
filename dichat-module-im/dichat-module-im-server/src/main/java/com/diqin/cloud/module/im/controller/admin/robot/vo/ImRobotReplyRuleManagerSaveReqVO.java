package com.diqin.cloud.module.im.controller.admin.robot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 机器人自动回复规则新增 Request VO")
@Data
public class ImRobotReplyRuleManagerSaveReqVO {

    @Schema(description = "机器人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "机器人编号不能为空")
    private Long robotId;

    @Schema(description = "触发关键词", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好")
    @NotBlank(message = "触发关键词不能为空")
    private String keyword;

    @Schema(description = "匹配方式：1-精确匹配 2-包含匹配 3-正则匹配", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "匹配方式不能为空")
    private Integer matchType;

    @Schema(description = "回复类型：1-文本 2-转人工 3-常见问题", example = "1")
    private Integer replyType;

    @Schema(description = "常见问题问题文本（reply_type=3 时使用）", example = "如何修改个人资料？")
    private String question;

    @Schema(description = "回复内容（reply_type=1/3 时作为答案，reply_type=2 时可空）", requiredMode = Schema.RequiredMode.REQUIRED, example = "您好，有什么可以帮您？")
    @NotBlank(message = "回复内容不能为空")
    private String replyContent;

    @Schema(description = "状态：0-停用 1-启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;

}
