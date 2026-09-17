package com.diqin.cloud.module.im.service.paypassword;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.module.im.dal.dataobject.paypassword.ImPayPasswordDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.paypassword.ImPayPasswordMapper;
import com.diqin.cloud.module.im.dto.paypassword.*;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.diqin.cloud.module.system.api.sms.SmsCodeApi;
import com.diqin.cloud.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.diqin.cloud.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.diqin.cloud.module.system.enums.sms.SmsSceneEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

import static com.diqin.cloud.framework.common.util.servlet.ServletUtils.getClientIP;

/**
 * IM 支付密码 Service 实现
 * <p>
 * 密码以 BCrypt 哈希存储，永不落明文。手势密码存储其 SHA-256 哈希（点序哈希），
 * 指纹支付仅存储开关标记（生物特征本身留在本机安全区，不上传）。
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class ImPayPasswordServiceImpl implements ImPayPasswordService {

    private final ImPayPasswordMapper payPasswordMapper;
    private final PasswordEncoder passwordEncoder;
    private final SmsCodeApi smsCodeApi;
    private final ImUserService userService;

    @Override
    public boolean hasPassword(Long userId) {
        return payPasswordMapper.selectByUserId(userId) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPassword(Long userId, ImPayPasswordSetReqDTO reqDTO) {
        String password = reqDTO.getPassword();
        if (password == null || password.length() < 6) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_WEAK);
        }
        String hash = passwordEncoder.encode(password);
        ImPayPasswordDO existing = payPasswordMapper.selectByUserId(userId);
        if (existing == null) {
            ImPayPasswordDO po = new ImPayPasswordDO();
            po.setUserId(userId);
            po.setPassword(hash);
            po.setStatus(0);
            po.setGestureEnabled(0);
            po.setFingerprintEnabled(0);
            payPasswordMapper.insert(po);
        } else {
            ImPayPasswordDO po = new ImPayPasswordDO();
            po.setId(existing.getId());
            po.setPassword(hash);
            po.setStatus(0);
            payPasswordMapper.updateById(po);
        }
    }

    @Override
    public void verifyPassword(Long userId, String password) {
        ImPayPasswordDO po = payPasswordMapper.selectByUserId(userId);
        if (po == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_NOT_SET);
        }
        if (!passwordEncoder.matches(password, po.getPassword())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_ERROR);
        }
    }

    // ==================== 重置支付密码（短信验证） ====================

    @Override
    public void sendResetSms(Long userId, String phone) {
        // 校验手机号与账户绑定手机号一致，防止向他人手机号发验证码
        ImUserDO user = userService.getUser(userId);
        if (user == null || !Objects.equals(user.getMobile(), phone)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_RESET_PHONE_MISMATCH);
        }
        smsCodeApi.sendSmsCode(new SmsCodeSendReqDTO()
                .setMobile(phone)
                .setScene(SmsSceneEnum.MEMBER_RESET_PAY_PASSWORD.getScene())
                .setCreateIp(getClientIP())).checkError();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId, ImPayPasswordResetReqDTO reqDTO) {
        // 1. 校验短信验证码（验证手机号归属 + 场景一致性）
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO()
                .setMobile(reqDTO.getPhone())
                .setScene(SmsSceneEnum.MEMBER_RESET_PAY_PASSWORD.getScene())
                .setCode(reqDTO.getCode())
                .setUsedIp(getClientIP())).checkError();

        // 2. 密码强度校验
        String password = reqDTO.getPassword();
        if (password == null || password.length() < 6) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_WEAK);
        }

        // 3. 落库（BCrypt 哈希）
        String hash = passwordEncoder.encode(password);
        ImPayPasswordDO existing = payPasswordMapper.selectByUserId(userId);
        if (existing == null) {
            ImPayPasswordDO po = new ImPayPasswordDO();
            po.setUserId(userId);
            po.setPassword(hash);
            po.setStatus(0);
            po.setGestureEnabled(0);
            po.setFingerprintEnabled(0);
            payPasswordMapper.insert(po);
        } else {
            ImPayPasswordDO po = new ImPayPasswordDO();
            po.setId(existing.getId());
            po.setPassword(hash);
            po.setStatus(0); // 重置后标记正常
            payPasswordMapper.updateById(po);
        }
    }

    // ==================== 手势密码 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setGesturePattern(Long userId, ImPayPasswordGestureSetReqDTO reqDTO) {
        // 必须先设置支付密码
        ImPayPasswordDO existing = payPasswordMapper.selectByUserId(userId);
        if (existing == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_NOT_SET);
        }
        ImPayPasswordDO po = new ImPayPasswordDO();
        po.setId(existing.getId());
        po.setGesturePattern(reqDTO.getPatternHash());
        po.setGestureEnabled(1);
        payPasswordMapper.updateById(po);
    }

    @Override
    public boolean verifyGesturePattern(Long userId, ImPayPasswordGestureVerifyReqDTO reqDTO) {
        ImPayPasswordDO po = payPasswordMapper.selectByUserId(userId);
        if (po == null || !Objects.equals(po.getGestureEnabled(), 1) || po.getGesturePattern() == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GESTURE_PATTERN_NOT_SET);
        }
        return Objects.equals(po.getGesturePattern(), reqDTO.getPatternHash());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearGesturePattern(Long userId) {
        ImPayPasswordDO existing = payPasswordMapper.selectByUserId(userId);
        if (existing == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_NOT_SET);
        }
        ImPayPasswordDO po = new ImPayPasswordDO();
        po.setId(existing.getId());
        po.setGesturePattern(null);
        po.setGestureEnabled(0);
        payPasswordMapper.updateById(po);
    }

    // ==================== 指纹支付 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleFingerprint(Long userId, ImPayPasswordFingerprintToggleReqDTO reqDTO) {
        ImPayPasswordDO existing = payPasswordMapper.selectByUserId(userId);
        if (existing == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PAY_PASSWORD_NOT_SET);
        }
        ImPayPasswordDO po = new ImPayPasswordDO();
        po.setId(existing.getId());
        po.setFingerprintEnabled(reqDTO.getEnabled() ? 1 : 0);
        payPasswordMapper.updateById(po);
    }

    // ==================== 安全状态汇总 ====================

    @Override
    public ImPaySecurityStatusRespDTO getSecurityStatus(Long userId) {
        ImPayPasswordDO po = payPasswordMapper.selectByUserId(userId);
        ImPaySecurityStatusRespDTO resp = new ImPaySecurityStatusRespDTO();
        resp.setHasPayPassword(po != null);
        resp.setGestureEnabled(po != null && Objects.equals(po.getGestureEnabled(), 1));
        resp.setFingerprintEnabled(po != null && Objects.equals(po.getFingerprintEnabled(), 1));
        // 设备指纹能力由客户端 Soter 判定，服务端默认认为支持
        resp.setFingerprintSupported(true);
        return resp;
    }

}
