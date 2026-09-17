package com.diqin.cloud.framework.websocket.core.offline.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.diqin.cloud.framework.websocket.core.offline.dal.dataobject.WsOfflineMessageDO;

import java.util.List;

/**
 * 离线消息服务接口
 *
 * @author hanson
 */
public interface WsOfflineMessageService extends IService<WsOfflineMessageDO> {

    /**
     * 拉取用户的离线消息
     *
     * @param tenantId 租户ID
     * @param userType 用户类型
     * @param userId   用户ID
     * @param limit    限制数量
     * @return 成功发送的消息数量
     */
    List<WsOfflineMessageDO> pullAndSendOfflineMessages(Long tenantId, Integer userType, Long userId, Integer limit);

    /**
     * 清理过期或重试失败的消息
     */
    void cleanExpiredMessages();

    void incrementRetryCount(Long id);

    void updateSentByIds(List<Long> successIds);
}
