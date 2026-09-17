package com.diqin.cloud.module.im.controller.admin.complaint.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 用户投诉（举报）分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImComplaintManagerPageReqVO extends PageParam {

    @Schema(description = "投诉人用户编号", example = "1024")
    private Long complainantId;

    @Schema(description = "被投诉人用户编号", example = "2048")
    private Long respondentId;

    @Schema(description = "投诉类别（1=骚扰 2=诈骗 3=色情 4=虚假信息 5=其他违规）", example = "1")
    private Integer category;

    @Schema(description = "处理状态（0=待处理 1=处理中 2=已处理 3=已驳回）", example = "0")
    private Integer status;

    @Schema(description = "投诉描述，模糊匹配", example = "发布不良信息")
    private String content;

    @Schema(description = "创建时间", example = "[2026-04-01 00:00:00, 2026-04-30 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
