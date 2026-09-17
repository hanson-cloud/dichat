package com.diqin.cloud.module.im.service.bankcard;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.bankcard.vo.ImBankCardManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;
import com.diqin.cloud.module.im.dal.dataobject.bankcard.ImBankCardDO;
import com.diqin.cloud.module.im.dal.mysql.bankcard.ImBankCardMapper;
import com.diqin.cloud.module.im.dto.bankcard.ImBankCardBindReqDTO;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.enums.bankcard.ImBankCardStatusEnum;
import com.diqin.cloud.module.im.service.bank.ImBankService;
import com.diqin.cloud.module.im.util.BankCardValidateUtil;
import com.diqin.cloud.module.system.api.sms.SmsCodeApi;
import com.diqin.cloud.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.diqin.cloud.module.system.enums.sms.SmsSceneEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.diqin.cloud.framework.common.util.servlet.ServletUtils.getClientIP;

/**
 * IM 银行卡 Service 实现
 * <p>
 * 出于安全考虑，卡号 / 证件号仅保留脱敏后的后四位，完整 PAN 不持久化。
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
public class ImBankCardServiceImpl implements ImBankCardService {

    @Resource
    private ImBankCardMapper bankCardMapper;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private ImBankService bankService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImBankCardDO bind(Long userId, ImBankCardBindReqDTO reqDTO) {
        // ========== 卡号检测 / 卡验证（服务端双重校验，防前端被绕过） ==========
        // 1. 卡号 Luhn 校验
        if (!BankCardValidateUtil.luhnValid(reqDTO.getCardNo())) {
            throw exception(ErrorCodeConstants.BANK_CARD_LUHN_INVALID);
        }
        // 2. 身份证校验位（实名校验）
        if (!BankCardValidateUtil.idCardValid(reqDTO.getIdCard())) {
            throw exception(ErrorCodeConstants.BANK_CARD_IDCARD_INVALID);
        }
        // 3. BIN 检测发卡行：必须在支持的银行范围内，且与提交的银行编码一致
        ImBankDO matched = BankCardValidateUtil.detectBankByBin(reqDTO.getCardNo(), bankService.getEnabledBanks());
        if (matched == null) {
            throw exception(ErrorCodeConstants.BANK_CARD_BIN_NOT_SUPPORTED);
        }
        if (!matched.getBankCode().equals(reqDTO.getBankCode())) {
            throw exception(ErrorCodeConstants.BANK_CARD_BIN_MISMATCH);
        }
        String cardNoMask = maskCardNo(reqDTO.getCardNo());
        // 同用户、同银行、同卡号（脱敏）视为重复绑定
        if (bankCardMapper.selectByUserIdAndCard(userId, reqDTO.getBankCode(), cardNoMask) != null) {
            throw exception(ErrorCodeConstants.BANK_CARD_DUPLICATED);
        }
        // 校验短信验证码（验证手机号归属，完成绑卡验证）
        smsCodeApi.useSmsCode(new SmsCodeUseReqDTO()
                .setMobile(reqDTO.getPhone())
                .setScene(SmsSceneEnum.MEMBER_BIND_BANK_CARD.getScene())
                .setCode(reqDTO.getCode())
                .setUsedIp(getClientIP())).checkError();
        boolean hasAny = !bankCardMapper.selectListByUserId(userId).isEmpty();
        ImBankCardDO bankCard = ImBankCardDO.builder()
                .userId(userId)
                .cardNoMask(cardNoMask)
                .bankName(matched.getBankName())
                .bankCode(reqDTO.getBankCode())
                .cardholder(reqDTO.getHolderName())
                .idCardMask(maskIdCard(reqDTO.getIdCard()))
                .phone(reqDTO.getPhone())
                .type(reqDTO.getCardType())
                .status(ImBankCardStatusEnum.NORMAL.getStatus())
                .isDefault(!hasAny) // 首张卡默认
                .bindTime(LocalDateTime.now())
                .build();
        bankCardMapper.insert(bankCard);
        return bankCard;
    }

    @Override
    public List<ImBankCardDO> getMyBankCards(Long userId) {
        return bankCardMapper.selectListByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long id) {
        ImBankCardDO bankCard = bankCardMapper.selectById(id);
        if (bankCard == null) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_EXISTS);
        }
        if (!Objects.equals(bankCard.getUserId(), userId)) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_OWN);
        }
        if (!ImBankCardStatusEnum.isNormal(bankCard.getStatus())) {
            throw exception(ErrorCodeConstants.BANK_CARD_UNBIND_DENIED);
        }
        bankCardMapper.update(ImBankCardDO.builder().isDefault(Boolean.FALSE).build(),
                Wrappers.<ImBankCardDO>lambdaUpdate()
                        .eq(ImBankCardDO::getUserId, userId)
                        .eq(ImBankCardDO::getIsDefault, Boolean.TRUE));
        bankCard.setIsDefault(Boolean.TRUE);
        bankCardMapper.updateById(bankCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBankCard(Long userId, Long id) {
        ImBankCardDO bankCard = bankCardMapper.selectById(id);
        if (bankCard == null) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_EXISTS);
        }
        if (!Objects.equals(bankCard.getUserId(), userId)) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_OWN);
        }
        bankCard.setStatus(ImBankCardStatusEnum.UNBIND.getStatus());
        bankCardMapper.updateById(bankCard);
    }

    @Override
    public ImBankCardDO getBankCard(Long id) {
        return bankCardMapper.selectById(id);
    }

    @Override
    public PageResult<ImBankCardDO> getBankCardPage(ImBankCardManagerPageReqVO reqVO) {
        return bankCardMapper.selectPage(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditBankCard(Long adminId, Long id, boolean approve, String reason) {
        ImBankCardDO bankCard = bankCardMapper.selectById(id);
        if (bankCard == null) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_EXISTS);
        }
        if (!ImBankCardStatusEnum.isPending(bankCard.getStatus())) {
            throw exception(ErrorCodeConstants.BANK_CARD_AUDIT_DENIED);
        }
        bankCard.setStatus(approve ? ImBankCardStatusEnum.NORMAL.getStatus() : ImBankCardStatusEnum.FROZEN.getStatus());
        bankCard.setAuditUserId(adminId);
        bankCard.setAuditTime(LocalDateTime.now());
        bankCard.setAuditReason(reason);
        bankCardMapper.updateById(bankCard);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBankCardAdmin(Long id) {
        ImBankCardDO bankCard = bankCardMapper.selectById(id);
        if (bankCard == null) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_EXISTS);
        }
        bankCard.setStatus(ImBankCardStatusEnum.UNBIND.getStatus());
        bankCardMapper.updateById(bankCard);
    }

    private String maskCardNo(String full) {
        if (ObjUtil.isEmpty(full) || full.length() < 4) {
            return "****";
        }
        return "****" + full.substring(full.length() - 4);
    }

    private String maskIdCard(String full) {
        if (ObjUtil.isEmpty(full) || full.length() < 4) {
            return "****";
        }
        return "****" + full.substring(full.length() - 4);
    }

}
