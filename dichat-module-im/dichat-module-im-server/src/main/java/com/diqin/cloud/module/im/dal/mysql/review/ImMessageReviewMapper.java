package com.diqin.cloud.module.im.dal.mysql.review;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.review.ImMessageReviewDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 消息审核 Mapper
 *
 * @author 速构构
 */
@Mapper
public interface ImMessageReviewMapper extends BaseMapperX<ImMessageReviewDO> {

    default PageResult<ImMessageReviewDO> selectPage(ImMessageReviewManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImMessageReviewDO>()
                .eqIfPresent(ImMessageReviewDO::getReviewStatus, reqVO.getReviewStatus())
                .eqIfPresent(ImMessageReviewDO::getMsgType, reqVO.getMsgType())
                .eqIfPresent(ImMessageReviewDO::getChatType, reqVO.getChatType())
                .eqIfPresent(ImMessageReviewDO::getSenderId, reqVO.getSenderId())
                .betweenIfPresent(ImMessageReviewDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImMessageReviewDO::getId));
    }

}
