package com.diqin.cloud.module.im.controller.admin.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台 - 提现开关配置 Response VO
 *
 * @author dichat
 */
@Schema(description = "管理后台 - 提现开关配置 Response VO")
@Data
public class ImWithdrawConfigRespVO {

    @Schema(description = "是否开放提现", example = "true")
    private Boolean enabled;

    @Schema(description = "关闭提现时的用户通知消息", example = "系统维护升级，提现通道暂未开放")
    private String closeMessage;

    @Schema(description = "最近操作管理员编号")
    private Long operatorId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
