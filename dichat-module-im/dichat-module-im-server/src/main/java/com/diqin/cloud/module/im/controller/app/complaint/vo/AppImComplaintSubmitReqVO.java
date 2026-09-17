package com.diqin.cloud.module.im.controller.app.complaint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * App - IM 用户 - 提交意见反馈/举报 Request VO
 *
 * @author dichat
 */
@Schema(description = "App - IM 用户 - 提交意见反馈/举报 Request VO")
@Data
@Accessors(chain = true)
public class AppImComplaintSubmitReqVO {

    @Schema(description = "类别：6=意见反馈（默认），1-5=举报", example = "6")
    private Integer category = 6;

    @Schema(description = "反馈/举报内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "希望增加暗色模式切换")
    @NotEmpty(message = "内容不能为空")
    private String content;

    @Schema(description = "联系方式（选填）", example = "138****8888")
    private String contact;

}
