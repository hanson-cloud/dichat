package com.diqin.cloud.module.im.dal.mysql.blacklist;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.blacklist.ImBlacklistDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 全局黑名单 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImBlacklistMapper extends BaseMapperX<ImBlacklistDO> {

    default List<ImBlacklistDO> selectListByUserId(Long userId) {
        return selectList(ImBlacklistDO::getUserId, userId);
    }

    default PageResult<ImBlacklistDO> selectPage(ImBlacklistManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImBlacklistDO>()
                .eqIfPresent(ImBlacklistDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ImBlacklistDO::getBlockId, reqVO.getBlockId())
                .likeIfPresent(ImBlacklistDO::getReason, reqVO.getReason())
                .betweenIfPresent(ImBlacklistDO::getCreatedTime, reqVO.getCreateTime())
                .orderByDesc(ImBlacklistDO::getId));
    }

}
