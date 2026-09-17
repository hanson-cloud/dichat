package com.diqin.cloud.module.im.service.robot;

import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotReplyRuleManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotReplyRuleDO;
import com.diqin.cloud.module.im.dal.mysql.robot.ImRobotMapper;
import com.diqin.cloud.module.im.dal.mysql.robot.ImRobotReplyRuleMapper;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.ROBOT_REPLY_RULE_KEYWORD_DUPLICATE;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.ROBOT_REPLY_RULE_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 机器人自动回复规则管理 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImRobotReplyRuleManagerServiceImpl implements ImRobotReplyRuleManagerService {

    @Resource
    private ImRobotReplyRuleMapper replyRuleMapper;

    @Resource
    private ImRobotMapper robotMapper;

    @Override
    public PageResult<ImRobotReplyRuleManagerRespVO> getReplyRuleManagerPage(ImRobotReplyRuleManagerPageReqVO pageReqVO) {
        PageResult<ImRobotReplyRuleDO> pageResult = replyRuleMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, ImRobotReplyRuleManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_ROBOT_TYPE, subType = IM_ROBOT_RULE_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_ROBOT_RULE_CREATE_SUCCESS)
    public Long createReplyRule(ImRobotReplyRuleManagerSaveReqVO createReqVO) {
        // 校验（机器人编号 + 关键词）唯一
        validateKeywordUnique(null, createReqVO.getRobotId(), createReqVO.getKeyword());
        ImRobotReplyRuleDO rule = BeanUtils.toBean(createReqVO, ImRobotReplyRuleDO.class);
        replyRuleMapper.insert(rule);
        LogRecordContext.putVariable("reqVO", createReqVO);
        return rule.getId();
    }

    @Override
    @LogRecord(type = IM_ROBOT_TYPE, subType = IM_ROBOT_RULE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_ROBOT_RULE_UPDATE_SUCCESS)
    public void updateReplyRule(ImRobotReplyRuleManagerUpdateReqVO updateReqVO) {
        // 校验存在
        if (replyRuleMapper.selectById(updateReqVO.getId()) == null) {
            throw ServiceExceptionUtil.exception(ROBOT_REPLY_RULE_NOT_EXISTS);
        }
        // 校验（机器人编号 + 关键词）唯一（排除自身）
        validateKeywordUnique(updateReqVO.getId(), updateReqVO.getRobotId(), updateReqVO.getKeyword());
        ImRobotReplyRuleDO update = BeanUtils.toBean(updateReqVO, ImRobotReplyRuleDO.class);
        replyRuleMapper.updateById(update);
    }

    @Override
    @LogRecord(type = IM_ROBOT_TYPE, subType = IM_ROBOT_RULE_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_ROBOT_RULE_DELETE_SUCCESS)
    public void deleteReplyRule(Long id) {
        // 校验存在
        if (replyRuleMapper.selectById(id) == null) {
            throw ServiceExceptionUtil.exception(ROBOT_REPLY_RULE_NOT_EXISTS);
        }
        replyRuleMapper.deleteById(id);
    }

    private void validateKeywordUnique(Long id, Long robotId, String keyword) {
        ImRobotReplyRuleDO exist = replyRuleMapper.selectByRobotAndKeyword(robotId, keyword);
        if (exist != null && (!exist.getId().equals(id))) {
            throw ServiceExceptionUtil.exception(ROBOT_REPLY_RULE_KEYWORD_DUPLICATE, keyword);
        }
    }

    @Override
    public List<ImRobotReplyRuleDO> getCommonQuestions(Integer limit) {
        // 取第一个启用中的机器人作为客服机器人（业务上通常只有一个客服机器人）
        List<ImRobotDO> robots = robotMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
        if (robots.isEmpty()) {
            return Collections.emptyList();
        }
        return replyRuleMapper.selectCommonQuestionsByRobotId(
                robots.getFirst().getId(), CommonStatusEnum.ENABLE.getStatus(), limit);
    }

}
