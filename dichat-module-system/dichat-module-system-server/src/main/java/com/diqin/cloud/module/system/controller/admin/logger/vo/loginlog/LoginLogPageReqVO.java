package com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 登录日志分页列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLogPageReqVO extends PageParam {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户 IP，模拟匹配", example = "127.0.0.1")
    private String userIp;

    @Schema(description = "用户账号，模拟匹配", example = "芋道")
    private String username;

    @Schema(description = "用户类型，参见 UserTypeEnum 枚举，用于按会员/管理后台筛选", example = "2")
    private Integer userType;

    @Schema(description = "操作状态（登录结果是否成功）", example = "true")
    private Boolean status;

    @Schema(description = "登录会话状态：1-在线 2-已离线 3-已强制下线，参见 LoginLogStatusEnum", example = "1")
    private Integer onlineStatus;

    @Schema(description = "登录时间", example = "[2022-07-01 00:00:00,2022-07-01 23:59:59]")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
