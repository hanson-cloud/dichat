package com.diqin.cloud.module.im.dal.mysql.paypassword;

import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.paypassword.ImPayPasswordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 支付密码 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImPayPasswordMapper extends BaseMapperX<ImPayPasswordDO> {

    default ImPayPasswordDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<ImPayPasswordDO>()
                .eq(ImPayPasswordDO::getUserId, userId));
    }

}
