package com.diqin.cloud.module.im.service.websocket;

import com.diqin.cloud.module.im.service.websocket.dto.ImChannelMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImGroupMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;

import java.util.Collection;

/**
 * IM WebSocket 异步发送服务
 *
 * <p>
 * 专门负责真正的 WebSocket 推送。
 * 所有方法均由 @Async 执行。
 * ImWebSocketServiceImpl 只负责：
 * 1、事务提交控制
 * 2、调用本服务
 * 本类只负责：
 * 1、异步发送
 * 2、广播
 * 3、异常处理
 * 这样可以彻底避免：
 * ① Spring AOP 自调用失效
 * ② SpringUtil.getBean()
 * ③ getSelf()
 * ④ JDK Proxy / CGLIB 差异
 *
 * @author hanson
 */
public interface ImWebSocketAsyncService {

    /**
     * 发送私聊消息
     *
     * @param userIds 用户ID集合
     * @param dto     消息
     */
    void sendPrivateMessage(Collection<Long> userIds,
                            ImPrivateMessageDTO dto);

    /**
     * 发送群聊消息
     *
     * @param userIds 用户ID集合
     * @param dto     消息
     */
    void sendGroupMessage(Collection<Long> userIds,
                          ImGroupMessageDTO dto);

    /**
     * 发送频道消息
     *
     * @param userIds 用户ID集合
     * @param dto     消息
     */
    void sendChannelMessage(Collection<Long> userIds,
                            ImChannelMessageDTO dto);

    /**
     * 广播频道消息
     *
     * @param dto 消息
     */
    void broadcastChannelMessage(ImChannelMessageDTO dto);

    /**
     * 广播给当前所有在线 C 端用户（MEMBER）；用于全员推送
     * <p>方案 C：人工客服 / 机器人资料变更后通知在线客户端重新拉取。</p>
     *
     * @param dto 消息
     */
    void broadcastToMembers(ImPrivateMessageDTO dto);

}