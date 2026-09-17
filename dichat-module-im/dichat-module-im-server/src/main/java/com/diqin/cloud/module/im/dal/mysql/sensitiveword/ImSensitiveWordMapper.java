package com.diqin.cloud.module.im.dal.mysql.sensitiveword;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.sensitiveword.vo.ImSensitiveWordPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.sensitiveword.ImSensitiveWordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * IM 敏感词 Mapper
 *
 * @author hanson
 */
@Mapper
public interface ImSensitiveWordMapper extends BaseMapperX<ImSensitiveWordDO> {

    default List<ImSensitiveWordDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<ImSensitiveWordDO>()
                .eq(ImSensitiveWordDO::getStatus, status));
    }

    default ImSensitiveWordDO selectByWord(String word) {
        return selectOne(new LambdaQueryWrapperX<ImSensitiveWordDO>()
                .eq(ImSensitiveWordDO::getWord, word));
    }

    default PageResult<ImSensitiveWordDO> selectPage(ImSensitiveWordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImSensitiveWordDO>()
                .likeIfPresent(ImSensitiveWordDO::getWord, reqVO.getWord())
                .eqIfPresent(ImSensitiveWordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImSensitiveWordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImSensitiveWordDO::getId));
    }

    @Select("SELECT MAX(update_time) FROM im_sensitive_word")
    LocalDateTime selectMaxUpdateTime(@Param("tenantId") Long tenantId);

}
