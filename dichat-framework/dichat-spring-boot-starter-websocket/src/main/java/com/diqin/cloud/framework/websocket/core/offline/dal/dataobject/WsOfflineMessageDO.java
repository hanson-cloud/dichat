package com.diqin.cloud.framework.websocket.core.offline.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.diqin.cloud.framework.mybatis.core.dataobject.BaseDO;
import com.diqin.cloud.framework.tenant.core.aop.TenantIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * WebSocket 离线消息
 *
 * @author hanson
 */
@Data
@Accessors(chain = true)
@TableName(value = "ws_offline_message", autoResultMap = true)
@TenantIgnore
public class WsOfflineMessageDO extends BaseDO {

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否已发送
     */
    private Boolean sent;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;
}
