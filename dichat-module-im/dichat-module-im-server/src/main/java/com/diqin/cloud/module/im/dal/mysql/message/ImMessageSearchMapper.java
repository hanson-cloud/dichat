package com.diqin.cloud.module.im.dal.mysql.message;

import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 全局消息搜索 Mapper
 *
 * <p>使用 UNION ALL 跨私聊+群聊搜索消息，支持关键词/类型/发送人/时间过滤。
 *
 * @author 速构构
 */
@Mapper
public interface ImMessageSearchMapper {

    /**
     * 搜索消息（分页数据）
     */
    @Select("<script>" +
            "SELECT * FROM (" +
            "  SELECT pm.id AS messageId, 1 AS chatType, pm.sender_id AS senderId, " +
            "         pm.receiver_id AS targetId, '' AS targetName, " +
            "         pm.type AS msgType, pm.content, pm.send_time AS sendTime, pm.create_time AS createTime " +
            "  FROM im_private_message pm " +
            "  WHERE pm.deleted = 0" +
            "    <if test='keyword != null and keyword != \"\"'> AND pm.content LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "    <if test='msgType != null'> AND pm.type = #{msgType}</if>" +
            "    <if test='senderId != null'> AND pm.sender_id = #{senderId}</if>" +
            "    <if test='beginTime != null'> AND pm.send_time &gt;= #{beginTime}</if>" +
            "    <if test='endTime != null'> AND pm.send_time &lt;= #{endTime}</if>" +
            "  UNION ALL " +
            "  SELECT gm.id, 2 AS chatType, gm.sender_id, " +
            "         gm.group_id, '', " +
            "         gm.type, gm.content, gm.send_time, gm.create_time " +
            "  FROM im_group_message gm " +
            "  WHERE gm.deleted = 0" +
            "    <if test='keyword != null and keyword != \"\"'> AND gm.content LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "    <if test='msgType != null'> AND gm.type = #{msgType}</if>" +
            "    <if test='senderId != null'> AND gm.sender_id = #{senderId}</if>" +
            "    <if test='beginTime != null'> AND gm.send_time &gt;= #{beginTime}</if>" +
            "    <if test='endTime != null'> AND gm.send_time &lt;= #{endTime}</if>" +
            "  ) t ORDER BY t.sendTime DESC " +
            "  LIMIT #{pageSize} OFFSET #{offset}" +
            "</script>")
    List<ImMessageSearchRespVO> searchMessages(
            @Param("keyword") String keyword,
            @Param("msgType") Integer msgType,
            @Param("senderId") Long senderId,
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("offset") long offset,
            @Param("pageSize") int pageSize);

    /**
     * 搜索消息总数（用于分页）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM (" +
            "  SELECT pm.id FROM im_private_message pm " +
            "  WHERE pm.deleted = 0" +
            "    <if test='keyword != null and keyword != \"\"'> AND pm.content LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "    <if test='msgType != null'> AND pm.type = #{msgType}</if>" +
            "    <if test='senderId != null'> AND pm.sender_id = #{senderId}</if>" +
            "    <if test='beginTime != null'> AND pm.send_time &gt;= #{beginTime}</if>" +
            "    <if test='endTime != null'> AND pm.send_time &lt;= #{endTime}</if>" +
            "  UNION ALL " +
            "  SELECT gm.id FROM im_group_message gm " +
            "  WHERE gm.deleted = 0" +
            "    <if test='keyword != null and keyword != \"\"'> AND gm.content LIKE CONCAT('%', #{keyword}, '%')</if>" +
            "    <if test='msgType != null'> AND gm.type = #{msgType}</if>" +
            "    <if test='senderId != null'> AND gm.sender_id = #{senderId}</if>" +
            "    <if test='beginTime != null'> AND gm.send_time &gt;= #{beginTime}</if>" +
            "    <if test='endTime != null'> AND gm.send_time &lt;= #{endTime}</if>" +
            "  ) t" +
            "</script>")
    long countMessages(
            @Param("keyword") String keyword,
            @Param("msgType") Integer msgType,
            @Param("senderId") Long senderId,
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime);
}
