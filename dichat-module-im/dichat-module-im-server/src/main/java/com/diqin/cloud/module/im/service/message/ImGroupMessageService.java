package com.diqin.cloud.module.im.service.message;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.message.vo.group.ImGroupMessageManagerPageReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.group.AppImGroupMessageListReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.group.AppImGroupMessageSendReqVO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImGroupMessageDO;
import com.diqin.cloud.module.im.service.message.dto.ImGroupMessageSendDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IM 群聊消息 Service 接口
 *
 * @author hanson
 */
public interface ImGroupMessageService {

    /**
     * 【用户调用】发送群聊消息
     * <p>
     * 用户在 IM 客户端发送 TEXT / IMAGE 等消息时调用，含幂等、敏感词、quote、@ 解析等业务校验。
     * type 校验由 VO 层 {@code @InEnum} + {@code @AssertTrue} 完成（仅允许 normal 类型）。
     *
     * @param senderId 发送人编号
     * @param reqVO    发送请求
     * @return 消息
     */
    ImGroupMessageDO sendGroupMessage(Long senderId, AppImGroupMessageSendReqVO reqVO);

    /**
     * 【系统调用】发送群聊消息：内部查 active 成员 + 推送
     * <p>
     * 调用方批量调用会触发多次 active 成员查询；如果调用方已经持有成员快照，优先使用 {@link #sendGroupMessage(Long, Collection, ImGroupMessageSendDTO)} 方法，避免重复查询。
     *
     * @param senderId 发送人编号
     * @param dto      消息 DTO
     * @return 消息
     */
    // todo 此方法的使用方式有待确认
    ImGroupMessageDO sendGroupMessage(Long senderId, ImGroupMessageSendDTO dto);

    /**
     * 【系统调用】发送群聊消息：显式指定推送目标
     *
     * @param senderId      发送人编号
     * @param targetUserIds 推送目标用户编号集合（调用方在变更成员状态前抓取的快照）
     * @param dto           消息 DTO
     * @return 构造的消息 DO（持久化时 id 已回填）
     */
    ImGroupMessageDO sendGroupMessage(Long senderId, Collection<Long> targetUserIds, ImGroupMessageSendDTO dto);

    /**
     * 【用户调用】撤回群聊消息
     *
     * @param userId    当前用户编号
     * @param messageId 消息编号
     * @return 撤回后的提示消息
     */
    ImGroupMessageDO recallGroupMessage(Long userId, Long messageId);

    /**
     * 拉取群聊消息（增量）
     *
     * @param userId 当前用户编号
     * @param minId  最小消息 id（不含）
     * @param size   拉取数量
     * @return 消息列表
     */
    List<ImGroupMessageDO> pullGroupMessageList(Long userId, Long minId, Integer size);

    /**
     * 标记群聊消息已读
     *
     * @param userId    当前用户编号
     * @param groupId   群编号
     * @param messageId 已读到的消息编号
     */
    void readGroupMessages(Long userId, Long groupId, Long messageId);

    /**
     * 获取群消息的已读用户列表
     *
     * @param userId    当前用户编号
     * @param groupId   群编号
     * @param messageId 消息编号
     * @return 已读用户编号列表
     */
    List<Long> getGroupReadUserIds(Long userId, Long groupId, Long messageId);

    /**
     * 查询群聊历史消息（游标拉取）
     *
     * @param userId 当前用户编号
     * @param reqVO  拉取请求
     * @return 消息列表（按 id 倒序）
     */
    List<ImGroupMessageDO> getGroupMessageList(Long userId, AppImGroupMessageListReqVO reqVO);

    /**
     * 清理用户在某群的已读位置缓存
     * <p>
     * 用于成员退群场景
     *
     * @param groupId 群编号
     * @param userId  用户编号
     */
    void deleteReadMaxMessageId(Long groupId, Long userId);

    /**
     * 批量清理用户在某群的已读位置缓存
     * <p>
     * 用于批量踢出场景
     *
     * @param groupId 群编号
     * @param userIds 用户编号集合
     */
    void deleteReadMaxMessageIds(Long groupId, Collection<Long> userIds);

    /**
     * 清理某群所有用户的已读位置缓存
     * <p>
     * 用于群解散场景
     *
     * @param groupId 群编号
     */
    void deleteReadMaxMessageIdMap(Long groupId);

    /**
     * 物理删除当前用户在 groupId 群中发送的若干条消息
     * <p>
     * 权限校验：
     * 1. 群存在 + 当前用户仍在群中（否则无法"管理"自己发送过的消息）
     * 2. 群主 / 管理员可删除他人消息；普通成员仅能删自己发送的消息（与 box-im 群场景行为一致）
     *
     * @param userId     当前用户编号
     * @param groupId    群编号
     * @param messageIds 待删除的消息编号列表
     * @return 实际删除条数
     */
    int deleteGroupMessages(Long userId, Long groupId, List<Long> messageIds);

    /**
     * 清空当前用户在 groupId 群中可见的所有消息
     * <p>
     * 物理删除：包含定向消息、撤回消息等所有状态；删除后给当前用户多端推 GROUP_CHAT_CLEAR
     *
     * @param userId  当前用户编号
     * @param groupId 群编号
     * @return 实际删除条数
     */
    int clearGroupChat(Long userId, Long groupId);

    // ==================== 管理后台 ====================

    /**
     * 【管理后台】分页查询群聊消息
     */
    PageResult<ImGroupMessageDO> getGroupMessagePage(ImGroupMessageManagerPageReqVO reqVO);

    /**
     * 【管理后台】获取群聊消息详情
     */
    ImGroupMessageDO getGroupMessage(Long id);

    /**
     * 【管理后台】导出群聊消息（按筛选条件全量，不分页）
     */
    List<ImGroupMessageDO> getGroupMessageExportList(ImGroupMessageManagerPageReqVO reqVO);

    /**
     * 批量按消息编号查询群聊消息
     *
     * @param ids 消息编号集合
     * @return 消息列表
     */
    List<ImGroupMessageDO> getGroupMessageList(Collection<Long> ids);

    /**
     * 批量按消息编号查询群聊消息，返回 messageId → DO 映射
     *
     * @param ids 消息编号集合
     * @return 消息 Map（key = 消息编号）
     */
    Map<Long, ImGroupMessageDO> getGroupMessageMap(Collection<Long> ids);

}

