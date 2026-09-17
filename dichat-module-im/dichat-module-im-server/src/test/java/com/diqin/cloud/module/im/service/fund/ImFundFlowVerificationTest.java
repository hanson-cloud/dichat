package com.diqin.cloud.module.im.service.fund;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.test.core.ut.BaseMockitoUnitTest;
import com.diqin.cloud.module.im.config.ImPayProperties;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawConfigRespVO;
import com.diqin.cloud.module.im.controller.app.withdraw.vo.AppImWithdrawCreateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bankcard.ImBankCardDO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketDO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketGrabDO;
import com.diqin.cloud.module.im.dal.dataobject.transfer.ImTransferDO;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawDO;
import com.diqin.cloud.module.im.dal.mysql.bankcard.ImBankCardMapper;
import com.diqin.cloud.module.im.dal.mysql.redpacket.ImRedPacketGrabMapper;
import com.diqin.cloud.module.im.dal.mysql.redpacket.ImRedPacketMapper;
import com.diqin.cloud.module.im.dal.mysql.transfer.ImTransferMapper;
import com.diqin.cloud.module.im.dal.mysql.withdraw.ImWithdrawMapper;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketSendReqDTO;
import com.diqin.cloud.module.im.dto.transfer.ImTransferReqDTO;
import com.diqin.cloud.module.im.enums.ImConversationTypeEnum;
import com.diqin.cloud.module.im.enums.bankcard.ImBankCardStatusEnum;
import com.diqin.cloud.module.im.enums.redpacket.ImRedPacketStatusEnum;
import com.diqin.cloud.module.im.enums.redpacket.ImRedPacketTypeEnum;
import com.diqin.cloud.module.im.enums.transfer.ImTransferStatusEnum;
import com.diqin.cloud.module.im.enums.withdraw.ImWithdrawStatusEnum;
import com.diqin.cloud.module.im.service.message.ImGroupMessageService;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import com.diqin.cloud.module.im.service.message.dto.ImGroupMessageSendDTO;
import com.diqin.cloud.module.im.service.message.dto.ImPrivateMessageSendDTO;
import com.diqin.cloud.module.im.service.paypassword.ImPayPasswordService;
import com.diqin.cloud.module.im.service.redpacket.ImRedPacketServiceImpl;
import com.diqin.cloud.module.im.service.transfer.ImTransferServiceImpl;
import com.diqin.cloud.module.im.service.withdraw.ImWithdrawConfigService;
import com.diqin.cloud.module.im.service.withdraw.ImWithdrawServiceImpl;
import com.diqin.cloud.module.pay.api.wallet.PayWalletApi;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletAddBalanceReqDTO;
import com.diqin.cloud.module.pay.api.wallet.dto.PayWalletRespDTO;
import com.diqin.cloud.module.pay.enums.wallet.PayWalletBizTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.WITHDRAW_BALANCE_NOT_ENOUGH;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.WITHDRAW_DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * IM 社交支付「资金闭环端到端验证」（P0-1 核心交付物）
 * <p>
 * 通过 mock {@link PayWalletApi} 在测试内维护一张余额表，断言四类资金流的
 * <b>余额不变式</b>：每次资金变动都精确落到对应用户、符号正确、且使用正确的业务类型。
 * 同时守护修复后的钱包层 switch（RED_PACKET / TRANSFER_IM / WITHDRAW 必须被处理）。
 *
 * <p>覆盖：
 * <ol>
 *   <li>转账：扣发送方 → 加接收方，双方代数和为 0</li>
 *   <li>红包：发送冻结发送方 → 领取入账接收方，总额守恒</li>
 *   <li>提现：发起扣减 → 审核拒绝退回 / 审核通过保持扣减</li>
 * </ol>
 *
 * @author dichat
 */
// 类级 LENIENT 严格度：setUp() 中的 sendPrivateMessage/sendGroupMessage 等桩为「转账/红包」测试共用，
// 但「提现」等测试用不到，严格模式会将其判为「多余桩」抛 UnnecessaryStubbingException。
// 放宽到 LENIENT 是该共享 setUp 桩模式的标准做法（仅放宽未使用桩检查，不掩盖生产逻辑错误）。
@MockitoSettings(strictness = Strictness.LENIENT)
public class ImFundFlowVerificationTest extends BaseMockitoUnitTest {

