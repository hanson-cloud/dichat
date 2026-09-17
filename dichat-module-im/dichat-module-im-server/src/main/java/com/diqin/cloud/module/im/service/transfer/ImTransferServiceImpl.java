package com.diqin.cloud.module.im.service.transfer;

import cn.hutool.core.util.IdUtil;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.config.ImPayProperties;
import com.diqin.cloud.module.im.controller.admin.transfer.vo.ImTransferManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.transfer.ImTransferDO;
import com.diqin.cloud.module.im.dal.mysql.transfer.ImTransferMapper;
import com.diqin.cloud.module.im.dto.transfer.ImTransferReqDTO;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.enums.transfer.ImTransferStatusEnum;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import com.diqin.cloud.module.im.service.message.dto.ImPrivateMessageSendDTO;
import com.diqin.cloud.module.im.service.paypassword.ImPayPasswordService;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import com.diqin.cloud.module.pay.enums.wallet.PayWalletBizTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * IM 转账 Service 实现
 * <p>
 * 余额变动委托 {@link PayWalletApi}：发送方扣减、接收方增加；失败则补偿回滚并记录失败。
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
public class ImTransferServiceImpl implements ImTransferService {

    @Resource
    private ImTransferMapper transferMapper;
    @Resource
    private PayWalletApi payWalletApi;
    @Resource
    private ImPayPasswordService payPasswordService;
    @Resource
    private ImPayProperties payProperties;
    @Resource
    private ImPrivateMessageService privateMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImTransferDO createTransfer(Long userId, ImTransferReqDTO reqDTO) {
        // 1. 校验支付密码
        payPasswordService.verifyPassword(userId, reqDTO.getPayPassword());
        // 2. 校验基本参数
        Long toUserId = reqDTO.getToUserId();
        int amount = reqDTO.getAmount().intValue();
        if (Objects.equals(userId, toUserId)) {
            throw exception(ErrorCodeConstants.TRANSFER_TO_SELF);
        }
        // 3. 校验限额（单笔 + 日累计）
        validateTransferLimit(userId, amount);
        // 4. 创建转账记录
        String no = IdUtil.fastSimpleUUID();
        ImTransferDO transfer = ImTransferDO.builder()
                .no(no).senderUserId(userId)
                .receiverUserId(toUserId).amount(amount)
                .remark(reqDTO.getRemark()).status(ImTransferStatusEnum.PENDING.getStatus())
                .build();
        transferMapper.insert(transfer);
        // 5. 扣减发送方
        boolean sent = Boolean.TRUE.equals(payWalletApi.addWalletBalance(
                buildReq(userId, -amount, no)).getData());
        if (!sent) {
            transfer.setStatus(ImTransferStatusEnum.FAILED.getStatus());
            transferMapper.updateById(transfer);
            throw exception(ErrorCodeConstants.WITHDRAW_BALANCE_NOT_ENOUGH);
        }
        // 6. 增加接收方；失败则补偿发送方
        try {
            boolean recv = Boolean.TRUE.equals(payWalletApi.addWalletBalance(
                    buildReq(toUserId, amount, no)).getData());
            if (!recv) {
                throw new IllegalStateException("receiver wallet add failed");
            }
        } catch (Exception e) {
            log.warn("[转账] 接收方入账失败，补偿发送方, no={}", no, e);
            payWalletApi.addWalletBalance(buildReq(userId, amount, no + ":compensate"));
            transfer.setStatus(ImTransferStatusEnum.FAILED.getStatus());
            transferMapper.updateById(transfer);
            throw exception(ErrorCodeConstants.TRANSFER_FAILED);
        }
        transfer.setStatus(ImTransferStatusEnum.SUCCESS.getStatus());
        transfer.setPayTime(LocalDateTime.now());
        transferMapper.updateById(transfer);
        // 7. 发送聊天消息（转账气泡），自动入库 + WebSocket 推送
        sendTransferMessage(userId, transfer);
        return transfer;
    }

    /**
     * 校验转账限额（单笔 + 日累计）
     */
    private void validateTransferLimit(Long userId, int amount) {
        // 单笔限额
        if (amount > payProperties.getSingleTransferLimit()) {
            throw exception(ErrorCodeConstants.TRANSFER_SINGLE_LIMIT);
        }
        // 日累计限额：查询今日已转账总额
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<ImTransferDO> todaySent = transferMapper.selectList(new LambdaQueryWrapperX<ImTransferDO>()
                .eq(ImTransferDO::getSenderUserId, userId)
                .ge(ImTransferDO::getCreateTime, todayStart));
        long todayTotal = todaySent.stream().mapToInt(ImTransferDO::getAmount).sum();
        if (todayTotal + amount > payProperties.getDailyTransferLimit()) {
            throw exception(ErrorCodeConstants.TRANSFER_DAILY_LIMIT);
        }
    }

    /**
     * 发送转账聊天消息，自动入库 + WebSocket 推送
     */
    private void sendTransferMessage(Long senderId, ImTransferDO transfer) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("no", transfer.getNo());
        content.put("amount", transfer.getAmount());
        content.put("remark", transfer.getRemark());
        privateMessageService.sendPrivateMessage(senderId,
                new ImPrivateMessageSendDTO()
                        .setReceiverId(transfer.getReceiverUserId())
                        .setType(ImMessageTypeEnum.TRANSFER.getType())
                        .setContent(content));
    }

    @Override
    public ImTransferDO getTransfer(Long id) {
        return transferMapper.selectById(id);
    }

    @Override
    public PageResult<ImTransferDO> getTransferPageMy(Long userId, PageParam pageReqVO) {
        return transferMapper.selectPageByUser(userId, pageReqVO);
    }

    @Override
    public PageResult<ImTransferDO> getTransferPage(ImTransferManagerPageReqVO reqVO) {
        return transferMapper.selectPage(reqVO);
    }

    private PayWalletAddBalanceReqDTO buildReq(Long userId, int price, String bizId) {
        return new PayWalletAddBalanceReqDTO()
                .setUserId(userId)
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setBizType(PayWalletBizTypeEnum.TRANSFER_IM.getType())
                .setBizId(bizId)
                .setPrice(price);
    }

}
