package com.diqin.cloud.module.im.service.customer_service.event;

import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import lombok.Getter;

/**
 * 客服相关私聊消息事件
 * <p>在消息落库后由 {@code ImPrivateMessageServiceImpl} 发布，供管理端客服工作台的 SSE 实时推送使用。
 * 仅当消息的发送方或接收方为人工客服（CS）时才发布。</p>
 *
 * @author 速构构
 */
@Getter
public class CsMessageEvent {

    /**
     * 客服编号（im_customer_service.id）—— SSE 订阅按此维度路由
     */
    private final Long csId;

    /**
     * 客服绑定的 IM 用户编号（im_users.id）—— 用于双 id 维度匹配
     */
    private final Long boundUserId;

    /**
     * 落库后的消息
     */
    private final ImPrivateMessageDO message;

    public CsMessageEvent(Long csId, Long boundUserId, ImPrivateMessageDO message) {
        this.csId = csId;
        this.boundUserId = boundUserId;
        this.message = message;
    }

}