    /** 测试内余额表（单位：分）。模拟钱包层 addWalletBalance 的真实记账。 */
    private final Map<Long, Integer> balances = new HashMap<>();

    @InjectMocks
    private ImTransferServiceImpl transferService;
    @InjectMocks
    private ImRedPacketServiceImpl redPacketService;
    @InjectMocks
    private ImWithdrawServiceImpl withdrawService;

    @Mock
    private ImTransferMapper transferMapper;
    @Mock
    private ImRedPacketMapper redPacketMapper;
    @Mock
    private ImRedPacketGrabMapper redPacketGrabMapper;
    @Mock
    private ImWithdrawMapper withdrawMapper;
    @Mock
    private ImBankCardMapper bankCardMapper;
    @Mock
    private ImWithdrawConfigService withdrawConfigService;
    @Mock
    private PayWalletApi payWalletApi;
    @Mock
    private ImPayPasswordService payPasswordService;
    @Mock
    private ImPayProperties payProperties;
    @Mock
    private ImPrivateMessageService privateMessageService;
    @Mock
    private ImGroupMessageService groupMessageService;

    @BeforeEach
    void setUp() {
        balances.clear();
        balances.put(1L, 100000); // 发送方/发红包方/提现方
        balances.put(2L, 0);      // 接收方/抢红包方

        // 钱包层：维护余额表，并断言每次调用都携带正确的业务类型（守护钱包 switch 修复）
        when(payWalletApi.addWalletBalance(any())).thenAnswer(inv -> {
            PayWalletAddBalanceReqDTO req = inv.getArgument(0);
            // null 保护：测试中对 addWalletBalance 重新桩（when(...).thenReturn）时，
            // Mockito 会用 any() 返回的 null 实参先触发本 answer，避免 NPE。
            if (req == null) {
                return CommonResult.success(true);
            }
            balances.merge(req.getUserId(), req.getPrice(), Integer::sum);
            return CommonResult.success(true);
        });

        // 公共 stub
        doNothing().when(payPasswordService).verifyPassword(anyLong(), anyString());
        // sendPrivateMessage/sendGroupMessage 返回 ImPrivateMessageDO（非 void）。
        // 用 doReturn(null) 而非 when().thenReturn(null)：后者在重载方法上会触发 javac 的 thenReturn 重载解析歧义导致编译失败。
        // 这两个桩为转账/红包测试共用，但提现测试用不到；标 lenient，避免 Mockito 严格模式对未使用桩抛 UnnecessaryStubbingException。
        Mockito.lenient().doReturn(null).when(privateMessageService).sendPrivateMessage(anyLong(), any(ImPrivateMessageSendDTO.class));
        Mockito.lenient().doReturn(null).when(groupMessageService).sendGroupMessage(anyLong(), any(ImGroupMessageSendDTO.class));
        when(payProperties.getSingleTransferLimit()).thenReturn(1_000_000L);
        when(payProperties.getDailyTransferLimit()).thenReturn(1_000_000L);
        when(payProperties.getSingleRedPacketLimit()).thenReturn(1_000_000L);
        when(payProperties.getDailyRedPacketLimit()).thenReturn(1_000_000L);

        // 提现开关：默认开启
        when(withdrawConfigService.getConfig()).thenReturn(new ImWithdrawConfigRespVO().setEnabled(true));
        // 钱包余额查询：默认余额 100000（分）
        when(payWalletApi.getOrCreateWallet(anyLong(), anyInt()))
                .thenReturn(CommonResult.success(new PayWalletRespDTO().setBalance(100000)));
    }

    // ========== 1. 转账：扣发送方 → 加接收方 ==========

