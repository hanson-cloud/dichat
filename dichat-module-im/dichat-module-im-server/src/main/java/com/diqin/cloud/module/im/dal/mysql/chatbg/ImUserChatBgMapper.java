package com.diqin.cloud.module.im.dal.mysql.chatbg;

import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.module.im.dal.dataobject.chatbg.ImUserChatBgDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImUserChatBgMapper extends BaseMapperX<ImUserChatBgDO> {

    default ImUserChatBgDO selectByUserIdAndConvKey(Long userId, String convKey) {
        return selectOne(ImUserChatBgDO::getUserId, userId, ImUserChatBgDO::getConvKey, convKey);
    }
}
