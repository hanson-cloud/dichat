package com.diqin.cloud.module.im.dal.mysql.moment;

import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentLikeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImMomentLikeMapper extends BaseMapperX<ImMomentLikeDO> {

    default ImMomentLikeDO selectByMomentIdAndUserId(Long momentId, Long userId) {
        return selectOne(ImMomentLikeDO::getMomentId, momentId, ImMomentLikeDO::getUserId, userId);
    }

    default Long selectCountByMomentId(Long momentId) {
        return selectCount(ImMomentLikeDO::getMomentId, momentId);
    }
}
