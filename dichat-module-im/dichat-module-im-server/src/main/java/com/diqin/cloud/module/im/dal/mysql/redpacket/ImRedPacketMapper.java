package com.diqin.cloud.module.im.dal.mysql.redpacket;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 红包 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImRedPacketMapper extends BaseMapperX<ImRedPacketDO> {

    default ImRedPacketDO selectByNo(String no) {
        return selectOne(ImRedPacketDO::getNo, no);
    }

    default ImRedPacketDO selectByNoAndSender(String no, Long senderUserId) {
        return selectOne(new LambdaQueryWrapperX<ImRedPacketDO>()
                .eq(ImRedPacketDO::getNo, no)
                .eq(ImRedPacketDO::getSenderUserId, senderUserId));
    }

    default PageResult<ImRedPacketDO> selectPage(ImRedPacketManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImRedPacketDO>()
                .eqIfPresent(ImRedPacketDO::getSenderUserId, reqVO.getSenderUserId())
                .eqIfPresent(ImRedPacketDO::getConversationType, reqVO.getConversationType())
                .eqIfPresent(ImRedPacketDO::getType, reqVO.getType())
                .eqIfPresent(ImRedPacketDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImRedPacketDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImRedPacketDO::getId));
    }

    default PageResult<ImRedPacketDO> selectPageBySender(Long userId, PageParam pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<ImRedPacketDO>()
                .eq(ImRedPacketDO::getSenderUserId, userId)
                .orderByDesc(ImRedPacketDO::getId));
    }

}
