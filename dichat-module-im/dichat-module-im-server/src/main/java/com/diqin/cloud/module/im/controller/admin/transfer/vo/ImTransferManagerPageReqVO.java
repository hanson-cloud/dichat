package com.diqin.cloud.module.im.controller.admin.transfer.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 转账分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImTransferManagerPageReqVO extends PageParam {

    @Schema(description = "转账人用户编号", example = "1024")
    private Long senderUserId;

    @Schema(description = "收款人用户编号", example = "2048")
    private Long receiverUserId;

    @Schema(description = "状态", example = "20")
    private Integer status;

    @Schema(description = "创建时间", example = "[2026-04-01 00:00:00, 2026-04-30 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
