package com.diqin.cloud.module.im.dal.mysql.album;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.album.ImUserAlbumDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImUserAlbumMapper extends BaseMapperX<ImUserAlbumDO> {

    default List<ImUserAlbumDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<ImUserAlbumDO>()
                .eq(ImUserAlbumDO::getUserId, userId)
                .orderByDesc(ImUserAlbumDO::getId));
    }

    default void delete(Long id, Long userId) {
        this.delete(Wrappers.<ImUserAlbumDO>lambdaQuery()
                .eq(ImUserAlbumDO::getId, id)
                .eq(ImUserAlbumDO::getUserId, userId));
    }
}
