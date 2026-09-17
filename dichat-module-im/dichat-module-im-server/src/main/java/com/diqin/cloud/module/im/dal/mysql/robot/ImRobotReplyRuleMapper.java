package com.diqin.cloud.module.im.dal.mysql.robot;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotReplyRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 机器人自动回复规则 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImRobotReplyRuleMapper extends BaseMapperX<ImRobotReplyRuleDO> {

    default PageResult<ImRobotReplyRuleDO> selectPage(ImRobotReplyRuleManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImRobotReplyRuleDO>()
                .eqIfPresent(ImRobotReplyRuleDO::getRobotId, reqVO.getRobotId())
                .likeIfPresent(ImRobotReplyRuleDO::getKeyword, reqVO.getKeyword())
                .eqIfPresent(ImRobotReplyRuleDO::getStatus, reqVO.getStatus())
                .orderByDesc(ImRobotReplyRuleDO::getId));
    }

    default ImRobotReplyRuleDO selectByRobotAndKeyword(Long robotId, String keyword) {
        return selectOne(new LambdaQueryWrapperX<ImRobotReplyRuleDO>()
                .eq(ImRobotReplyRuleDO::getRobotId, robotId)
                .eq(ImRobotReplyRuleDO::getKeyword, keyword));
    }

    /**
     * 查询某机器人启用中的回复规则，按 sort 升序、id 升序（保证匹配优先级稳定）
     */
    default List<ImRobotReplyRuleDO> selectListByRobotIdAndStatus(Long robotId, Integer status) {
        return selectList(new LambdaQueryWrapperX<ImRobotReplyRuleDO>()
                .eq(ImRobotReplyRuleDO::getRobotId, robotId)
                .eq(ImRobotReplyRuleDO::getStatus, status)
                .orderByAsc(ImRobotReplyRuleDO::getSort)
                .orderByAsc(ImRobotReplyRuleDO::getId));
    }

    /**
     * 随机查询某机器人启用中的常见问题（reply_type=3），用于客服页快捷入口
     */
    default List<ImRobotReplyRuleDO> selectCommonQuestionsByRobotId(Long robotId, Integer status, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        return selectList(new LambdaQueryWrapperX<ImRobotReplyRuleDO>()
                .eq(ImRobotReplyRuleDO::getRobotId, robotId)
                .eq(ImRobotReplyRuleDO::getStatus, status)
                .eq(ImRobotReplyRuleDO::getReplyType, 3)
                .last("ORDER BY RAND() LIMIT " + limit));
    }

}
