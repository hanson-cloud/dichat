package com.diqin.cloud.module.im.mq.producer.user;

import com.diqin.cloud.module.im.api.message.user.ImUserCreateMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 会员用户 Producer
 *
 * @author owen
 */
@Slf4j
@Component
public class ImUserProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送 {@link ImUserCreateMessage} 消息
     *
     * @param userId 用户编号
     */
    public void sendUserCreateMessage(Long userId) {
        applicationContext.publishEvent(new ImUserCreateMessage().setUserId(userId));
    }

}
