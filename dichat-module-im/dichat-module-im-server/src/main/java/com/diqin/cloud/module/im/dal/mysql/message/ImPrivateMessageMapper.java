package com.diqin.cloud.module.im.dal.mysql.message;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.framework.mybatis.core.query.QueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.message.vo.privates.ImPrivateMessageManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 私聊消息 Mapper
 *
 * @author hanson
 */
@Mapper
public interface ImPrivateMessageMapper extends BaseMapperX<ImPrivateMessageDO> {

    /**
     * 根据 minId + 时间窗口增量拉取私聊消息
     *
     * @param userId      当前用户编号
     * @param minId       最小消息 id（不含）
     * @param minSendTime 最早发送时间（不含），限制离线消息时间窗口
     * @param size        拉取数量
     * @return 消息列表
     */
    default List<ImPrivateMessageDO> selectListByMinId(Long userId, Long minId,
                                                       LocalDateTime minSendTime, Integer size) {
        QueryWrapperX<ImPrivateMessageDO> wrapper = new QueryWrapperX<>();
        wrapper.and(w -> w.eq("sender_id", userId)
                        .or()
                        .eq("receiver_id", userId))
                .gt("id", minId)
                .gt("send_time", minSendTime)
                .orderByAsc("id");
        wrapper.limitN(size);
        return selectList(wrapper);
    }

    /**
     * 查询私聊历史消息（游标拉取）
     *
     * @param userId     当前用户编号
     * @param receiverId 对方用户编号
     * @param maxId      起始消息 id（不含），为空则从最新开始
     * @param limit      拉取数量
     * @return 消息列表（按 id 倒序）
     */
    default List<ImPrivateMessageDO> selectHistoryList(Long userId, Long receiverId, Long maxId, Integer limit) {
        QueryWrapperX<ImPrivateMessageDO> wrapper = new QueryWrapperX<>();
        wrapper.and(w -> w.eq("sender_id", userId).eq("receiver_id", receiverId)
                        .or()
                        .eq("sender_id", receiverId).eq("receiver_id", userId))
                .lt(maxId != null, "id", maxId)
                .orderByDesc("id");
        wrapper.limitN(limit);
        return selectList(wrapper);
    }

    /**
     * 查询私聊历史消息（游标拉取，按参与方多 id 维度匹配）
     * <p>方案 C：人工客服 / 机器人作为独立参与方，私聊里既可能出现其「绑定的 im_users.id」（用户 → 客服 / 机器人方向），
     * 也可能出现其「自身 id」（客服 / 机器人 → 用户方向）。{@code peerIds} 同时携带这两种 id，
     * 任一命中即视为同一会话，避免客服 / 机器人回复在历史里丢失。</p>
     *
     * @param userId   当前用户编号
     * @param peerIds  对方参与方的全部候选编号（普通用户仅 1 个；客服 / 机器人含 绑定 im_users.id + 自身 id）
     * @param maxId    起始消息 id（不含），为空则从最新开始
     * @param limit    拉取数量
     * @return 消息列表（按 id 倒序）
     */
    default List<ImPrivateMessageDO> selectHistoryListByPeerIds(Long userId, List<Long> peerIds, Long maxId, Integer limit) {
        if (CollUtil.isEmpty(peerIds)) {
            return List.of();
        }
        QueryWrapperX<ImPrivateMessageDO> wrapper = new QueryWrapperX<>();
        wrapper.and(w -> w.eq("sender_id", userId).in("receiver_id", peerIds)
                        .or()
                        .in("sender_id", peerIds).eq("receiver_id", userId))
                .lt(maxId != null, "id", maxId)
                .orderByDesc("id");
        wrapper.limitN(limit);
        return selectList(wrapper);
    }

    default ImPrivateMessageDO selectBySenderIdAndClientMessageId(Integer senderType, Long senderId, String clientMessageId) {
        return selectOne(new LambdaQueryWrapperX<ImPrivateMessageDO>()
                .eq(ImPrivateMessageDO::getSenderType, senderType)
                .eq(ImPrivateMessageDO::getSenderId, senderId)
                .eq(ImPrivateMessageDO::getClientMessageId, clientMessageId));
    }

