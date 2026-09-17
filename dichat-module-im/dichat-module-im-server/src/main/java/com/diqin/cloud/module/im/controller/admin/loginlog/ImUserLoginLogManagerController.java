package com.diqin.cloud.module.im.controller.admin.loginlog;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.enums.ForceOfflineReason;
import com.diqin.cloud.module.im.service.loginlog.ImUserLoginLogManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - IM 用户登录日志（强制下线）
 *
 * <p>登录日志的「查询 / 分页」已收敛到 system 模块，本 Controller 仅保留「强制下线」——
 * 因为真实断开 WebSocket 会话必须由 IM 模块完成（WebSocket 连接只进 IM 模块）。</p>
 *
 * @author hanson
 */
@Tag(name = "管理后台 - IM 用户登录日志")
@RestController
@RequestMapping("/im/manager/login-log")
@Validated
public class ImUserLoginLogManagerController {

    @Resource
    private ImUserLoginLogManagerService loginLogManagerService;

    @PostMapping("/force-offline")
    @Operation(summary = "强制下线（踢出当前终端）")
    @PreAuthorize("@ss.hasPermission('im:manager:login-log:offline')")
    public CommonResult<Boolean> forceOffline(@RequestParam("id") Long id) {
        loginLogManagerService.forceOffline(id, ForceOfflineReason.ADMIN);
        return success(true);
    }

}
