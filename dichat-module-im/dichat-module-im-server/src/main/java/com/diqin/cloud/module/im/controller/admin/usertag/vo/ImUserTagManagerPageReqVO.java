package com.diqin.cloud.module.im.controller.admin.usertag.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 用户标签分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImUserTagManagerPageReqVO extends PageParam {

    @Schema(description = "标签名称（模糊匹配）", example = "VIP")
    private String name;

    @Schema(description = "状态：0-停用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
