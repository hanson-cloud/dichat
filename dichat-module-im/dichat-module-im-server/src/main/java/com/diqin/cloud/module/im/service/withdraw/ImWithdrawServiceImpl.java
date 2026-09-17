package com.diqin.cloud.module.im.service.withdraw;

import cn.hutool.core.util.IdUtil;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawConfigRespVO;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawManagerPageReqVO;
import com.diqin.cloud.module.im.controller.app.withdraw.vo.AppImWithdrawCreateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bankcard.ImBankCardDO;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawDO;
import com.diqin.cloud.module.im.dal.mysql.bankcard.ImBankCardMapper;
import com.diqin.cloud.module.im.dal.mysql.withdraw.ImWithdrawMapper;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.enums.bankcard.ImBankCardStatusEnum;
import com.diqin.cloud.module.im.enums.withdraw.ImWithdrawStatusEnum;
import com.diqin.cloud.module.im.service.paypassword.ImPayPasswordService;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRespDTO;
import com.diqin.cloud.module.pay.enums.wallet.PayWalletBizTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * IM 提现 Service 实现
 * <p>
 * 余额冻结委托 {@link PayWalletApi}：提现发起即扣减钱包余额（冻结），审核通过则完成，
 * 审核拒绝则退回冻结余额。
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
public class ImWithdrawServiceImpl implements ImWithdrawService {

    @Resource
    private ImWithdrawMapper withdrawMapper;
    @Resource
    private ImBankCardMapper bankCardMapper;
    @Resource
    private PayWalletApi payWalletApi;
    @Resource
    private ImPayPasswordService payPasswordService;
    @Resource
    private ImWithdrawConfigService withdrawConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImWithdrawDO createWithdraw(Long userId, AppImWithdrawCreateReqVO reqVO) {
        // 0. 提现开关（服务端防暴力：置于密码校验之前，关闭时直接拒绝，避免借提现试探支付密码）
        ImWithdrawConfigRespVO config = withdrawConfigService.getConfig();
        boolean enabled = Boolean.TRUE.equals(config.getEnabled());
        if (!enabled) {
            throw exception(ErrorCodeConstants.WITHDRAW_DISABLED);
        }

        // 1. 校验支付密码
        payPasswordService.verifyPassword(userId, reqVO.getPayPassword());
        // 2. 校验银行卡
        ImBankCardDO bankCard = bankCardMapper.selectById(reqVO.getBankCardId());
        if (bankCard == null) {
            throw exception(ErrorCodeConstants.WITHDRAW_NO_BANK_CARD);
        }
        if (!Objects.equals(bankCard.getUserId(), userId)) {
            throw exception(ErrorCodeConstants.BANK_CARD_NOT_OWN);
        }
        if (!ImBankCardStatusEnum.isNormal(bankCard.getStatus())) {
            throw exception(ErrorCodeConstants.BANK_CARD_UNBIND_DENIED);
        }
        // 防重复提交：同一用户已有进行中的提现申请时拒绝新建
        if (withdrawMapper.existsPendingByUserId(userId)) {
            throw exception(ErrorCodeConstants.WITHDRAW_DUPLICATE_PENDING);
        }

        // 3. 预读余额，用于「开启且金额 < 余额」时自动通过审核（金额==余额走人工审核；金额>余额由冻结 CAS 拒绝）
        int balance = Optional.ofNullable(payWalletApi.getOrCreateWallet(userId, UserTypeEnum.MEMBER.getValue())
                .getData()).map(PayWalletRespDTO::getBalance).orElse(0);

        String no = IdUtil.fastSimpleUUID();
        // 自动通过：开启 && 金额 < 余额
        int status = reqVO.getAmount() < balance
                ? ImWithdrawStatusEnum.SUCCESS.getStatus()
                : ImWithdrawStatusEnum.PENDING.getStatus();
        ImWithdrawDO withdraw = ImWithdrawDO.builder()
                .no(no).userId(userId)
                .bankCardId(bankCard.getId())
                .bankName(bankCard.getBankName()).cardNoMask(bankCard.getCardNoMask())
                .amount(reqVO.getAmount()).remark(reqVO.getRemark())
                .status(status)
                .build();
        withdrawMapper.insert(withdraw);

        // 4. 冻结（扣减）钱包余额：CAS 保证 balance >= amount，不足时抛异常并整事务回滚
        boolean frozen;
        try {
            frozen = Boolean.TRUE.equals(payWalletApi.addWalletBalance(
                    buildReq(userId, -reqVO.getAmount(), no)).getData());
        } catch (com.diqin.cloud.framework.common.exception.ServiceException se) {
            // 钱包层（跨模块 RPC）抛出的余额不足等异常，统一收敛为提现余额不足错误码
            frozen = false;
        }
        if (!frozen) {
            withdraw.setStatus(ImWithdrawStatusEnum.FAILED.getStatus());
            withdrawMapper.updateById(withdraw);
            throw exception(ErrorCodeConstants.WITHDRAW_BALANCE_NOT_ENOUGH);
        }
        return withdraw;
    }

