package com.diqin.cloud.module.im.controller.admin.withdraw.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 管理后台 - 提现开关配置设置 Request VO
 *
 * @author dichat
 */
@Schema(description = "管理后台 - 提现开关配置设置 Request VO")
@Data
public class ImWithdrawConfigSetReqVO {

    @Schema(description = "是否开放提现", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "提现开关状态不能为空")
    private Boolean enabled;

    @Schema(description = "关闭提现时的用户通知消息（关闭时必填）", example = "系统维护升级，提现通道暂未开放")
    private String closeMessage;

}
