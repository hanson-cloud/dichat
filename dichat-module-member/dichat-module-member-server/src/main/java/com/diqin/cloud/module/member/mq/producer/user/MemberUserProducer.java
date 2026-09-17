package com.diqin.cloud.module.member.mq.producer.user;

import com.diqin.cloud.module.member.api.message.user.MemberUserCreateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 会员用户 Producer
 *
 * @author hanson
 */
@Slf4j
@Component
public class MemberUserProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送 {@link MemberUserCreateMessage} 消息
     *
     * @param userId 用户编号
     */
    public void sendUserCreateMessage(Long userId) {
        applicationContext.publishEvent(new MemberUserCreateMessage().setUserId(userId));
    }

}