    @Test
    public void testTransfer_closedLoop_balanceInvariant() {
        // 准备
        when(transferMapper.selectList(any())).thenReturn(Collections.emptyList());
        // BaseMapper.insert/updateById 返回 int（非 void）→ 用 doReturn(1)，绕开 when().thenReturn() 的重载歧义
        doReturn(1).when(transferMapper).insert(any(ImTransferDO.class));
        doReturn(1).when(transferMapper).updateById(any(ImTransferDO.class));

        ImTransferReqDTO req = new ImTransferReqDTO();
        req.setToUserId(2L).setAmount(500L).setRemark("归还借款").setPayPassword("123456");

        // 调用
        ImTransferDO result = transferService.createTransfer(1L, req);

        // 断言：状态成功
        assertEquals(ImTransferStatusEnum.SUCCESS.getStatus(), result.getStatus());
        // 断言：余额不变式 — 发送方 -500，接收方 +500
        assertEquals(99500, balances.get(1L));
        assertEquals(500, balances.get(2L));
        // 断言：钱包层被调用两次，分别为扣款与收款，且使用 TRANSFER_IM
        ArgumentCaptor<PayWalletAddBalanceReqDTO> cap = ArgumentCaptor.forClass(PayWalletAddBalanceReqDTO.class);
        verify(payWalletApi, times(2)).addWalletBalance(cap.capture());
        List<PayWalletAddBalanceReqDTO> calls = cap.getAllValues();
        assertTrue(calls.stream().anyMatch(c -> c.getUserId().equals(1L) && c.getPrice() == -500
                && c.getBizType().equals(PayWalletBizTypeEnum.TRANSFER_IM.getType())), "发送方扣款应为 -500 / TRANSFER_IM");
        assertTrue(calls.stream().anyMatch(c -> c.getUserId().equals(2L) && c.getPrice() == 500
                && c.getBizType().equals(PayWalletBizTypeEnum.TRANSFER_IM.getType())), "接收方收款应为 +500 / TRANSFER_IM");
    }

    // ========== 2. 红包：发送冻结 → 领取入账 ==========

    @Test
    public void testRedPacket_sendAndGrab_balanceInvariant() {
        // 准备：发送
        when(redPacketMapper.selectList(any())).thenReturn(Collections.emptyList());
        doReturn(1).when(redPacketMapper).insert(any(ImRedPacketDO.class));

        ImRedPacketSendReqDTO sendReq = new ImRedPacketSendReqDTO();
        sendReq.setConversationType(ImConversationTypeEnum.PRIVATE.getType())
                .setReceiverUserId(2L).setTotalAmount(1000L).setTotalCount(1)
                .setType(ImRedPacketTypeEnum.NORMAL.getType()).setBlessing("恭喜发财").setPayPassword("123456");

        ImRedPacketDO packet = redPacketService.send(1L, sendReq);
        assertEquals(ImRedPacketStatusEnum.PENDING.getStatus(), packet.getStatus());
        assertEquals(99000, balances.get(1L)); // 发送方冻结 1000

        // 准备：领取
        when(redPacketMapper.selectByNo(packet.getNo())).thenReturn(packet);
        when(redPacketGrabMapper.selectByRedPacketIdAndUserId(packet.getId(), 2L)).thenReturn(null);
        when(redPacketMapper.selectById(packet.getId())).thenReturn(packet);
        when(redPacketMapper.update(any(), any())).thenReturn(1); // 乐观锁 CAS 命中
        doReturn(1).when(redPacketGrabMapper).insert(any(ImRedPacketGrabDO.class));
        doReturn(1).when(redPacketGrabMapper).markBestLuck(anyLong());

        // 调用
        redPacketService.grabRedPacket(2L, packet.getNo());

        // 断言：接收方入账 1000；总额守恒（发送方 99000 + 接收方 1000 = 100000）
        assertEquals(1000, balances.get(2L));
        ArgumentCaptor<ImRedPacketGrabDO> grabCap = ArgumentCaptor.forClass(ImRedPacketGrabDO.class);
        verify(redPacketGrabMapper).insert(grabCap.capture());
        assertEquals(1000, grabCap.getValue().getAmount());
        assertEquals(100000, balances.get(1L) + balances.get(2L));
    }

