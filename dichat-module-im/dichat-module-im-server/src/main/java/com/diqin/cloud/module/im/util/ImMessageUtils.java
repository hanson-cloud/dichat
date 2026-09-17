package com.diqin.cloud.module.im.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.util.json.JsonUtils;
import com.diqin.cloud.module.im.enums.message.ImMessageTypeEnum;
import com.diqin.cloud.module.im.service.websocket.dto.message.QuoteMessage;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.MESSAGE_CONTENT_INVALID;

/**
 * IM 消息内容相关工具类
 */
public class ImMessageUtils {

    /**
     * 文本消息最大长度
     */
    private static final int TEXT_MAX_LENGTH = 4096;
    /**
     * URL 最大长度
     */
    private static final int URL_MAX_LENGTH = 2048;
    /**
     * 合并消息最大条数
     */
    private static final int MERGE_MESSAGE_MAX_COUNT = 100;

    /**
     * 校验用户发送的消息内容
     *
     * @param type 消息类型
     * @param content 消息内容
     */
    public static void validateUserMessageContent(Integer type, String content) {
        ImMessageTypeEnum messageType;
        try {
            messageType = ImMessageTypeEnum.validate(type);
        } catch (IllegalArgumentException e) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
        if (!messageType.isNormal()) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
        // TEXT 消息的 content 是纯文本字符串，不是 JSON，直接校验字符串
        if (ImMessageTypeEnum.TEXT == messageType) {
            validateTextContent(content);
            return;
        }
        // 非 TEXT 消息的 content 是 JSON，解析成 Map 后校验各字段
        Map<String, Object> map = JsonUtils.parseMap(content);
        if (map == null) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
        if (ImMessageTypeEnum.IMAGE == messageType || ImMessageTypeEnum.FACE == messageType) {
            validateUrl(getString(map, "url"));
        } else if (ImMessageTypeEnum.VOICE == messageType) {
            validateUrl(getString(map, "url"));
            validatePositiveNumber(map.get("duration"));
        } else if (ImMessageTypeEnum.VIDEO == messageType) {
            validateUrl(getString(map, "url"));
        } else if (ImMessageTypeEnum.FILE == messageType) {
            validateUrl(getString(map, "url"));
            validateNotBlank(getString(map, "name"));
        } else if (ImMessageTypeEnum.CARD == messageType) {
            validateCardContent(map);
        } else if (ImMessageTypeEnum.MERGE == messageType) {
            validateMergeContent(map);
        } else if (ImMessageTypeEnum.MATERIAL == messageType) {
            validateMaterialContent(map);
        }
    }

    private static void validateTextContent(String text) {
        validateNotBlank(text);
        if (text.length() > TEXT_MAX_LENGTH) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
    }

    private static void validateCardContent(Map<String, Object> map) {
        validateNumber(map.get("targetType"));
        validateNumber(map.get("targetId"));
        validateNotBlank(getString(map, "name"));
    }

    private static void validateMergeContent(Map<String, Object> map) {
        validateNotBlank(getString(map, "title"));
        Object messages = map.get("messages");
        if (!(messages instanceof Collection<?> messageList)
                || CollUtil.isEmpty(messageList)
                || messageList.size() > MERGE_MESSAGE_MAX_COUNT) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
    }

    private static void validateMaterialContent(Map<String, Object> map) {
        if (map.get("materialId") == null && StrUtil.isBlank(getString(map, "title"))) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
        validateUrlIfPresent(getString(map, "url"));
        validateUrlIfPresent(getString(map, "coverUrl"));
    }

    private static String getString(Map<String, Object> map, String field) {
        return Convert.toStr(map.get(field), null);
    }

    private static void validateNotBlank(String value) {
        if (StrUtil.isBlank(value)) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
    }

    private static void validateNumber(Object value) {
        if (Convert.toLong(value, null) == null) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
    }

    private static void validatePositiveNumber(Object value) {
        Integer number = Convert.toInt(value, null);
        if (number == null || number < 0) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
    }

    private static void validateUrlIfPresent(String url) {
        if (StrUtil.isBlank(url)) {
            return;
        }
        validateUrl(url);
    }

