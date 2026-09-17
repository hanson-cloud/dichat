package com.diqin.cloud.module.im.dal.mysql.usertag;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.usertag.ImUserTagRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * IM 用户标签关联关系 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImUserTagRelationMapper extends BaseMapperX<ImUserTagRelationDO> {

    default PageResult<ImUserTagRelationDO> selectPage(ImUserTagRelationManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImUserTagRelationDO>()
                .eqIfPresent(ImUserTagRelationDO::getTagId, reqVO.getTagId())
                .eqIfPresent(ImUserTagRelationDO::getUserId, reqVO.getUserId())
                .orderByDesc(ImUserTagRelationDO::getId));
    }

    /**
     * 统计每个标签下的用户数（key=tagId, value=count）
     */
    default Map<Long, Long> countGroupByTagId(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        List<ImUserTagRelationDO> list = selectList(new LambdaQueryWrapperX<ImUserTagRelationDO>()
                .in(ImUserTagRelationDO::getTagId, tagIds));
        return list.stream().collect(Collectors.groupingBy(ImUserTagRelationDO::getTagId, Collectors.counting()));
    }

    /**
     * 查询某用户在某标签下是否已存在关联
     */
    default ImUserTagRelationDO selectByUserAndTag(Long userId, Long tagId) {
        return selectOne(new LambdaQueryWrapperX<ImUserTagRelationDO>()
                .eq(ImUserTagRelationDO::getUserId, userId)
                .eq(ImUserTagRelationDO::getTagId, tagId));
    }

    /**
     * 批量删除某标签的全部关联
     */
    default void deleteByTagId(Long tagId) {
        delete(new LambdaQueryWrapperX<ImUserTagRelationDO>().eq(ImUserTagRelationDO::getTagId, tagId));
    }

}
