package com.diqin.cloud.module.im.util;

import cn.hutool.core.util.StrUtil;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;

/**
 * IM 模块敏感词检测工具
 *
 * <p>复用 houbb {@code sensitive-word} 库的默认配置（trie 树 + 全/半角 / 大小写 / 数字风格），
 * <b>注意：本工具使用默认词库，不包含 IM 数据库自定义词库</b>。
 * 业务侧若需数据库自定义词库（管理员后台维护的），请改用 {@code ImSensitiveWordService} 的内部 SensitiveWordBs。
 *
 * <p>用于：注册 / 修改昵称 / 用户名等对实时性不敏感、且期望"零空窗"的基础敏感词检查。
 *
 * @author hanson
 */
public class SensitiveWordUtils {

    private static final SensitiveWordBs DEFAULT_BS = SensitiveWordBs.newInstance().init();

    private SensitiveWordUtils() {
    }

    /**
     * 文本是否包含敏感词
     */
    public static boolean contains(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        return DEFAULT_BS.contains(text);
    }
}
