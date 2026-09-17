package com.diqin.cloud.module.im.dto.paypassword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * APP - 支付安全状态汇总 Response VO
 * <p>
 * 供支付密码设置页初始化、支付前校验前置判断使用。
 *
 * @author dichat
 */
@Schema(description = "APP - 支付安全状态汇总 Response VO")
@Data
public class ImPaySecurityStatusRespDTO implements Serializable {

    @Schema(description = "是否已设置支付密码", example = "true")
    private Boolean hasPayPassword;

    @Schema(description = "手势密码是否已开启", example = "false")
    private Boolean gestureEnabled;

    @Schema(description = "指纹支付是否已开启", example = "false")
    private Boolean fingerprintEnabled;

    @Schema(description = "设备是否支持指纹认证（由客户端 Soter 判定，服务端默认 true）", example = "true")
    private Boolean fingerprintSupported;

}
