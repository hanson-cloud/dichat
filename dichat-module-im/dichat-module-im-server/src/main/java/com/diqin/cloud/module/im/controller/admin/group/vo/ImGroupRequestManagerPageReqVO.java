package com.diqin.cloud.module.im.controller.admin.group.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 加群申请分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImGroupRequestManagerPageReqVO extends PageParam {

    @Schema(description = "群组编号", example = "1024")
    private Long groupId;

    @Schema(description = "申请用户编号", example = "2048")
    private Long userId;

    @Schema(description = "邀请人用户编号", example = "4096")
    private Long inviterUserId;

    @Schema(description = "处理结果（枚举 ImGroupRequestHandleResultEnum）", example = "0")
    private Integer handleResult;

    @Schema(description = "添加来源（枚举 ImFriendAddSourceEnum）", example = "1")
    private Integer addSource;

    @Schema(description = "创建时间", example = "[2026-04-01 00:00:00, 2026-04-30 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
