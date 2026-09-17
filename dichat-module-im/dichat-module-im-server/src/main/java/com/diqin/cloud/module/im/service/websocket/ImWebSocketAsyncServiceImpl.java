package com.diqin.cloud.module.im.service.websocket;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.module.im.config.push.UniPushProperties;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.online.ImOnlineService;
import com.diqin.cloud.module.im.service.push.UniPushClient;
import com.diqin.cloud.module.im.service.push.UniPushMessageConverter;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.diqin.cloud.module.im.service.websocket.dto.ImChannelMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImGroupMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * IM WebSocket 异步推送实现
 *
 * <p>
 * 本类只负责真正的消息推送。
 * 所有入口均由 @Async 执行，不参与事务控制。
 * 事务提交控制全部放在 ImWebSocketServiceImpl 中。
 * 这样职责更加清晰：
 * ImWebSocketServiceImpl
 * ↓
 * afterCommit
 * ↓
 * ImWebSocketAsyncService
 * ↓
 * {@code @Async}
 * ↓
 * WebSocketSender
 * ↓
 * UniPush（离线时触发）
 * </p>
 */
@Slf4j
@Service
public class ImWebSocketAsyncServiceImpl implements ImWebSocketAsyncService {

    @Resource
    private WebSocketMessageSender webSocketMessageSender;

    @Resource
    private ImOnlineService onlineService;

    @Resource
    private UniPushMessageConverter pushConverter;

    @Resource
    private UniPushProperties pushProperties;

    /**
     * 离线推送客户端。UniPush 默认关闭（dichat.im.push.unipush.enabled=false），
     * 此时该 bean 不存在，故声明为「可选」注入，避免容器因缺少依赖而无法启动。
     * 关闭状态下 tryPushOnOffline 会提前 return，不会触及此字段。
     */
    @Autowired(required = false)
    private UniPushClient uniPushClient;

    @Resource
    private ImUserService userService;

    // ========== 私聊消息 ==========

    @Override
    @Async
    public void sendPrivateMessage(Collection<Long> userIds,
                                   ImPrivateMessageDTO dto) {
        Set<Long> targets = distinct(userIds);
        if (targets.isEmpty()) return;

        for (Long userId : targets) {
            try {
                webSocketMessageSender.sendObject(
                        UserTypeEnum.MEMBER.getValue(),
                        userId,
                        ImPrivateMessageDTO.TYPE,
                        dto
                );
                // 用户不在线 → 触发 UniPush
                tryPushOnOffline(userId, dto);
            } catch (Exception e) {
                log.error("[WebSocket][Private][发送失败] userId={} dto={}", userId, dto, e);
            }
        }
    }

    // ========== 群聊消息 ==========

    @Override
    @Async
    public void sendGroupMessage(Collection<Long> userIds,
                                 ImGroupMessageDTO dto) {
        Set<Long> targets = distinct(userIds);
        if (targets.isEmpty()) return;

        for (Long userId : targets) {
            try {
                webSocketMessageSender.sendObject(
                        UserTypeEnum.MEMBER.getValue(),
                        userId,
                        ImGroupMessageDTO.TYPE,
                        dto
                );
                tryPushOnOffline(userId, dto);
            } catch (Exception e) {
                log.error("[WebSocket][Group][发送失败] userId={} dto={}", userId, dto, e);
            }
        }
    }

    // ========== 频道消息 ==========

    @Override
    @Async
    public void sendChannelMessage(Collection<Long> userIds,
                                   ImChannelMessageDTO dto) {
        Set<Long> targets = distinct(userIds);
        if (targets.isEmpty()) return;

        for (Long userId : targets) {
            try {
                webSocketMessageSender.sendObject(
                        UserTypeEnum.MEMBER.getValue(),
                        userId,
                        ImChannelMessageDTO.TYPE,
                        dto
                );
            } catch (Exception e) {
                log.error("[WebSocket][Channel][发送失败] userId={} dto={}", userId, dto, e);
            }
        }
    }

    // ========== 广播频道消息 ==========

