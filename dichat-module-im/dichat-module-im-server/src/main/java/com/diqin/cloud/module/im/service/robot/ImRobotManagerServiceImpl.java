package com.diqin.cloud.module.im.service.robot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;
import com.diqin.cloud.module.im.dal.mysql.robot.ImRobotMapper;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.service.websocket.ImWebSocketService;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.ROBOT_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.ROBOT_USERNAME_DUPLICATE;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 机器人管理 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImRobotManagerServiceImpl implements ImRobotManagerService {

    @Resource
    private ImRobotMapper robotMapper;

    @Resource
    private ImWebSocketService imWebSocketService;

    @Override
    public PageResult<ImRobotManagerRespVO> getRobotManagerPage(ImRobotManagerPageReqVO pageReqVO) {
        PageResult<ImRobotDO> pageResult = robotMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, ImRobotManagerRespVO.class);
    }

    @Override
    public ImRobotManagerRespVO getRobot(Long id) {
        ImRobotDO robot = robotMapper.selectById(id);
        return BeanUtils.toBean(robot, ImRobotManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_ROBOT_TYPE, subType = IM_ROBOT_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_ROBOT_CREATE_SUCCESS)
    public Long createRobot(ImRobotManagerSaveReqVO createReqVO) {
        // 校验登录账号唯一
        validateUsernameUnique(null, createReqVO.getUsername());
        ImRobotDO robot = BeanUtils.toBean(createReqVO, ImRobotDO.class);
        robotMapper.insert(robot);
        LogRecordContext.putVariable("robot", robot);
        // 方案 C：机器人资料变更，广播通知在线客户端重新拉取机器人列表
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.ROBOT.getType(), robot.getId()));
        return robot.getId();
    }

    @Override
    @LogRecord(type = IM_ROBOT_TYPE, subType = IM_ROBOT_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_ROBOT_UPDATE_SUCCESS)
    public void updateRobot(ImRobotManagerUpdateReqVO updateReqVO) {
        // 校验存在
        if (robotMapper.selectById(updateReqVO.getId()) == null) {
            throw ServiceExceptionUtil.exception(ROBOT_NOT_EXISTS);
        }
        // 校验登录账号唯一（排除自身）
        validateUsernameUnique(updateReqVO.getId(), updateReqVO.getUsername());
        ImRobotDO update = BeanUtils.toBean(updateReqVO, ImRobotDO.class);
        LogRecordContext.putVariable("updateReqVO", updateReqVO);
        robotMapper.updateById(update);
        // 方案 C：机器人资料变更，广播通知在线客户端重新拉取机器人列表
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.ROBOT.getType(), updateReqVO.getId()));
    }

    @Override
    @LogRecord(type = IM_ROBOT_TYPE, subType = IM_ROBOT_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_ROBOT_DELETE_SUCCESS)
    public void deleteRobot(Long id) {
        // 校验存在
        if (robotMapper.selectById(id) == null) {
            throw ServiceExceptionUtil.exception(ROBOT_NOT_EXISTS);
        }
        robotMapper.deleteById(id);
        // 方案 C：机器人资料变更，广播通知在线客户端重新拉取机器人列表
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.ROBOT.getType(), id));
    }

    private void validateUsernameUnique(Long id, String username) {
        ImRobotDO exist = robotMapper.selectByUsername(username);
        if (exist != null && (!exist.getId().equals(id))) {
            throw ServiceExceptionUtil.exception(ROBOT_USERNAME_DUPLICATE, username);
        }
    }

    @Override
    public ImRobotDO getRobotById(Long id) {
        if (id == null) {
            return null;
        }
        return robotMapper.selectById(id);
    }

    @Override
    public Map<Long, ImRobotDO> getRobotMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return MapUtil.empty();
        }
        List<ImRobotDO> list = robotMapper.selectByIds(ids);
        return list.stream()
                .filter(e -> e.getId() != null) // 防止数据库脏数据导致 NPE
                .collect(Collectors.toMap(
                        ImRobotDO::getId,
                        Function.identity(),
                        (oldVal, newVal) -> oldVal // 明确重复Key时的保留策略
                ));
    }


    @Override
    public List<ImRobotManagerRespVO> getAppRobotList() {
        List<ImRobotDO> robots = robotMapper.selectList(new LambdaQueryWrapperX<ImRobotDO>()
                .eq(ImRobotDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByAsc(ImRobotDO::getSort)
                .orderByAsc(ImRobotDO::getId));
        return BeanUtils.toBean(robots, ImRobotManagerRespVO.class);
    }

}
