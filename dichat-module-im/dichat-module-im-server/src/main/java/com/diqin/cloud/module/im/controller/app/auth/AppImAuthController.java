package com.diqin.cloud.module.im.controller.app.auth;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.security.config.SecurityProperties;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginReqVO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthLoginRespVO;
import com.diqin.cloud.module.im.controller.app.auth.vo.AppImAuthRegisterReqVO;
import com.diqin.cloud.module.im.service.auth.ImAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * App - IM 用户认证
 *
 * <p>参考 member 模块的路径规范，使用 {@code /im/auth} 前缀。
 * <br>网关路由 /app-api/im/** 通过 RewritePath 去掉 /app-api 前缀后转发到 /im/auth/login 等。
 * <br>完整访问路径为 /app-api/im/auth/login 等，与 member 模块的 /app-api/member/auth/login 风格一致。
 *
 * @author hanson
 */
@Tag(name = "用户 APP - IM 认证")
@RestController
@RequestMapping("/im/auth")
public class AppImAuthController {

    @Resource
    private ImAuthService authService;

    @Resource
    private SecurityProperties securityProperties;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "返回 accessToken / refreshToken；前端后续请求 Header 携带 Authorization=Bearer xxx")
    @PermitAll
    public CommonResult<AppImAuthLoginRespVO> login(@Valid @RequestBody AppImAuthLoginReqVO reqVO) {
        AppImAuthLoginRespVO respVO = authService.login(reqVO);
        return success(respVO);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "免登录；注册成功后默认 status=0 未停用、未封禁")
    @PermitAll
    public CommonResult<AppImAuthLoginRespVO> register(@Valid @RequestBody AppImAuthRegisterReqVO reqVO) {
        return success(authService.register(reqVO));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用 refreshToken 换取新的 accessToken / refreshToken")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    @PermitAll
    public CommonResult<AppImAuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        AppImAuthLoginRespVO respVO = authService.refreshToken(refreshToken);
        return success(respVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "从请求头中提取 accessToken 并标记登出；当前 mock token 场景下仅记录登出日志")
    @PermitAll
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token);
        }
        return success(true);
    }
}
