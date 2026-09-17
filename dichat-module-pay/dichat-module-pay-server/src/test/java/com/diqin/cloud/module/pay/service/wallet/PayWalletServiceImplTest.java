package com.diqin.cloud.module.pay.service.wallet;

import com.diqin.cloud.framework.test.core.ut.BaseMockitoUnitTest;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletDO;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import com.diqin.cloud.module.pay.dal.mysql.wallet.PayWalletMapper;
import com.diqin.cloud.module.pay.dal.redis.wallet.PayWalletLockRedisDAO;
import com.diqin.cloud.module.pay.enums.wallet.PayWalletBizTypeEnum;
import com.diqin.cloud.module.pay.service.wallet.bo.WalletTransactionCreateReqBO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static com.diqin.cloud.module.pay.enums.ErrorCodeConstants.WALLET_BALANCE_NOT_ENOUGH;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link PayWalletServiceImpl} 的单元测试
 * <p>
 * 重点守护「钱包余额变动 switch」：IM 社交支付业务类型
 * （{@link PayWalletBizTypeEnum#RED_PACKET} / {@link PayWalletBizTypeEnum#TRANSFER_IM} /
 * {@link PayWalletBizTypeEnum#WITHDRAW}）必须被处理，否则会抛 UnsupportedOperationException。
 * <p>
 * 该用例即为 P0-1「资金闭环端到端验证」发现并修复的阻断级 bug 的回归守护：
 * 修复前 addWalletBalance 的 switch 仅覆盖 RECHARGE/PAYMENT_REFUND/UPDATE_BALANCE/TRANSFER，
 * 导致转账 / 红包 / 提现 的钱包余额变动全部抛异常、资金一分不动。
 *
 * @author dichat
 */
public class PayWalletServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PayWalletServiceImpl walletService;

    @Mock
    private PayWalletMapper walletMapper;
    @Mock
    private PayWalletLockRedisDAO lockRedisDAO;
    @Mock
    private PayWalletTransactionService walletTransactionService;

    @Test
    public void testAddWalletBalance_transferIm_deductAndRecord() throws Exception {
        // 准备：钱包余额 1000，锁直接执行逻辑，余额变动落流水
        when(lockRedisDAO.lock(anyLong(), anyLong(), any())).thenAnswer(inv -> inv.<Callable<?>>getArgument(2).call());
        when(walletMapper.selectById(1L)).thenReturn(new PayWalletDO().setId(1L).setBalance(1000));
        when(walletMapper.updateWhenAdd(anyLong(), anyInt())).thenReturn(1);
        when(walletTransactionService.createWalletTransaction(any())).thenReturn(new PayWalletTransactionDO());

        // 调用：IM 转账扣款 -500（bizType = TRANSFER_IM）
        walletService.addWalletBalance(1L, "T-1", PayWalletBizTypeEnum.TRANSFER_IM, -500);

        // 断言：余额按负价扣减、且生成一条钱包流水
        verify(walletMapper).updateWhenAdd(eq(1L), eq(-500));
        ArgumentCaptor<WalletTransactionCreateReqBO> boCap = ArgumentCaptor.forClass(WalletTransactionCreateReqBO.class);
        verify(walletTransactionService).createWalletTransaction(boCap.capture());
        assertEquals(-500, boCap.getValue().getPrice());
    }

    @Test
    public void testAddWalletBalance_redPacket_creditAndRecord() throws Exception {
        when(lockRedisDAO.lock(anyLong(), anyLong(), any())).thenAnswer(inv -> inv.<Callable<?>>getArgument(2).call());
        when(walletMapper.selectById(1L)).thenReturn(new PayWalletDO().setId(1L).setBalance(1000));
        when(walletMapper.updateWhenAdd(anyLong(), anyInt())).thenReturn(1);
        when(walletTransactionService.createWalletTransaction(any())).thenReturn(new PayWalletTransactionDO());

        // 调用：红包领取入账 +800（bizType = RED_PACKET）
        walletService.addWalletBalance(1L, "R-1", PayWalletBizTypeEnum.RED_PACKET, 800);

        verify(walletMapper).updateWhenAdd(eq(1L), eq(800));
        verify(walletTransactionService).createWalletTransaction(any());
    }

    @Test
    public void testAddWalletBalance_withdraw_deductAndRecord() throws Exception {
        when(lockRedisDAO.lock(anyLong(), anyLong(), any())).thenAnswer(inv -> inv.<Callable<?>>getArgument(2).call());
        when(walletMapper.selectById(1L)).thenReturn(new PayWalletDO().setId(1L).setBalance(1000));
        when(walletMapper.updateWhenAdd(anyLong(), anyInt())).thenReturn(1);
        when(walletTransactionService.createWalletTransaction(any())).thenReturn(new PayWalletTransactionDO());

        // 调用：提现扣款 -300（bizType = WITHDRAW）
        walletService.addWalletBalance(1L, "W-1", PayWalletBizTypeEnum.WITHDRAW, -300);

        verify(walletMapper).updateWhenAdd(eq(1L), eq(-300));
        verify(walletTransactionService).createWalletTransaction(any());
    }

    @Test
    public void testAddWalletBalance_insufficientBalance_throws() throws Exception {
        when(lockRedisDAO.lock(anyLong(), anyLong(), any())).thenAnswer(inv -> inv.<Callable<?>>getArgument(2).call());
        // 余额仅 100，扣款 300 必然失败
        when(walletMapper.selectById(1L)).thenReturn(new PayWalletDO().setId(1L).setBalance(100));
        // CAS 守卫：余额不足 → 影响行数 0
        when(walletMapper.updateWhenAdd(anyLong(), anyInt())).thenReturn(0);

        // 调用 + 断言：抛余额不足异常，且不写流水
        com.diqin.cloud.framework.common.exception.ServiceException ex = org.junit.jupiter.api.Assertions.assertThrows(
                com.diqin.cloud.framework.common.exception.ServiceException.class,
                () -> walletService.addWalletBalance(1L, "W-2", PayWalletBizTypeEnum.WITHDRAW, -300));
        assertEquals(WALLET_BALANCE_NOT_ENOUGH.code(), ex.getCode());
        verify(walletTransactionService, org.mockito.Mockito.never()).createWalletTransaction(any());
    }

}
