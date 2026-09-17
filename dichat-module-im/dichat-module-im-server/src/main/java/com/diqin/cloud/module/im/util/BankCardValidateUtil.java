package com.diqin.cloud.module.im.util;

import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;

import java.util.List;

/**
 * 银行卡校验工具
 * <p>
 * 与前端 {@code common/bankCard.ts} 逻辑对齐，作为服务端「双重验证」的最后一道防线：
 * 即使前端被绕过（直接调 API），服务端仍校验卡号合法性、身份证校验位与发卡行 BIN。
 *
 * @author dichat
 */
public final class BankCardValidateUtil {

    private BankCardValidateUtil() {
    }

    /**
     * Luhn 算法校验银行卡号（先过滤非数字，要求 16-19 位且校验位合法）
     */
    public static boolean luhnValid(String cardNo) {
        String s = digitsOnly(cardNo);
        if (!s.matches("\\d{16,19}")) {
            return false;
        }
        int sum = 0;
        boolean alt = false;
        for (int i = s.length() - 1; i >= 0; i--) {
            int n = s.charAt(i) - '0';
            if (alt) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alt = !alt;
        }
        return sum % 10 == 0;
    }

    /**
     * 中国大陆 18 位身份证校验（格式 + ISO 7064 mod 11-2 校验位）
     */
    public static boolean idCardValid(String idCard) {
        String s = (idCard == null ? "" : idCard).toUpperCase().trim();
        if (!s.matches("\\d{17}[0-9X]")) {
            return false;
        }
        // 加权因子与校验码映射
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (s.charAt(i) - '0') * weights[i];
        }
        char expected = checkCodes[sum % 11];
        return s.charAt(17) == expected;
    }

    /**
     * 根据卡号前 6（或更长）位匹配银行 BIN，返回命中的银行；未命中返回 null。
     * 逐个银行尝试其 {@code binPrefix}（逗号分隔）前缀集合，取最长匹配优先。
     */
    public static ImBankDO detectBankByBin(String cardNo, List<ImBankDO> banks) {
        String s = digitsOnly(cardNo);
        if (s.length() < 6 || banks == null) {
            return null;
        }
        ImBankDO best = null;
        int bestLen = 0;
        for (ImBankDO bank : banks) {
            String prefixes = bank.getBinPrefix();
            if (prefixes == null || prefixes.isEmpty()) {
                continue;
            }
            for (String raw : prefixes.split(",")) {
                String p = raw.trim();
                if (p.isEmpty() || s.startsWith(p)) {
                    // 优先匹配更长的前缀，降低误命中
                    if (p.length() > bestLen) {
                        best = bank;
                        bestLen = p.length();
                    }
                }
            }
        }
        return best;
    }

    private static String digitsOnly(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

}
