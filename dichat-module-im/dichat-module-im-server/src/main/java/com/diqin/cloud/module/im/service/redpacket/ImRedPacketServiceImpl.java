package com.diqin.cloud.module.im.service.redpacket;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.config.ImPayProperties;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketRefundStatusRespVO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketDO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketGrabDO;
import com.diqin.cloud.module.im.dal.mysql.redpacket.ImRedPacketGrabMapper;
import com.diqin.cloud.module.im.dal.mysql.redpacket.ImRedPacketMapper;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketSendReqDTO;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.enums.ImConversationTypeEnum;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.enums.redpacket.ImRedPacketStatusEnum;
import com.diqin.cloud.module.im.enums.redpacket.ImRedPacketTypeEnum;
import com.diqin.cloud.module.im.service.message.ImGroupMessageService;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import com.diqin.cloud.module.im.service.message.dto.ImGroupMessageSendDTO;
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
import java.util.*;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * IM 红包 Service 实现
 * <p>
 * 钱包余额委托 {@link PayWalletApi}：发送时冻结发送方、领取时解冻到领取方、退款时退回发送方。
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
public class ImRedPacketServiceImpl implements ImRedPacketService {

    private static final Random RANDOM = new Random();
    /**
     * 红包默认过期时长（小时）
     */
    private static final int EXPIRE_HOURS = 24;

    @Resource
    private ImRedPacketMapper redPacketMapper;
    @Resource
    private ImRedPacketGrabMapper redPacketGrabMapper;
    @Resource
    private PayWalletApi payWalletApi;
    @Resource
    private ImPayPasswordService payPasswordService;
    @Resource
    private ImPayProperties payProperties;
    @Resource
    private ImPrivateMessageService privateMessageService;
    @Resource
    private ImGroupMessageService groupMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImRedPacketDO send(Long userId, ImRedPacketSendReqDTO reqDTO) {
        // 1. 校验支付密码
        payPasswordService.verifyPassword(userId, reqDTO.getPayPassword());
        // 2. 校验限额（单笔 + 日累计）
        int totalAmount = reqDTO.getTotalAmount().intValue();
        int totalCount = reqDTO.getTotalCount();
        validateRedPacketLimit(userId, totalAmount);
        // 3. 冻结发送方钱包余额（负值表示扣减）
        boolean isPrivate = ImConversationTypeEnum.isPrivate(reqDTO.getConversationType());
        String no = IdUtil.fastSimpleUUID();
        boolean deducted = Boolean.TRUE.equals(payWalletApi.addWalletBalance(buildAddBalanceReq(userId,
                -totalAmount, no)).getData());
        if (!deducted) {
            throw exception(ErrorCodeConstants.WITHDRAW_BALANCE_NOT_ENOUGH);
        }
        // 4. 创建红包记录
        ImRedPacketDO redPacket = ImRedPacketDO.builder()
                .no(no).senderUserId(userId)
                .conversationType(reqDTO.getConversationType())
                .receiverUserId(isPrivate ? reqDTO.getReceiverUserId() : null)
                .groupId(isPrivate ? null : reqDTO.getGroupId())
                .type(reqDTO.getType())
                .totalAmount(totalAmount).totalCount(totalCount)
                .remainAmount(totalAmount).remainCount(totalCount)
                .greeting(reqDTO.getBlessing())
                .status(ImRedPacketStatusEnum.PENDING.getStatus())
                .expireTime(LocalDateTime.now().plusHours(EXPIRE_HOURS))
                .grabbedAmount(0).grabbedCount(0)
                .build();
        redPacketMapper.insert(redPacket);
        // 5. 发送聊天消息（红包气泡），自动入库 + WebSocket 推送
        sendRedPacketMessage(userId, redPacket, isPrivate);
        return redPacket;
    }

