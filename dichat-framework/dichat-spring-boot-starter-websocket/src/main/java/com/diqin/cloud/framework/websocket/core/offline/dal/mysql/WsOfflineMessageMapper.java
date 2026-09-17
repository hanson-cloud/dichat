package com.diqin.cloud.framework.websocket.core.offline.dal.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.diqin.cloud.framework.websocket.core.offline.dal.dataobject.WsOfflineMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket 离线消息 Mapper
 *
 * @author hanson
 */
@Mapper
public interface WsOfflineMessageMapper extends BaseMapper<WsOfflineMessageDO> {

    /**
     * 拉取用户的离线消息
     *
     * @param tenantId 租户ID
     * @param userType 用户类型
     * @param userId   用户ID
     * @param limit    限制数量
     * @return 离线消息列表
     */
    List<WsOfflineMessageDO> selectByUserAndNotSent(
            @Param("tenantId") Long tenantId,
            @Param("userType") Integer userType,
            @Param("userId") Long userId,
            @Param("limit") Integer limit);

    /**
     * 批量更新消息为已发送
     *
     * @param ids 消息ID列表
     * @param sendTime 发送时间
     * @return 更新数量
     */
    int updateSentByIds(@Param("ids") List<Long> ids, @Param("sendTime") LocalDateTime sendTime);

    /**
     * 增加重试次数
     *
     * @param id 消息ID
     * @return 更新数量
     */
    int incrementRetryCount(@Param("id") Long id);

    /**
     * 清理过期或重试失败的消息
     *
     * @param expireTime 过期时间
     * @param maxRetryCount 最大重试次数
     * @return 清理数量
     */
    int cleanExpiredOrFailedMessages(
            @Param("expireTime") LocalDateTime expireTime,
            @Param("maxRetryCount") Integer maxRetryCount);
}
