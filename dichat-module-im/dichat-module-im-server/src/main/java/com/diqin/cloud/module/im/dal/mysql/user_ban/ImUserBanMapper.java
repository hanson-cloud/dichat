package com.diqin.cloud.module.im.dal.mysql.user_ban;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.user_ban.vo.ImUserBanManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.user_ban.ImUserBanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 用户封禁记录 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImUserBanMapper extends BaseMapperX<ImUserBanDO> {

    default PageResult<ImUserBanDO> selectPage(ImUserBanManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImUserBanDO>()
                .eqIfPresent(ImUserBanDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ImUserBanDO::getBanType, reqVO.getBanType())
                .eqIfPresent(ImUserBanDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImUserBanDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImUserBanDO::getId));
    }

}
