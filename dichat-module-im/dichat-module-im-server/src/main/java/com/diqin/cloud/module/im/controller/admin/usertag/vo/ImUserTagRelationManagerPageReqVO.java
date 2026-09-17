package com.diqin.cloud.module.im.controller.admin.usertag.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - IM 用户标签关联分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImUserTagRelationManagerPageReqVO extends PageParam {

    @Schema(description = "标签编号", example = "1024")
    private Long tagId;

    @Schema(description = "用户编号", example = "2048")
    private Long userId;

}
