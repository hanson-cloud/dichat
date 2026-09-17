package com.diqin.cloud.module.im.dal.mysql.favorite;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.favorite.ImUserFavoriteDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImUserFavoriteMapper extends BaseMapperX<ImUserFavoriteDO> {

    default List<ImUserFavoriteDO> selectListByUserId(Long userId) {
        return selectList(ImUserFavoriteDO::getUserId, userId);
    }

    default ImUserFavoriteDO selectByUserIdAndMessageId(Long userId, Long messageId) {
        return selectOne(ImUserFavoriteDO::getUserId, userId, ImUserFavoriteDO::getMessageId, messageId);
    }

    default PageResult<ImUserFavoriteDO> selectPageByUserId(Long userId, Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo).setPageSize(pageSize);
        return selectPage(pageParam,
                new LambdaQueryWrapperX<ImUserFavoriteDO>()
                        .eq(ImUserFavoriteDO::getUserId, userId)
                        .orderByDesc(ImUserFavoriteDO::getId));
    }

    default void delete(Long id, Long userId){
        this.delete( Wrappers.<ImUserFavoriteDO>lambdaQuery().eq(ImUserFavoriteDO::getId, id).eq(ImUserFavoriteDO::getUserId, userId));
    }
}
