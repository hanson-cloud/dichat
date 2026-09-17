package com.diqin.cloud.module.im.dal.mysql.transfer;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.transfer.vo.ImTransferManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.transfer.ImTransferDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 转账 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImTransferMapper extends BaseMapperX<ImTransferDO> {

    default ImTransferDO selectByNo(String no) {
        return selectOne(ImTransferDO::getNo, no);
    }

    default PageResult<ImTransferDO> selectPageByUser(Long userId, PageParam pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<ImTransferDO>()
                .and(wrapper -> wrapper.eq(ImTransferDO::getSenderUserId, userId)
                        .or().eq(ImTransferDO::getReceiverUserId, userId))
                .orderByDesc(ImTransferDO::getId));
    }

    default PageResult<ImTransferDO> selectPage(ImTransferManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImTransferDO>()
                .eqIfPresent(ImTransferDO::getSenderUserId, reqVO.getSenderUserId())
                .eqIfPresent(ImTransferDO::getReceiverUserId, reqVO.getReceiverUserId())
                .eqIfPresent(ImTransferDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImTransferDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImTransferDO::getId));
    }

}