    @Override
    @Async
    public void broadcastChannelMessage(ImChannelMessageDTO dto) {
        try {
            webSocketMessageSender.sendObject(
                    UserTypeEnum.MEMBER.getValue(),
                    ImChannelMessageDTO.TYPE,
                    dto
            );
        } catch (Exception e) {
            log.error("[WebSocket][Broadcast][广播失败] dto={}", dto, e);
        }
    }

    @Override
    @Async
    public void broadcastToMembers(ImPrivateMessageDTO dto) {
        try {
            webSocketMessageSender.sendObject(
                    UserTypeEnum.MEMBER.getValue(),
                    ImPrivateMessageDTO.TYPE,
                    dto
            );
        } catch (Exception e) {
            log.error("[WebSocket][BroadcastToMembers][广播失败] dto={}", dto, e);
        }
    }

    // ========== 离线推送 ==========

    /**
     * 当用户离线时，通过 UniPush 发送系统通知栏推送。
     * 仅在 {@link UniPushProperties#isEnabled()} 为 true 时生效。
     */
    private void tryPushOnOffline(Long userId, Object dto) {
        if (!pushProperties.isEnabled()) return;
        // 离线推送客户端可能未注入（UniPush 关闭时 bean 不存在），直接跳过
        if (uniPushClient == null) return;
        if (onlineService.isOnline(userId)) return;

        // 提取推送所需字段
        String senderName = extractSenderName(dto);
        Integer messageType = extractMessageType(dto);
        String content = extractContent(dto);
        Long convId = extractConvId(dto);
        Boolean isGroup = dto instanceof ImGroupMessageDTO;

        String title = pushConverter.buildTitle(senderName);
        String body = pushConverter.buildBody(messageType, content);
        String payload = pushConverter.buildPayload(convId, isGroup, null, senderName, messageType);

        try {
            boolean ok = uniPushClient.pushToSingleByAlias(userId, title, body, payload);
            if (ok) {
                log.debug("[UniPush] 离线推送成功 userId={} title={}", userId, title);
            }
        } catch (Exception e) {
            log.error("[UniPush] 离线推送异常 userId={}", userId, e);
        }
    }

    /**
     * 从 DTO 中提取发送者昵称（优先 userService 查询）
     */
    private String extractSenderName(Object dto) {
        Long senderId = null;
        try {
            if (dto instanceof ImPrivateMessageDTO p) {
                senderId = p.getSenderId();
            }
            if (dto instanceof ImGroupMessageDTO g) {
                senderId = g.getSenderId();
            }
        } catch (Exception ignored) {}
        if (senderId != null) {
            try {
                ImUserDO user = userService.getUser(senderId);
                if (user != null) return user.getNickname();
            } catch (Exception ignored) {}
        }
        return "消息";
    }

    /**
     * 从 DTO 中提取消息类型（优先从 messageInner 中取）
     */
    private Integer extractMessageType(Object dto) {
        try {
            if (dto instanceof ImPrivateMessageDTO p) {
                return p.getType();
            }
            if (dto instanceof ImGroupMessageDTO g) {
                return g.getType();
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * 从 DTO 中提取消息文本内容
     */
    private String extractContent(Object dto) {
        try {
            if (dto instanceof ImPrivateMessageDTO p) {
                return p.getContent();
            }
            if (dto instanceof ImGroupMessageDTO g) {
                return g.getContent();
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * 从 DTO 中提取会话 ID
     */
    private Long extractConvId(Object dto) {
        try {
            if (dto instanceof ImPrivateMessageDTO p) {
                return p.getSenderId();
            }
            if (dto instanceof ImGroupMessageDTO g) {
                return g.getGroupId();
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ========== 工具 ==========

    private Set<Long> distinct(Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) return Set.of();
        // 去重并保持顺序，同时过滤 null（避免向 null 用户推送无意义消息）
        Set<Long> set = new LinkedHashSet<>();
        for (Long userId : userIds) {
            if (userId != null) {
                set.add(userId);
            }
        }
        return set;
    }
}