    @Override
    public ImWithdrawDO getWithdraw(Long id) {
        return withdrawMapper.selectById(id);
    }

    @Override
    public PageResult<ImWithdrawDO> getWithdrawPageMy(Long userId, PageParam pageReqVO) {
        return withdrawMapper.selectPageByUser(userId, pageReqVO);
    }

    @Override
    public PageResult<ImWithdrawDO> getWithdrawPage(ImWithdrawManagerPageReqVO reqVO) {
        return withdrawMapper.selectPage(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditWithdraw(Long adminId, Long id, boolean approve, String reason) {
        ImWithdrawDO withdraw = withdrawMapper.selectById(id);
        if (withdraw == null) {
            throw exception(ErrorCodeConstants.WITHDRAW_NOT_EXISTS);
        }
        // 原子抢审：仅当记录仍为待审核(pending)时才更新成功。
        // 并发/重复提交时，只有第一个 UPDATE 命中(影响行数=1)，其余返回 0 并抛错，杜绝重复退款。
        ImWithdrawDO updateDO = new ImWithdrawDO();
        updateDO.setAuditUserId(adminId);
        updateDO.setAuditTime(LocalDateTime.now());
        updateDO.setAuditReason(reason);
        updateDO.setStatus(approve ? ImWithdrawStatusEnum.SUCCESS.getStatus()
                : ImWithdrawStatusEnum.FAILED.getStatus());
        int rows = withdrawMapper.update(updateDO,
                new LambdaQueryWrapperX<ImWithdrawDO>()
                        .eq(ImWithdrawDO::getId, id)
                        .eq(ImWithdrawDO::getStatus, ImWithdrawStatusEnum.PENDING.getStatus()));
        if (rows == 0) {
            throw exception(ErrorCodeConstants.WITHDRAW_AUDIT_DENIED);
        }
        if (!approve) {
            // 退款与状态更新同事务：若退款失败整笔回滚（回到 pending，可重新审核），避免重复退款
            payWalletApi.addWalletBalance(buildReq(withdraw.getUserId(), withdraw.getAmount(),
                    withdraw.getNo() + ":refund"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelWithdraw(Long adminId, Long id, String reason) {
        ImWithdrawDO withdraw = withdrawMapper.selectById(id);
        if (withdraw == null) {
            throw exception(ErrorCodeConstants.WITHDRAW_NOT_EXISTS);
        }
        ImWithdrawDO updateDO = new ImWithdrawDO();
        updateDO.setAuditUserId(adminId);
        updateDO.setAuditTime(LocalDateTime.now());
        updateDO.setAuditReason(reason);
        updateDO.setStatus(ImWithdrawStatusEnum.CANCELLED.getStatus());
        int rows = withdrawMapper.update(updateDO,
                new LambdaQueryWrapperX<ImWithdrawDO>()
                        .eq(ImWithdrawDO::getId, id)
                        .eq(ImWithdrawDO::getStatus, ImWithdrawStatusEnum.PENDING.getStatus()));
        if (rows == 0) {
            throw exception(ErrorCodeConstants.WITHDRAW_CANCEL_DENIED);
        }
        // 退回已扣减的钱包余额
        payWalletApi.addWalletBalance(buildReq(withdraw.getUserId(), withdraw.getAmount(),
                withdraw.getNo() + ":cancel"));
    }

    private PayWalletAddBalanceReqDTO buildReq(Long userId, int price, String bizId) {
        return new PayWalletAddBalanceReqDTO()
                .setUserId(userId)
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setBizType(PayWalletBizTypeEnum.WITHDRAW.getType())
                .setBizId(bizId)
                .setPrice(price);
    }

}
