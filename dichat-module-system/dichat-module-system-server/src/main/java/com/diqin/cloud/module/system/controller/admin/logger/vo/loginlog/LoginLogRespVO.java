package com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog;

import com.diqin.cloud.framework.excel.core.annotations.DictFormat;
import com.diqin.cloud.framework.excel.core.convert.DictConvert;
import com.diqin.cloud.module.system.enums.DictTypeConstants;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 登录日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class LoginLogRespVO {

    @Schema(description = "日志编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("日志主键")
    private Long id;

    @Schema(description = "日志类型，参见 LoginLogTypeEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "日志类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.LOGIN_TYPE)
    private Integer logType;

    @Schema(description = "用户编号", example = "666")
    private Long userId;

    @Schema(description = "用户类型，参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer userType;

    @Schema(description = "链路追踪编号", example = "89aca178-a370-411c-ae02-3f0d672be4ab")
    private String traceId;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "dichat")
    @ExcelProperty("用户账号")
    private String username;

    @Schema(description = "登录结果，参见 LoginResultEnum 枚举类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "登录结果", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.LOGIN_RESULT)
    private Integer result;

    @Schema(description = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("登录 IP")
    private String userIp;

    @Schema(description = "浏览器 UserAgent", example = "Mozilla/5.0")
    @ExcelProperty("浏览器 UA")
    private String userAgent;

    @Schema(description = "终端类型（TerminalEnum）：0-未知 10-小程序 11-公众号 20-H5 31-App", example = "31")
    private Integer terminal;

    @Schema(description = "登录状态：1-在线 2-已离线 3-已强制下线，参见 LoginLogStatusEnum", example = "1")
    private Integer status;

    @Schema(description = "登出时间（主动 / 强制 / 超时）")
    private LocalDateTime logoutTime;

    @Schema(description = "登录时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("登录时间")
    private LocalDateTime createTime;

}
