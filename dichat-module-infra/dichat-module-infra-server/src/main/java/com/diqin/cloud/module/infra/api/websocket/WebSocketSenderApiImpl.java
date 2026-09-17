package com.diqin.cloud.module.infra.api.websocket;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.websocket.core.sender.WebSocketMessageSender;
import com.diqin.cloud.module.infra.api.websocket.dto.WebSocketSendReqDTO;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
@AllArgsConstructor
public class WebSocketSenderApiImpl implements WebSocketSenderApi {

    private final WebSocketMessageSender webSocketMessageSender;

    @Override
    public CommonResult<Boolean> send(WebSocketSendReqDTO message) {
        if (StrUtil.isNotEmpty(message.getSessionId())) {
            webSocketMessageSender.send(message.getSessionId(),
                    message.getMessageType(), message.getMessageContent());
        } else if (message.getUserType() != null && message.getUserId() != null) {
            webSocketMessageSender.send(message.getUserType(), message.getUserId(),
                    message.getMessageType(), message.getMessageContent());
        } else if (message.getUserType() != null) {
            webSocketMessageSender.send(message.getUserType(),
                    message.getMessageType(), message.getMessageContent());
        }
        return success(true);
    }

}
