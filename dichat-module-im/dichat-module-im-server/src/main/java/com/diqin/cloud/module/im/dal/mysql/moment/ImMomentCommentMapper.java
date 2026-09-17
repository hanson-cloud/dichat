package com.diqin.cloud.module.im.dal.mysql.moment;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentCommentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImMomentCommentMapper extends BaseMapperX<ImMomentCommentDO> {

    default List<ImMomentCommentDO> selectListByMomentId(Long momentId) {
        return selectList(ImMomentCommentDO::getMomentId, momentId);
    }

    default Long selectCountByMomentId(Long momentId) {
        return selectCount(ImMomentCommentDO::getMomentId, momentId);
    }

    default void delete(Long commentId, Long userId){
        this.delete( Wrappers.<ImMomentCommentDO>lambdaQuery().eq(ImMomentCommentDO::getId, commentId).eq(ImMomentCommentDO::getUserId, userId));
    }
}
