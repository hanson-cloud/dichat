package com.diqin.cloud.module.im.controller.app.paypassword;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.validation.Mobile;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.dto.paypassword.*;
import com.diqin.cloud.module.im.service.paypassword.ImPayPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 支付密码
 * <p>
 * 支付密码为 IM 模块专属能力（pay 模块无此功能），保留本地实现。
 * 覆盖：设置 / 校验 / 重置（短信验证）/ 手势密码 / 指纹支付 / 安全状态汇总。
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 支付密码")
@RestController
@RequestMapping("/im/pay-password")
@Validated
public class AppImPayPasswordController {

    @Resource
    private ImPayPasswordService payPasswordService;

    @PostMapping("/set")
    @Operation(summary = "设置 / 重置支付密码（BCrypt 哈希存储）")
    public CommonResult<Boolean> setPassword(@Valid @RequestBody ImPayPasswordSetReqDTO reqDTO) {
        payPasswordService.setPassword(SecurityFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @GetMapping("/has")
    @Operation(summary = "是否已完成支付密码设置")
    public CommonResult<Boolean> hasPassword() {
        return success(payPasswordService.hasPassword(SecurityFrameworkUtils.getLoginUserId()));
    }

    // ==================== 重置支付密码（短信验证） ====================

    @PostMapping("/reset/send-sms")
    @Operation(summary = "发送重置支付密码短信验证码")
    public CommonResult<Boolean> sendResetSms(@RequestParam("phone") @Mobile @NotEmpty(message = "手机号不能为空") String phone) {
        payPasswordService.sendResetSms(SecurityFrameworkUtils.getLoginUserId(), phone);
        return success(true);
    }

    @PostMapping("/reset")
    @Operation(summary = "重置支付密码（短信验证码校验后设置新密码）")
    public CommonResult<Boolean> resetPassword(@Valid @RequestBody ImPayPasswordResetReqDTO reqDTO) {
        payPasswordService.resetPassword(SecurityFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    // ==================== 手势密码 ====================

    @PostMapping("/gesture/set")
    @Operation(summary = "设置手势密码（存储 SHA-256 哈希）")
    public CommonResult<Boolean> setGesturePattern(@Valid @RequestBody ImPayPasswordGestureSetReqDTO reqDTO) {
        payPasswordService.setGesturePattern(SecurityFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    @PostMapping("/gesture/verify")
    @Operation(summary = "验证手势密码（支付时调用）")
    public CommonResult<Boolean> verifyGesturePattern(@Valid @RequestBody ImPayPasswordGestureVerifyReqDTO reqDTO) {
        return success(payPasswordService.verifyGesturePattern(SecurityFrameworkUtils.getLoginUserId(), reqDTO));
    }

    @PostMapping("/gesture/clear")
    @Operation(summary = "清除手势密码")
    public CommonResult<Boolean> clearGesturePattern() {
        payPasswordService.clearGesturePattern(SecurityFrameworkUtils.getLoginUserId());
        return success(true);
    }

    // ==================== 指纹支付 ====================

    @PostMapping("/fingerprint/toggle")
    @Operation(summary = "开关指纹支付")
    public CommonResult<Boolean> toggleFingerprint(@Valid @RequestBody ImPayPasswordFingerprintToggleReqDTO reqDTO) {
        payPasswordService.toggleFingerprint(SecurityFrameworkUtils.getLoginUserId(), reqDTO);
        return success(true);
    }

    // ==================== 安全状态汇总 ====================

    @GetMapping("/security-status")
    @Operation(summary = "获取支付安全状态汇总（支付密码/手势/指纹）")
    public CommonResult<ImPaySecurityStatusRespDTO> securityStatus() {
        return success(payPasswordService.getSecurityStatus(SecurityFrameworkUtils.getLoginUserId()));
    }

}
