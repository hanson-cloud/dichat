package com.diqin.cloud.module.im.dal.mysql.conversation;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.conversation.ImConversationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * IM 私聊会话 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImConversationMapper extends BaseMapperX<ImConversationDO> {

    /**
     * 按参与方配对查询会话（uk_pair 唯一）
     *
     * @param aType 参与方A类型
     * @param aId   参与方A编号
     * @param bType 参与方B类型
     * @param bId   参与方B编号
     * @return 会话，不存在返回 null
     */
    default ImConversationDO selectByPair(@Param("aType") Integer aType, @Param("aId") Long aId,
                                          @Param("bType") Integer bType, @Param("bId") Long bId) {
        return selectOne(new LambdaQueryWrapperX<ImConversationDO>()
                .eq(ImConversationDO::getParticipantAType, aType)
                .eq(ImConversationDO::getParticipantAId, aId)
                .eq(ImConversationDO::getParticipantBType, bType)
                .eq(ImConversationDO::getParticipantBId, bId));
    }

    /**
     * 更新会话的最近消息（覆盖式）
     *
     * @param id                 会话编号
     * @param lastMessageId      最近消息编号
     * @param lastMessageContent 最近消息内容摘要
     * @param lastMessageType    最近消息类型
     * @param lastMessageTime    最近消息时间
     */
    default void updateLastMessage(@Param("id") Long id,
                                   @Param("lastMessageId") Long lastMessageId,
                                   @Param("lastMessageContent") String lastMessageContent,
                                   @Param("lastMessageType") Integer lastMessageType,
                                   @Param("lastMessageTime") LocalDateTime lastMessageTime) {
        update(null, new LambdaUpdateWrapper<ImConversationDO>()
                .eq(ImConversationDO::getId, id)
                .set(ImConversationDO::getLastMessageId, lastMessageId)
                .set(ImConversationDO::getLastMessageContent, lastMessageContent)
                .set(ImConversationDO::getLastMessageType, lastMessageType)
                .set(ImConversationDO::getLastMessageTime, lastMessageTime));
    }

    /**
     * 增量某侧未读数（+1）
     *
     * @param id  会话编号
     * @param isA true=参与方A未读+1；false=参与方B未读+1
     */
    default void incrementUnread(@Param("id") Long id, @Param("isA") boolean isA) {
        String sql = isA ? "a_unread_count = a_unread_count + 1" : "b_unread_count = b_unread_count + 1";
        update(null, new LambdaUpdateWrapper<ImConversationDO>()
                .eq(ImConversationDO::getId, id)
                .setSql(sql));
    }

    /**
     * 清零某侧未读数
     *
     * @param id  会话编号
     * @param isA true=清零参与方A；false=清零参与方B
     */
    default void resetUnread(@Param("id") Long id, @Param("isA") boolean isA) {
        update(null, new LambdaUpdateWrapper<ImConversationDO>()
                .eq(ImConversationDO::getId, id)
                .set(isA, ImConversationDO::getAUnreadCount, 0)
                .set(!isA, ImConversationDO::getBUnreadCount, 0));
    }

}