    // ========== 3. 提现：发起扣减 → 审核拒绝退回 ==========

    @Test
    public void testWithdraw_createDeducts_andRejectRefunds() {
        // 准备：创建提现（预读余额 == 提现金额，走人工审核 PENDING 分支）
        when(payWalletApi.getOrCreateWallet(eq(1L), anyInt()))
                .thenReturn(CommonResult.success(new PayWalletRespDTO().setBalance(300)));
        ImBankCardDO card = ImBankCardDO.builder().id(100L).userId(1L)
                .status(ImBankCardStatusEnum.NORMAL.getStatus()).bankName("ICBC").cardNoMask("****1234").build();
        when(bankCardMapper.selectById(100L)).thenReturn(card);
        when(withdrawMapper.existsPendingByUserId(1L)).thenReturn(false);
        doReturn(1).when(withdrawMapper).insert(any(ImWithdrawDO.class));
        doReturn(1).when(withdrawMapper).updateById(any(ImWithdrawDO.class));

        AppImWithdrawCreateReqVO req = new AppImWithdrawCreateReqVO();
        req.setBankCardId(100L).setAmount(300).setRemark("工资提现").setPayPassword("123456");

        ImWithdrawDO created = withdrawService.createWithdraw(1L, req);
        assertEquals(ImWithdrawStatusEnum.PENDING.getStatus(), created.getStatus());
        assertEquals(99700, balances.get(1L)); // 发起即扣减 300

        // 准备：审核拒绝（CAS 命中）
        ImWithdrawDO pending = ImWithdrawDO.builder().id(created.getId()).userId(1L)
                .amount(300).no(created.getNo()).status(ImWithdrawStatusEnum.PENDING.getStatus()).build();
        when(withdrawMapper.selectById(created.getId())).thenReturn(pending);
        when(withdrawMapper.update(any(), any())).thenReturn(1);

        // 调用：拒绝提现 → 退回
        withdrawService.auditWithdraw(99L, created.getId(), false, "风控拒绝");

        // 断言：余额退回原值
        assertEquals(100000, balances.get(1L));
    }

    // ========== 4. 提现：审核通过保持扣减 ==========

    @Test
    public void testWithdraw_approveKeepsDeducted() {
        // 预读余额 == 提现金额，走人工审核 PENDING 分支
        when(payWalletApi.getOrCreateWallet(eq(1L), anyInt()))
                .thenReturn(CommonResult.success(new PayWalletRespDTO().setBalance(300)));
        ImBankCardDO card = ImBankCardDO.builder().id(100L).userId(1L)
                .status(ImBankCardStatusEnum.NORMAL.getStatus()).bankName("ICBC").cardNoMask("****1234").build();
        when(bankCardMapper.selectById(100L)).thenReturn(card);
        when(withdrawMapper.existsPendingByUserId(1L)).thenReturn(false);
        doReturn(1).when(withdrawMapper).insert(any(ImWithdrawDO.class));
        doReturn(1).when(withdrawMapper).updateById(any(ImWithdrawDO.class));

        AppImWithdrawCreateReqVO req = new AppImWithdrawCreateReqVO();
        req.setBankCardId(100L).setAmount(300).setPayPassword("123456");
        ImWithdrawDO created = withdrawService.createWithdraw(1L, req);
        assertEquals(ImWithdrawStatusEnum.PENDING.getStatus(), created.getStatus());
        assertEquals(99700, balances.get(1L));

        ImWithdrawDO pending = ImWithdrawDO.builder().id(created.getId()).userId(1L)
                .amount(300).no(created.getNo()).status(ImWithdrawStatusEnum.PENDING.getStatus()).build();
        when(withdrawMapper.selectById(created.getId())).thenReturn(pending);
        when(withdrawMapper.update(any(), any())).thenReturn(1);

        // 调用：审核通过 → 不退回
        withdrawService.auditWithdraw(99L, created.getId(), true, "ok");

        // 断言：余额保持扣减（钱已"提出"）
        assertEquals(99700, balances.get(1L));
    }

    // ========== 5. 余额不足：提现应失败且不改动余额 ==========

