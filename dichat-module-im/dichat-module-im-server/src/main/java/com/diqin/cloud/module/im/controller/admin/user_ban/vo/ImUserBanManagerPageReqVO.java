package com.diqin.cloud.module.im.controller.admin.user_ban.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM 封禁台账分页 Request VO")
@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class ImUserBanManagerPageReqVO extends PageParam {
    @Schema(description = "被处罚用户编号", example = "225") private Long userId;
    @Schema(description = "处罚类型：1-全局禁言 2-封号", example = "1") private Integer banType;
    @Schema(description = "状态：0-生效中 1-已解封", example = "0") private Integer status;
    @Schema(description = "创建时间") private LocalDateTime[] createTime;
}