    /**
     * 校验红包限额（单笔 + 日累计）
     */
    private void validateRedPacketLimit(Long userId, int totalAmount) {
        // 单笔限额
        if (totalAmount > payProperties.getSingleRedPacketLimit()) {
            throw exception(ErrorCodeConstants.RED_PACKET_SINGLE_LIMIT);
        }
        // 日累计限额：查询今日已发红包总额
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<ImRedPacketDO> todaySent = redPacketMapper.selectList(new LambdaQueryWrapperX<ImRedPacketDO>()
                .eq(ImRedPacketDO::getSenderUserId, userId)
                .ge(ImRedPacketDO::getCreateTime, todayStart));
        long todayTotal = todaySent.stream().mapToLong(ImRedPacketDO::getTotalAmount).sum();
        if (todayTotal + totalAmount > payProperties.getDailyRedPacketLimit()) {
            throw exception(ErrorCodeConstants.RED_PACKET_DAILY_LIMIT);
        }
    }

    /**
     * 发送红包聊天消息（私聊 or 群聊），自动入库 + WebSocket 推送
     */
    private void sendRedPacketMessage(Long senderId, ImRedPacketDO redPacket, boolean isPrivate) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("no", redPacket.getNo());
        content.put("type", redPacket.getType());
        content.put("totalAmount", redPacket.getTotalAmount());
        content.put("totalCount", redPacket.getTotalCount());
        content.put("blessing", redPacket.getGreeting());
        if (isPrivate) {
            privateMessageService.sendPrivateMessage(senderId,
                    new ImPrivateMessageSendDTO()
                            .setReceiverId(redPacket.getReceiverUserId())
                            .setType(ImMessageTypeEnum.RED_PACKET.getType())
                            .setContent(content));
        } else {
            groupMessageService.sendGroupMessage(senderId,
                    new ImGroupMessageSendDTO()
                            .setGroupId(redPacket.getGroupId())
                            .setType(ImMessageTypeEnum.RED_PACKET.getType())
                            .setContent(content));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImRedPacketGrabDO grabRedPacket(Long userId, String no) {
        ImRedPacketDO redPacket = redPacketMapper.selectByNo(no);
        if (redPacket == null) {
            throw exception(ErrorCodeConstants.RED_PACKET_NOT_EXISTS);
        }
        if (!ImRedPacketStatusEnum.isPending(redPacket.getStatus())) {
            throw exception(ErrorCodeConstants.RED_PACKET_FINISHED);
        }
        boolean isPrivate = ImConversationTypeEnum.isPrivate(redPacket.getConversationType());
        if (isPrivate && !Objects.equals(redPacket.getReceiverUserId(), userId)) {
            throw exception(ErrorCodeConstants.RED_PACKET_NOT_RECEIVER);
        }
        if (redPacketGrabMapper.selectByRedPacketIdAndUserId(redPacket.getId(), userId) != null) {
            throw exception(ErrorCodeConstants.RED_PACKET_ALREADY_GRABBED);
        }
        int amount = doGrab(redPacket, userId, isPrivate);
        // 领取方钱包入账
        try {
            payWalletApi.addWalletBalance(buildAddBalanceReq(userId, amount, no + ":" + userId));
        } catch (Exception e) {
            log.error("[红包] 领取方钱包入账失败, redPacketNo={}, userId={}", no, userId, e);
        }
        return redPacketGrabMapper.selectByRedPacketIdAndUserId(redPacket.getId(), userId);
    }

    /**
     * 原子领取：自旋重试直至抢到份额或失败；返回本次领取金额
     */
    private int doGrab(ImRedPacketDO redPacket, Long userId, boolean isPrivate) {
        for (int i = 0; i < 5; i++) {
            ImRedPacketDO current = redPacketMapper.selectById(redPacket.getId());
            if (current == null || !ImRedPacketStatusEnum.isPending(current.getStatus())) {
                throw exception(ErrorCodeConstants.RED_PACKET_FINISHED);
            }
            int remainAmount = current.getRemainAmount();
            int remainCount = current.getRemainCount();
            int amount;
            if (isPrivate) {
                amount = remainAmount;
            } else if (Objects.equals(current.getType(), ImRedPacketTypeEnum.NORMAL.getType())) {
                amount = remainCount == 1 ? remainAmount : remainAmount / remainCount;
            } else {
                amount = remainCount == 1 ? remainAmount : RANDOM.nextInt(remainAmount - (remainCount - 1)) + 1;
            }
            ImRedPacketDO updateObj = ImRedPacketDO.builder()
                    .remainAmount(remainAmount - amount)
                    .remainCount(remainCount - 1)
                    .grabbedAmount(current.getGrabbedAmount() + amount)
                    .grabbedCount(current.getGrabbedCount() + 1)
                    .build();
            int affected = redPacketMapper.update(updateObj, Wrappers.<ImRedPacketDO>lambdaUpdate()
                    .eq(ImRedPacketDO::getId, current.getId())
                    .eq(ImRedPacketDO::getStatus, ImRedPacketStatusEnum.PENDING.getStatus())
                    .gt(ImRedPacketDO::getRemainCount, 0)
                    .ge(ImRedPacketDO::getRemainAmount, amount));
            if (affected > 0) {
                ImRedPacketGrabDO grab = ImRedPacketGrabDO.builder()
                        .redPacketId(current.getId()).redPacketNo(current.getNo())
                        .userId(userId).amount(amount).isBestLuck(Boolean.FALSE)
                        .grabTime(LocalDateTime.now()).build();
                redPacketGrabMapper.insert(grab);
                if (remainCount - 1 == 0) {
                    redPacketMapper.update(ImRedPacketDO.builder()
                                    .status(ImRedPacketStatusEnum.GRABBED_OUT.getStatus()).build(),
                            Wrappers.<ImRedPacketDO>lambdaUpdate().eq(ImRedPacketDO::getId, current.getId()));
                    redPacketGrabMapper.markBestLuck(current.getId());
                }
                return amount;
            }
        }
        throw exception(ErrorCodeConstants.RED_PACKET_GRAB_FAILED);
    }

    @Override
    public ImRedPacketDO getRedPacketByNo(String no) {
        return redPacketMapper.selectByNo(no);
    }

    @Override
    public ImRedPacketGrabDO getMyGrab(String no, Long userId) {
        ImRedPacketDO redPacket = redPacketMapper.selectByNo(no);
        if (redPacket == null) {
            return null;
        }
        return redPacketGrabMapper.selectByRedPacketIdAndUserId(redPacket.getId(), userId);
    }

    @Override
    public PageResult<ImRedPacketDO> getRedPacketPageMySent(Long userId, PageParam pageReqVO) {
        return redPacketMapper.selectPageBySender(userId, pageReqVO);
    }

    @Override
    public PageResult<ImRedPacketDO> getRedPacketPageMyGrabbed(Long userId, PageParam pageReqVO) {
        List<ImRedPacketGrabDO> grabs = redPacketGrabMapper.selectList(ImRedPacketGrabDO::getUserId, userId);
        if (CollectionUtils.isAnyEmpty(grabs)) {
            return PageResult.empty();
        }
        Set<Long> ids = CollectionUtils.convertSet(grabs, ImRedPacketGrabDO::getRedPacketId);
        List<ImRedPacketDO> all = redPacketMapper.selectList(new LambdaQueryWrapperX<ImRedPacketDO>()
                .in(ImRedPacketDO::getId, ids).orderByDesc(ImRedPacketDO::getId));
        return pageByList(all, pageReqVO);
    }

    @Override
    public ImRedPacketDO getRedPacket(Long id) {
        return redPacketMapper.selectById(id);
    }

    @Override
    public PageResult<ImRedPacketDO> getRedPacketPage(ImRedPacketManagerPageReqVO reqVO) {
        return redPacketMapper.selectPage(reqVO);
    }

    @Override
    public List<ImRedPacketGrabDO> getGrabList(Long redPacketId) {
        return redPacketGrabMapper.selectListByRedPacketId(redPacketId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundRedPacket(Long id) {
        ImRedPacketDO redPacket = redPacketMapper.selectById(id);
        if (redPacket == null) {
            throw exception(ErrorCodeConstants.RED_PACKET_NOT_EXISTS);
        }
        if (!ImRedPacketStatusEnum.isExpired(redPacket.getStatus()) || redPacket.getRemainCount() == 0) {
            throw exception(ErrorCodeConstants.RED_PACKET_REFUND_DENIED);
        }
        // 退回剩余金额给发送方
        payWalletApi.addWalletBalance(buildAddBalanceReq(redPacket.getSenderUserId(),
                redPacket.getRemainAmount(), redPacket.getNo() + ":refund"));
        redPacket.setStatus(ImRedPacketStatusEnum.REFUNDED.getStatus());
        redPacket.setRemainAmount(0);
        redPacket.setRemainCount(0);
        redPacketMapper.updateById(redPacket);
    }

    @Override
    @Transactional
    public int refundExpiredPackets() {
        List<ImRedPacketDO> expired = redPacketMapper.selectList(new LambdaQueryWrapperX<ImRedPacketDO>()
                .eq(ImRedPacketDO::getStatus, ImRedPacketStatusEnum.PENDING.getStatus())
                .gt(ImRedPacketDO::getRemainCount, 0)
                .lt(ImRedPacketDO::getExpireTime, LocalDateTime.now()));
        int count = 0;
        for (ImRedPacketDO rp : expired) {
            try {
                int rows = redPacketMapper.update(ImRedPacketDO.builder()
                                .status(ImRedPacketStatusEnum.EXPIRED.getStatus()).build(),
                        Wrappers.<ImRedPacketDO>lambdaUpdate()
                                .eq(ImRedPacketDO::getId, rp.getId())
                                .eq(ImRedPacketDO::getStatus, ImRedPacketStatusEnum.PENDING.getStatus()));
                if (rows > 0) {
                    refundRedPacket(rp.getId());
                    count++;
                }
            } catch (Exception e) {
                log.error("[红包] 超时自动退款失败, redPacketId={}", rp.getId(), e);
            }
        }
        return count;
    }

    @Override
    public ImRedPacketRefundStatusRespVO getRedPacketRefundStatus(Long id) {
        ImRedPacketDO redPacket = redPacketMapper.selectById(id);
        if (redPacket == null) {
            throw exception(ErrorCodeConstants.RED_PACKET_NOT_EXISTS);
        }
        ImRedPacketRefundStatusRespVO resp = new ImRedPacketRefundStatusRespVO();
        resp.setId(redPacket.getId());
        resp.setStatus(redPacket.getStatus());
        int refundStatus;
        String refundStatusName;
        Integer refundAmount;
        if (ImRedPacketStatusEnum.isRefunded(redPacket.getStatus())) {
            refundStatus = 10;
            refundStatusName = "已退款成功";
            refundAmount = 0;
        } else if (ImRedPacketStatusEnum.isExpired(redPacket.getStatus())) {
            refundStatus = 0;
            refundStatusName = "待退款";
            refundAmount = redPacket.getRemainAmount();
        } else {
            refundStatus = -1;
            refundStatusName = "无需退款";
            refundAmount = 0;
        }
        resp.setRefundStatus(refundStatus);
        resp.setRefundStatusName(refundStatusName);
        resp.setRefundAmount(refundAmount);
        return resp;
    }

    private PayWalletAddBalanceReqDTO buildAddBalanceReq(Long userId, int price, String bizId) {
        return new PayWalletAddBalanceReqDTO()
                .setUserId(userId)
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setBizType(PayWalletBizTypeEnum.RED_PACKET.getType())
                .setBizId(bizId)
                .setPrice(price);
    }

    private <T> PageResult<T> pageByList(List<T> all, PageParam pageReqVO) {
        int total = all.size();
        int from = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        if (from < 0) {
            from = 0;
        }
        int to = Math.min(from + pageReqVO.getPageSize(), total);
        List<T> sub = from >= total ? Collections.emptyList() : all.subList(from, to);
        return new PageResult<>(sub, (long) total);
    }

}