    @Test
    public void testWithdraw_insufficientBalance_failsWithoutMutation() {
        ImBankCardDO card = ImBankCardDO.builder().id(100L).userId(1L)
                .status(ImBankCardStatusEnum.NORMAL.getStatus()).bankName("ICBC").cardNoMask("****1234").build();
        when(bankCardMapper.selectById(100L)).thenReturn(card);
        when(withdrawMapper.existsPendingByUserId(1L)).thenReturn(false);
        doReturn(1).when(withdrawMapper).insert(any(ImWithdrawDO.class));
        doReturn(1).when(withdrawMapper).updateById(any(ImWithdrawDO.class));
        // 钱包层模拟"余额不足"：返回 false
        when(payWalletApi.addWalletBalance(any())).thenReturn(CommonResult.success(false));

        AppImWithdrawCreateReqVO req = new AppImWithdrawCreateReqVO();
        req.setBankCardId(100L).setAmount(300).setPayPassword("123456");

        // 调用 + 断言：抛余额不足异常
        com.diqin.cloud.framework.common.exception.ServiceException ex = org.junit.jupiter.api.Assertions.assertThrows(
                com.diqin.cloud.framework.common.exception.ServiceException.class,
                () -> withdrawService.createWithdraw(1L, req));
        assertEquals(WITHDRAW_BALANCE_NOT_ENOUGH.code(), ex.getCode());
        // 断言：余额未被改动
        assertEquals(100000, balances.get(1L));
    }

    // ========== 6. 提现：开启且金额 < 余额 → 自动通过审核 ==========

    @Test
    public void testWithdraw_autoApproveWhenEnabledAndAmountLessThanBalance() {
        ImBankCardDO card = ImBankCardDO.builder().id(100L).userId(1L)
                .status(ImBankCardStatusEnum.NORMAL.getStatus()).bankName("ICBC").cardNoMask("****1234").build();
        when(bankCardMapper.selectById(100L)).thenReturn(card);
        when(withdrawMapper.existsPendingByUserId(1L)).thenReturn(false);
        doReturn(1).when(withdrawMapper).insert(any(ImWithdrawDO.class));
        doReturn(1).when(withdrawMapper).updateById(any(ImWithdrawDO.class));

        AppImWithdrawCreateReqVO req = new AppImWithdrawCreateReqVO();
        req.setBankCardId(100L).setAmount(300).setPayPassword("123456");

        // 余额 100000 > 300 → 自动通过（SUCCESS）
        ImWithdrawDO created = withdrawService.createWithdraw(1L, req);
        assertEquals(ImWithdrawStatusEnum.SUCCESS.getStatus(), created.getStatus());
        assertEquals(99700, balances.get(1L)); // 发起即扣减 300，无需人工审核
    }

    // ========== 7. 提现：开关关闭 → 直接拒绝（防暴力绕过前端） ==========

    @Test
    public void testWithdraw_disabledSwitch_rejected() {
        when(withdrawConfigService.getConfig()).thenReturn(new ImWithdrawConfigRespVO().setEnabled(false));

        ImBankCardDO card = ImBankCardDO.builder().id(100L).userId(1L)
                .status(ImBankCardStatusEnum.NORMAL.getStatus()).bankName("ICBC").cardNoMask("****1234").build();
        when(bankCardMapper.selectById(100L)).thenReturn(card);
        doReturn(1).when(withdrawMapper).insert(any(ImWithdrawDO.class));

        AppImWithdrawCreateReqVO req = new AppImWithdrawCreateReqVO();
        req.setBankCardId(100L).setAmount(300).setPayPassword("123456");

        com.diqin.cloud.framework.common.exception.ServiceException ex = org.junit.jupiter.api.Assertions.assertThrows(
                com.diqin.cloud.framework.common.exception.ServiceException.class,
                () -> withdrawService.createWithdraw(1L, req));
        assertEquals(WITHDRAW_DISABLED.code(), ex.getCode());
        // 断言：余额未被改动（开关检查在密码校验之前，未发起任何冻结）
        assertEquals(100000, balances.get(1L));
    }

}
