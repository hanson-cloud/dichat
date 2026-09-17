package com.diqin.cloud.module.im.dal.mysql.usertag;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.usertag.ImUserTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 用户标签 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImUserTagMapper extends BaseMapperX<ImUserTagDO> {

    default PageResult<ImUserTagDO> selectPage(ImUserTagManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImUserTagDO>()
                .likeIfPresent(ImUserTagDO::getName, reqVO.getName())
                .eqIfPresent(ImUserTagDO::getStatus, reqVO.getStatus())
                .orderByDesc(ImUserTagDO::getSort)
                .orderByDesc(ImUserTagDO::getId));
    }

    /**
     * 根据名称查询（用于唯一性校验）
     */
    default ImUserTagDO selectByName(String name) {
        return selectOne(new LambdaQueryWrapperX<ImUserTagDO>().eq(ImUserTagDO::getName, name));
    }

    /**
     * 批量查询全部标签（供打标弹窗下拉使用）
     */
    default List<ImUserTagDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<ImUserTagDO>().eq(ImUserTagDO::getStatus, 1)
                .orderByDesc(ImUserTagDO::getSort).orderByDesc(ImUserTagDO::getId));
    }

}
