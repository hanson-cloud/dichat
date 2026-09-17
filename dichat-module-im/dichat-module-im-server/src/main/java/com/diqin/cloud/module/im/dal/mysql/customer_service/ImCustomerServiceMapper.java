package com.diqin.cloud.module.im.dal.mysql.customer_service;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceActiveStatDO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 客服 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImCustomerServiceMapper extends BaseMapperX<ImCustomerServiceDO> {

    default PageResult<ImCustomerServiceDO> selectPage(ImCustomerServiceManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImCustomerServiceDO>()
                .likeIfPresent(ImCustomerServiceDO::getUsername, reqVO.getUsername())
                .likeIfPresent(ImCustomerServiceDO::getNickname, reqVO.getNickname())
                .eqIfPresent(ImCustomerServiceDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImCustomerServiceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImCustomerServiceDO::getId));
    }

    default ImCustomerServiceDO selectByUsername(String username) {
        return selectOne(ImCustomerServiceDO::getUsername, username);
    }

    /**
     * 根据关联的管理后台账号编号（system_users.id）查询客服
     * <p>管理端「客服工作台」用当前登录管理员编号反查其坐席身份；一个管理员可绑定一个客服（本方法取首个）。
     *
     * @param adminUserId 管理后台账号编号（system_users.id）
     * @return 客服配置；未绑定时返回 {@code null}
     */
    default ImCustomerServiceDO selectByAdminUserId(Long adminUserId) {
        if (adminUserId == null) {
            return null;
        }
        return selectOne(ImCustomerServiceDO::getAdminUserId, adminUserId);
    }

    /**
     * 获取「当前可接入」的客服（在线 status=1），
     * 按「近一段时间内的活跃会话数（来自 im_private_message 的不同对端数）升序」挑选，实现最低并发分配；
     * 并跳过已达 max_concurrent 上限的客服。无可用客服时返回 null。
     * <p>活跃会话数 = 该客服 im_customer_service.id 在 windowStart 之后、与不同对端（sender/receiver 互换）的私聊数。
     * <p>供移动端「联系客服」与机器人「转人工」自动分配共用，确保两端挑到的客服一致。
     *
     * @param windowStart 统计活跃会话的起始时间（早于此时间的消息不计入）
     */
    @Select("""
        <script>
        SELECT cs.*,
               COALESCE(cnt.conv_count, 0) AS conv_count
        FROM im_customer_service cs
        LEFT JOIN (
            SELECT t.cs_id, COUNT(DISTINCT t.partner) AS conv_count
            FROM (
                SELECT cs2.id AS cs_id,
                       CASE WHEN pm.sender_type = 3 THEN pm.receiver_id ELSE pm.sender_id END AS partner
                FROM im_customer_service cs2
                LEFT JOIN im_private_message pm
                    ON ((pm.sender_id = cs2.id AND pm.sender_type = 3)
                     OR (pm.receiver_id = cs2.id AND pm.receiver_type = 3))
                   AND pm.send_time &gt;= #{windowStart}
                WHERE cs2.status = 1
            ) t
            GROUP BY t.cs_id
        ) cnt ON cnt.cs_id = cs.id
        WHERE cs.status = 1
          AND (cs.max_concurrent IS NULL OR COALESCE(cnt.conv_count, 0) &lt; cs.max_concurrent)
        ORDER BY COALESCE(cnt.conv_count, 0) ASC, cs.sort ASC, cs.id ASC
        LIMIT 1
        </script>
        """)
    ImCustomerServiceDO selectCurrent(@Param("windowStart") LocalDateTime windowStart);

    /**
     * 判断某用户近一段时间内是否已与任意客服有过私聊，
     * 用于「转人工」去重冷却：已存在活跃客服会话时不再重复转接开新会话。
     *
     * @param userId      用户 im_users.id（普通用户侧身份，正确）
     * @param windowStart 冷却窗口起始时间
     * @return 存在任一近期客服私聊则为 true
     */
    @Select("""
        <script>
        SELECT 1
        FROM im_private_message pm
        WHERE pm.send_time &gt;= #{windowStart}
          AND (
            (pm.sender_id = #{userId} AND pm.sender_type = 1 AND pm.receiver_type = 3)
            OR (pm.receiver_id = #{userId} AND pm.receiver_type = 1 AND pm.sender_type = 3)
          )
        LIMIT 1
        </script>
        """)
    boolean existsRecentConversationWithCs(@Param("userId") Long userId, @Param("windowStart") LocalDateTime windowStart);

    /**
     * 统计每个客服在窗口内的「活跃会话数」（与不同对端的私聊数），供管理后台列表展示实时负载。
     * 返回结果覆盖所有客服（含离线，其活跃数为 0），便于列表逐行展示。
     * <p>方案 C：客服以自身 id（im_customer_service.id）+ CS 类型寻址。</p>
     *
     * @param windowStart 统计窗口起始时间
     */
    @Select("""
        <script>
        SELECT cs.id AS csId,
               COALESCE(cnt.conv_count, 0) AS activeConversations
        FROM im_customer_service cs
        LEFT JOIN (
            SELECT t.cs_id, COUNT(DISTINCT t.partner) AS conv_count
            FROM (
                SELECT cs2.id AS cs_id,
                       CASE WHEN pm.sender_type = 3 THEN pm.receiver_id ELSE pm.sender_id END AS partner
                FROM im_customer_service cs2
                LEFT JOIN im_private_message pm
                    ON ((pm.sender_id = cs2.id AND pm.sender_type = 3)
                     OR (pm.receiver_id = cs2.id AND pm.receiver_type = 3))
                   AND pm.send_time &gt;= #{windowStart}
            ) t
            GROUP BY t.cs_id
        ) cnt ON cnt.cs_id = cs.id
        </script>
        """)
    List<ImCustomerServiceActiveStatDO> selectActiveConversationStats(@Param("windowStart") LocalDateTime windowStart);

}
