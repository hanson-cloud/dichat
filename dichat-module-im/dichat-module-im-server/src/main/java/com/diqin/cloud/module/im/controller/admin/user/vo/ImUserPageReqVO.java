package com.diqin.cloud.module.im.controller.admin.user.vo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static com.diqin.cloud.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * IM 用户分页查询 Request VO
 *
 * @author hanson
 */
@Schema(description = "管理后台 - IM 用户分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ImUserPageReqVO extends PageParam {

    @Schema(description = "登录账号（模糊）", example = "zhang")
    private String username;

    @Schema(description = "昵称（模糊）", example = "张")
    private String nickname;

    @Schema(description = "手机号（模糊）", example = "156")
    private String mobile;

    @Schema(description = "账号状态 0:正常 1:停用", example = "0")
    private Integer status;

    @Schema(description = "是否被封禁", example = "false")
    private Boolean banned;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "注册时间范围", example = "[\"2024-01-01 00:00:00\", \"2024-12-31 23:59:59\"]")
    private LocalDateTime[] createTime;
}
