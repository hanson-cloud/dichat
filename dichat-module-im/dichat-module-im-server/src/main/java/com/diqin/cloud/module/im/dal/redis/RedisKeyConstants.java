package com.diqin.cloud.module.im.dal.redis;


/**
 * IM Redis Key 枚举类
 *
 * @author hanson
 */
public interface RedisKeyConstants {

    /**
     * 群消息已读位置
     * KEY 格式：  im:group:message:read:{groupId}
     * VALUE 数据类型： Hash (field: userId, value: maxReadMessageId)
     */
    String GROUP_MESSAGE_READ = "im:group:message:read:%s";

    /**
     * 频道消息已读位置
     * KEY 格式：  im:channel:message:read:{channelId}
     * VALUE 数据类型： Hash (field: userId, value: maxReadMessageId)
     */
    String CHANNEL_MESSAGE_READ = "im:channel:message:read:%s";

    /**
     * 好友关系状态缓存（合并「是否好友」+「是否拉黑」两态）
     * <p>
     * KEY 格式：friend_state:{userId}_{friendUserId}
     * VALUE 数据类型：{@link com.diqin.cloud.module.im.enums.friend.ImFriendStateEnum}
     */
    String FRIEND_STATE = "friend_state";

    /**
     * 群信息缓存
     * <p>
     * KEY 格式：group:{groupId}
     * VALUE 数据类型：ImGroupDO
     */
    String GROUP = "group";

    /**
     * 群有效成员 userId 列表缓存（仅 ENABLE 状态）
     * <p>
     * KEY 格式：group_member_ids:{groupId}
     * VALUE 数据类型：List<Long>
     * <p>
     * 说明：只缓存轻量的 userId 列表，适合"群消息推送目标"这类只关心 userId 的场景。
     */
    String GROUP_MEMBER_IDS = "group_member_ids";

    /**
     * 通话同对 / 同群活跃唯一性的分布式锁
     * <p>
     * KEY 格式：im_rtc_call:{conversationType}:{suffix}
     *   私聊（conversationType=1）：suffix = {小 userId}_{大 userId}
     *   群聊（conversationType=2）：suffix = {groupId}
     * VALUE 数据格式：HASH                  // RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构
     * 过期时间：不固定（lock 时显式传 timeoutMillis）
     */
    String IM_RTC_CALL_LOCK = "im_rtc_call:%d:%s";

    /**
     * 钱包并发操作分布式锁（充值 / 转账 / 红包领取）
     * <p>
     * KEY 格式：im:wallet:lock:{suffix}
     *   suffix = 单用户场景的 userId，或双用户场景的 {小 userId}_{大 userId}
     * VALUE 数据格式：HASH                  // RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构
     * 过期时间：不固定（lock 时显式传 timeoutMillis）
     */
    String IM_WALLET_LOCK = "im:wallet:lock:%s";

    /**
     * 红包抢领并发分布式锁（按红包 id 串行，避免同一红包多人同时抢到同一份）
     * <p>
     * KEY 格式：im:red-packet:lock:{redPacketId}
     * VALUE 数据格式：HASH                  // RLock.class：Redisson 的 Lock 锁，使用 Hash 数据结构
     * 过期时间：不固定（lock 时显式传 timeoutMillis）
     */
    String IM_RED_PACKET_LOCK = "im:red-packet:lock:%s";

}
