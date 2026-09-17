package com.diqin.cloud.module.im.service.push;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * UniPush 消息格式转换器
 *
 * <p>
 * 将 IM 内部消息对象转换为 UniPush 通知栏需要的标题/正文/透传数据。
 * 拆分规避免强依赖消息 DTO 类型。
 * </p>
 */
@Component
public class UniPushMessageConverter {

    /**
     * 构造推送标题：优先使用发送者昵称
     */
    public String buildTitle(String senderName) {
        return senderName != null ? senderName : "消息";
    }

    /**
     * 构造推送正文：根据消息类型提取可读内容
     */
    public String buildBody(Integer messageType, String content) {
        if (messageType == null) return content != null ? truncate(content, 50) : "新消息";

        return switch (messageType) {
            case 101 -> truncate(content, 50); // TEXT
            case 102 -> "[图片]";
            case 103 -> "[语音]";
            case 104 -> "[视频]";
            case 105 -> "[文件] " + extractFileName(content);
            case 106 -> "[位置]";
            case 107 -> "[合并转发]";
            case 108 -> "[名片]";
            case 115 -> "[表情]";
            case 126 -> "[红包]";
            case 127 -> "[转账]";
            default -> "新消息";
        };
    }

    /**
     * 构建透传 payload（点击通知栏后传递给应用）
     */
    public String buildPayload(Long convId, Boolean isGroup, Long senderId, String senderName, Integer messageType) {
        Map<String, Object> payload = new java.util.HashMap<>();
        if (convId != null) {
            payload.put("convId", convId);
        }
        if (isGroup != null) {
            payload.put("isGroup", isGroup);
        }
        if (senderId != null) {
            payload.put("senderId", senderId);
        }
        payload.put("action", "chat");

        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
        } catch (Exception e) {
            return "{}";
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }

    private String extractFileName(String content) {
        if (content == null) return "";
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var node = mapper.readTree(content);
            var name = node.get("name");
            return name != null ? truncate(name.asText(), 30) : "文件";
        } catch (Exception e) {
            return "文件";
        }
    }
}
