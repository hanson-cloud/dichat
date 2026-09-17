package com.diqin.cloud.module.im.controller.app.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户 APP - 提现开关配置 Response VO
 *
 * @author dichat
 */
@Schema(description = "用户 APP - 提现开关配置 Response VO")
@Data
public class AppImWithdrawConfigRespVO {

    @Schema(description = "是否开放提现", example = "true")
    private Boolean enabled;

    @Schema(description = "关闭提现时的用户通知消息（关闭时展示在提现页）", example = "系统维护升级，提现通道暂未开放")
    private String closeMessage;

}
