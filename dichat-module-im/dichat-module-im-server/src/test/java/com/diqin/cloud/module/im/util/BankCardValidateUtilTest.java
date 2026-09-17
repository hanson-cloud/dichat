package com.diqin.cloud.module.im.util;

import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link BankCardValidateUtil} 双重校验工具测试（与前端 common/bankCard.ts 对齐）
 */
class BankCardValidateUtilTest {

    // 已知合法银联测试卡号（Luhn 通过）
    private static final String VALID_CARD = "6217001234567890123";
    private static final String INVALID_LUHN = "6217001234567890124";

    @Test
    void luhnValid() {
        assertTrue(BankCardValidateUtil.luhnValid(VALID_CARD));
        assertTrue(BankCardValidateUtil.luhnValid("6217 0012 3456 7890 123")); // 含空格
        assertFalse(BankCardValidateUtil.luhnValid(INVALID_LUHN));
        assertFalse(BankCardValidateUtil.luhnValid("12345")); // 太短
        assertFalse(BankCardValidateUtil.luhnValid(null));
    }

    @Test
    void idCardValid() {
        // 标准校验位：前 17 位 "11010519491231002" → 末位 X
        assertTrue(BankCardValidateUtil.idCardValid("11010519491231002X"));
        assertTrue(BankCardValidateUtil.idCardValid("110105194912310021")); // 另一合法示例
        assertFalse(BankCardValidateUtil.idCardValid("110105194912310022")); // 校验位错误
        assertFalse(BankCardValidateUtil.idCardValid("12345")); // 格式错误
        assertFalse(BankCardValidateUtil.idCardValid(null));
    }

    @Test
    void detectBankByBin() {
        List<ImBankDO> banks = Arrays.asList(
                new ImBankDO().setBankCode("CMB").setBankName("招商银行").setBinPrefix("621483,622588"),
                new ImBankDO().setBankCode("ICBC").setBankName("工商银行").setBinPrefix("621226,622202")
        );
        ImBankDO hit = BankCardValidateUtil.detectBankByBin("6214831234567890", banks);
        assertNotNull(hit);
        assertEquals("CMB", hit.getBankCode());

        // 最长前缀优先匹配
        ImBankDO best = BankCardValidateUtil.detectBankByBin("6212261234567890", banks);
        assertNotNull(best);
        assertEquals("ICBC", best.getBankCode());

        // 未命中支持银行
        assertNull(BankCardValidateUtil.detectBankByBin("9999001234567890", banks));
        // 卡号不足 6 位
        assertNull(BankCardValidateUtil.detectBankByBin("123", banks));
    }
}
