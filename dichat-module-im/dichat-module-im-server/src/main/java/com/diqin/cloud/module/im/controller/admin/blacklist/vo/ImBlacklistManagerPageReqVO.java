package com.diqin.cloud.module.im.controller.admin.blacklist.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 全局黑名单分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImBlacklistManagerPageReqVO extends PageParam {

    @Schema(description = "操作用户编号（谁拉的黑）", example = "1024")
    private Long userId;

    @Schema(description = "被拉黑用户编号", example = "2048")
    private Long blockId;

    @Schema(description = "拉黑原因，模糊匹配", example = "骚扰谩骂")
    private String reason;

    @Schema(description = "拉黑时间", example = "[2026-04-01 00:00:00, 2026-04-30 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
