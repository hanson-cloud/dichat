package com.diqin.cloud.module.im.controller.admin.friend.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 好友关系分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ImFriendManagerPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "好友用户编号", example = "2048")
    private Long friendUserId;

    @Schema(description = "好友状态（0 正常 / 1 已删除）", example = "0")
    private Integer status;

    @Schema(description = "是否免打扰", example = "false")
    private Boolean silent;

    @Schema(description = "好友展示备注，模糊匹配", example = "老同学")
    private String displayName;

    @Schema(description = "添加好友时间", example = "[2026-04-01 00:00:00, 2026-04-30 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] addTime;

}