    default int updateBySenderIdAndReceiverIdAndIdLeAndStatus(Long senderId, Long receiverId, Long maxMessageId,
                                                              Integer whereStatus, ImPrivateMessageDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<ImPrivateMessageDO>()
                .eq(ImPrivateMessageDO::getSenderId, senderId)
                .eq(ImPrivateMessageDO::getReceiverId, receiverId)
                .le(ImPrivateMessageDO::getId, maxMessageId)
                .eq(ImPrivateMessageDO::getStatus, whereStatus));
    }

    /**
     * 物理删除两个参与方之间（双向）的所有私聊消息（按对方多 id 维度匹配）
     * <p>方案 C：客服 / 机器人参与方存在「绑定 im_users.id」与「自身 id」两种编号，{@code peerIds} 同时携带，
     * 任一命中即视为同一会话，确保清空会话时不漏删客服 / 机器人方向的消息。</p>
     *
     * @param userId   当前用户编号
     * @param peerIds  对方参与方的全部候选编号
     * @return 实际删除条数
     */
    default int deleteByUserPairIds(Long userId, List<Long> peerIds) {
        if (CollUtil.isEmpty(peerIds)) {
            return 0;
        }
        return delete(new LambdaQueryWrapperX<ImPrivateMessageDO>()
                .and(w -> w.and(q -> q.eq(ImPrivateMessageDO::getSenderId, userId)
                                .in(ImPrivateMessageDO::getReceiverId, peerIds))
                        .or(q -> q.in(ImPrivateMessageDO::getSenderId, peerIds)
                                .eq(ImPrivateMessageDO::getReceiverId, userId))));
    }

    default PageResult<ImPrivateMessageDO> selectPage(ImPrivateMessageManagerPageReqVO reqVO) {
        LambdaQueryWrapperX<ImPrivateMessageDO> query = new LambdaQueryWrapperX<>();
        if (reqVO.getSenderId() != null && reqVO.getReceiverId() != null) {
            query.and(w -> w.eq(ImPrivateMessageDO::getSenderId, reqVO.getSenderId())
                            .eq(ImPrivateMessageDO::getReceiverId, reqVO.getReceiverId())
                            .or()
                            .eq(ImPrivateMessageDO::getSenderId, reqVO.getReceiverId())
                            .eq(ImPrivateMessageDO::getReceiverId, reqVO.getSenderId()));
        } else {
            query.eqIfPresent(ImPrivateMessageDO::getSenderId, reqVO.getSenderId())
                    .eqIfPresent(ImPrivateMessageDO::getReceiverId, reqVO.getReceiverId());
        }
        return selectPage(reqVO, query
                .eqIfPresent(ImPrivateMessageDO::getSenderType, reqVO.getSenderType())
                .eqIfPresent(ImPrivateMessageDO::getReceiverType, reqVO.getReceiverType())
                .eqIfPresent(ImPrivateMessageDO::getType, reqVO.getType())
                .likeIfPresent(ImPrivateMessageDO::getContent, reqVO.getContent())
                .eqIfPresent(ImPrivateMessageDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImPrivateMessageDO::getSendTime, reqVO.getSendTime())
                .orderByDesc(ImPrivateMessageDO::getId));
    }

    /**
     * 导出私聊消息（按筛选条件全量，不分页）
     *
     * @param reqVO 筛选条件
     * @return 消息列表
     */
    default List<ImPrivateMessageDO> selectExportList(ImPrivateMessageManagerPageReqVO reqVO) {
        LambdaQueryWrapperX<ImPrivateMessageDO> query = new LambdaQueryWrapperX<>();
        if (reqVO.getSenderId() != null && reqVO.getReceiverId() != null) {
            query.and(w -> w.eq(ImPrivateMessageDO::getSenderId, reqVO.getSenderId())
                            .eq(ImPrivateMessageDO::getReceiverId, reqVO.getReceiverId())
                            .or()
                            .eq(ImPrivateMessageDO::getSenderId, reqVO.getReceiverId())
                            .eq(ImPrivateMessageDO::getReceiverId, reqVO.getSenderId()));
        } else {
            query.eqIfPresent(ImPrivateMessageDO::getSenderId, reqVO.getSenderId())
                    .eqIfPresent(ImPrivateMessageDO::getReceiverId, reqVO.getReceiverId());
        }
        return selectList(query
                .eqIfPresent(ImPrivateMessageDO::getSenderType, reqVO.getSenderType())
                .eqIfPresent(ImPrivateMessageDO::getReceiverType, reqVO.getReceiverType())
                .eqIfPresent(ImPrivateMessageDO::getType, reqVO.getType())
                .likeIfPresent(ImPrivateMessageDO::getContent, reqVO.getContent())
                .eqIfPresent(ImPrivateMessageDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImPrivateMessageDO::getSendTime, reqVO.getSendTime())
                .orderByDesc(ImPrivateMessageDO::getId));
    }

    // ==================== 管理端客服工作台 ====================

    /**
     * 查询客服与指定用户（peerId）的会话消息（游标翻页，按 id 倒序）
     * <p>覆盖三类消息：</p>
     * <ol>
     *   <li>客服 ↔ 用户（sender/receiver_type=3, csId ↔ peerId）</li>
     *   <li>机器人 → 用户（sender_type=2, receiver_id=peerId）：机器人回复该用户的消息</li>
     *   <li>用户 → 机器人（receiver_type=2, sender_id=peerId）：该用户发给机器人的消息</li>
     * </ol>
     * <p>方案 C 下客服仅以自身 id（csId）定位。扩展机器人消息后，客服工作台可看到
     * 转人工前该用户与机器人的完整对话历史。</p>
     *
     * @param csId   客服编号（im_customer_service.id）
     * @param peerId 对端用户编号（im_users.id）
     * @param maxId  游标：起始消息 id（不含），为空则从最新开始
     * @param limit  拉取数量
     * @return 消息列表（按 id 倒序）
     */
    default List<ImPrivateMessageDO> selectCsConversationMessages(@Param("csId") Long csId,
                                                                  @Param("peerId") Long peerId,
                                                                  @Param("maxId") Long maxId,
                                                                  @Param("limit") Integer limit) {
        QueryWrapperX<ImPrivateMessageDO> wrapper = new QueryWrapperX<>();
        wrapper.and(w -> w.eq("sender_type", 3).eq("sender_id", csId).eq("receiver_id", peerId)
                        .or()
                        .eq("receiver_type", 3).eq("receiver_id", csId).eq("sender_id", peerId)
                        // 机器人 ↔ 该用户的消息（转人工前的对话历史）
                        .or()
                        .eq("sender_type", 2).eq("receiver_id", peerId)
                        .or()
                        .eq("receiver_type", 2).eq("sender_id", peerId))
                .lt(maxId != null, "id", maxId)
                .orderByDesc("id");
        wrapper.limitN(limit);
        return selectList(wrapper);
    }

}