    private static void validateUrl(String url) {
        validateNotBlank(url);
        if (url.length() > URL_MAX_LENGTH) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
        String lowerUrl = StrUtil.trim(url).toLowerCase();
        if (StrUtil.startWithAny(lowerUrl, "javascript:", "data:", "vbscript:", "file:")) {
            throw exception(MESSAGE_CONTENT_INVALID);
        }
    }

    /**
     * 从 content 解析客户端写入的 quote.messageId
     *
     * @param content 原 content（TEXT 消息为纯字符串，其他类型为 JSON）
     * @return messageId，不存在 / 非法返回 null
     */
    public static Long parseQuoteMessageId(String content) {
        // TEXT 消息 content 是纯字符串，不含 quote
        if (isPlainTextContent(content)) {
            return null;
        }
        Map<String, Object> map = JsonUtils.parseMap(content);
        if (map == null) {
            return null;
        }
        Object quoteRaw = map.get(QuoteMessage.FIELD_NAME);
        if (!(quoteRaw instanceof Map)) {
            return null;
        }
        return Convert.toLong(((Map<?, ?>) quoteRaw).get(QuoteMessage.FIELD_MESSAGE_ID));
    }

    /**
     * 把 quote 写入 content 的 quote 字段
     *
     * @param content 原 content（TEXT 消息为纯字符串，其他类型为 JSON）
     * @param quote  QuoteMessage 对象
     * @return 注入 quote 后的 content（JSON 格式）
     */
    public static String appendQuote(String content, QuoteMessage quote) {
        Map<String, Object> map;
        if (isPlainTextContent(content)) {
            // TEXT 消息 content 是纯字符串，先包装成 { "content": "原文本" }
            map = new LinkedHashMap<>();
            map.put("content", content);
        } else {
            map = JsonUtils.parseMap(content);
            if (map == null) {
                map = new LinkedHashMap<>();
            }
        }
        map.put(QuoteMessage.FIELD_NAME, quote);
        return JsonUtils.toJsonString(map);
    }

    /**
     * 移除 content 里的 quote 字段
     *
     * @param content 原 content（TEXT 消息为纯字符串，其他类型为 JSON）
     * @return remove quote 后的 content（TEXT 消息原样返回）
     */
    public static String removeQuote(String content) {
        if (StrUtil.isBlank(content) || isPlainTextContent(content)) {
            return content;
        }
        if (!StrUtil.contains(content, "\"" + QuoteMessage.FIELD_NAME + "\"")) {
            return content;
        }
        Map<String, Object> map = JsonUtils.parseMap(content);
        if (map == null) {
            return content;
        }
        map.remove(QuoteMessage.FIELD_NAME);
        // 如果移除 quote 后只剩 { "content": "..." }，还原为纯字符串（TEXT 消息）
        if (map.size() == 1 && map.containsKey("content")) {
            return map.get("content").toString();
        }
        return JsonUtils.toJsonString(map);
    }

    /**
     * 判断 content 是否是纯文本（非 JSON Map 格式）—— 即 TEXT 消息的 content
     * <p>
     * TEXT 消息的 content 是纯字符串（如 "你好"、"[媚眼]"），
     * 非 TEXT 消息的 content 是 JSON Map 格式（如 {"url":"...", "width":100}）。
     */
    private static boolean isPlainTextContent(String content) {
        if (StrUtil.isBlank(content)) {
            return false;
        }
        // parseMap 成功 → JSON Map → 非纯文本；失败返回 null → 纯文本或非法内容
        // 注意："[媚眼]" 等含中文的方括号字符串不是合法 JSON，parseMap 返回 null
        return JsonUtils.parseMap(content) == null;
    }

    /**
     * 构建 QuoteMessage 对象
     *
     * @param messageId 被引用消息编号
     * @param senderId 被引用消息发送人编号
     * @param type 被引用消息类型
     * @param originalContent 被引用消息原 content(JSON)，服务端会 removeQuote 防止嵌套
     * @return QuoteMessage 对象
     */
    public static QuoteMessage buildQuote(Long messageId, Long senderId, Integer type, String originalContent) {
        return new QuoteMessage().setMessageId(messageId).setSenderId(senderId).setType(type)
                .setContent(removeQuote(originalContent));
    }

}
