package com.diqin.cloud.module.im.service.robot;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * IM 机器人管理 Service 接口
 *
 * @author 速构构
 */
public interface ImRobotManagerService {

    /**
     * 获得机器人分页
     */
    PageResult<ImRobotManagerRespVO> getRobotManagerPage(ImRobotManagerPageReqVO pageReqVO);

    /**
     * 获得机器人详情
     */
    ImRobotManagerRespVO getRobot(Long id);

    /**
     * 创建机器人
     */
    Long createRobot(ImRobotManagerSaveReqVO createReqVO);

    /**
     * 更新机器人
     */
    void updateRobot(ImRobotManagerUpdateReqVO updateReqVO);

    /**
     * 删除机器人
     */
    void deleteRobot(Long id);

    /**
     * 根据机器人自身编号获取机器人（方案 C：机器人成为独立参与方后，私聊以 robot.id 寻址）
     *
     * @param id 机器人编号（im_robot.id）
     * @return 机器人配置；不存在时返回 {@code null}
     */
    ImRobotDO getRobotById(Long id);

    /**
     * 批量获取机器人（按 id），用于后台消息列表 / 导出时的「发送方 / 接收方昵称」回填
     *
     * @param ids 机器人编号集合
     * @return id → 机器人配置
     */
    Map<Long, ImRobotDO> getRobotMap(Collection<Long> ids);

    /**
     * APP 端获取启用中的机器人列表（含关联 userId，供移动端打开私聊）
     */
    List<ImRobotManagerRespVO> getAppRobotList();

}
