package com.diqin.cloud.module.im.service.robot;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotReplyRuleDO;

import java.util.List;

/**
 * IM 机器人自动回复规则管理 Service 接口
 *
 * @author 速构构
 */
public interface ImRobotReplyRuleManagerService {

    /**
     * 获得机器人自动回复规则分页
     */
    PageResult<ImRobotReplyRuleManagerRespVO> getReplyRuleManagerPage(ImRobotReplyRuleManagerPageReqVO pageReqVO);

    /**
     * 创建机器人自动回复规则
     */
    Long createReplyRule(ImRobotReplyRuleManagerSaveReqVO createReqVO);

    /**
     * 更新机器人自动回复规则
     */
    void updateReplyRule(ImRobotReplyRuleManagerUpdateReqVO updateReqVO);

    /**
     * 删除机器人自动回复规则
     */
    void deleteReplyRule(Long id);

    /**
     * 随机获取启用中的常见问题（reply_type=3）
     *
     * @param limit 最多返回条数
     */
    List<ImRobotReplyRuleDO> getCommonQuestions(Integer limit);

}
