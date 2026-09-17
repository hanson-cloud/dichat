package com.diqin.cloud.module.im.controller.admin.robot.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - IM 机器人自动回复规则分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImRobotReplyRuleManagerPageReqVO extends PageParam {

    @Schema(description = "机器人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long robotId;

    @Schema(description = "触发关键词（模糊匹配）", example = "你好")
    private String keyword;

    @Schema(description = "状态：0-停用 1-启用", example = "1")
    private Integer status;

}
