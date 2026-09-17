package com.diqin.cloud.module.im.controller.admin.complaint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM 用户投诉（举报）审核处置 Request VO")
@Data
public class ImComplaintManagerHandleReqVO {

    @Schema(description = "投诉编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "投诉编号不能为空")
    private Long id;

    @Schema(description = "处罚措施（1=警告 2=禁言 3=封号 4=无处罚）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "处罚措施不能为空")
    private Integer punishment;

    @Schema(description = "处理结果描述", example = "核实存在违规行为，予以封号处理")
    private String handleResult;

}
