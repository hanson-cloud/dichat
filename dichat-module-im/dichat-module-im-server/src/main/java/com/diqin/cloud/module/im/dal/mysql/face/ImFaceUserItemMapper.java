package com.diqin.cloud.module.im.dal.mysql.face;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.face.vo.useritem.ImFaceUserItemManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.face.ImFaceUserItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 用户私有表情 Mapper
 *
 * @author hanson
 */
@Mapper
public interface ImFaceUserItemMapper extends BaseMapperX<ImFaceUserItemDO> {

    default List<ImFaceUserItemDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<ImFaceUserItemDO>()
                .eq(ImFaceUserItemDO::getUserId, userId)
                .orderByAsc(ImFaceUserItemDO::getSort)
                .orderByDesc(ImFaceUserItemDO::getId));
    }

    default ImFaceUserItemDO selectByUserIdAndUrl(Long userId, String url) {
        return selectOne(new LambdaQueryWrapperX<ImFaceUserItemDO>()
                .eq(ImFaceUserItemDO::getUserId, userId)
                .eq(ImFaceUserItemDO::getUrl, url));
    }

    default Long selectCountByUserId(Long userId) {
        return selectCount(ImFaceUserItemDO::getUserId, userId);
    }

    default PageResult<ImFaceUserItemDO> selectPage(ImFaceUserItemManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImFaceUserItemDO>()
                .eqIfPresent(ImFaceUserItemDO::getUserId, reqVO.getUserId())
                .likeIfPresent(ImFaceUserItemDO::getName, reqVO.getName())
                .betweenIfPresent(ImFaceUserItemDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImFaceUserItemDO::getId));
    }

}
