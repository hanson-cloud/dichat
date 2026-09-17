package com.diqin.cloud.module.im.dal.mysql.robot;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.robot.vo.ImRobotManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 机器人 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImRobotMapper extends BaseMapperX<ImRobotDO> {

    default PageResult<ImRobotDO> selectPage(ImRobotManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImRobotDO>()
                .likeIfPresent(ImRobotDO::getUsername, reqVO.getUsername())
                .likeIfPresent(ImRobotDO::getNickname, reqVO.getNickname())
                .eqIfPresent(ImRobotDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ImRobotDO::getAutoReplyEnabled, reqVO.getAutoReplyEnabled())
                .betweenIfPresent(ImRobotDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImRobotDO::getId));
    }

    default ImRobotDO selectByUsername(String username) {
        return selectOne(ImRobotDO::getUsername, username);
    }

    default List<ImRobotDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<ImRobotDO>()
                .eq(ImRobotDO::getStatus, status)
                .orderByDesc(ImRobotDO::getId));
    }

}
