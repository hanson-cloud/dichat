package com.diqin.cloud.module.im.dal.mysql.redpacket;

import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketGrabDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 红包领取明细 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImRedPacketGrabMapper extends BaseMapperX<ImRedPacketGrabDO> {

    default List<ImRedPacketGrabDO> selectListByRedPacketId(Long redPacketId) {
        return selectList(ImRedPacketGrabDO::getRedPacketId, redPacketId);
    }

    default ImRedPacketGrabDO selectByRedPacketIdAndUserId(Long redPacketId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<ImRedPacketGrabDO>()
                .eq(ImRedPacketGrabDO::getRedPacketId, redPacketId)
                .eq(ImRedPacketGrabDO::getUserId, userId));
    }

    default int markBestLuck(Long redPacketId) {
        ImRedPacketGrabDO updateObj = ImRedPacketGrabDO.builder().isBestLuck(Boolean.TRUE).build();
        return update(updateObj, new LambdaQueryWrapperX<ImRedPacketGrabDO>()
                .eq(ImRedPacketGrabDO::getRedPacketId, redPacketId)
                .orderByDesc(ImRedPacketGrabDO::getAmount)
                .last("LIMIT 1"));
    }

}
