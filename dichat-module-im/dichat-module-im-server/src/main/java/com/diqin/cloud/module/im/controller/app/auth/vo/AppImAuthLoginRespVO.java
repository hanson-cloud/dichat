package com.diqin.cloud.module.im.controller.app.auth.vo;

import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * App - IM 用户登录 Response VO
 *
 * @author hanson
 */
@Schema(description = "App - IM 用户登录 Response VO")
@Data
@Accessors(chain = true)
public class AppImAuthLoginRespVO {

    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "happy")
    private String accessToken;

    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "nice")
    private String refreshToken;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

    @Schema(description = "用户信息")
    private AppImUserRespVO user;
}
