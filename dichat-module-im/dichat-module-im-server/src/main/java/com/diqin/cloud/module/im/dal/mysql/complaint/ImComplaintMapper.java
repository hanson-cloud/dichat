package com.diqin.cloud.module.im.dal.mysql.complaint;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.complaint.ImComplaintDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 用户投诉（举报） Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImComplaintMapper extends BaseMapperX<ImComplaintDO> {

    default PageResult<ImComplaintDO> selectPage(ImComplaintManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImComplaintDO>()
                .eqIfPresent(ImComplaintDO::getComplainantId, reqVO.getComplainantId())
                .eqIfPresent(ImComplaintDO::getRespondentId, reqVO.getRespondentId())
                .eqIfPresent(ImComplaintDO::getCategory, reqVO.getCategory())
                .eqIfPresent(ImComplaintDO::getStatus, reqVO.getStatus())
                .likeIfPresent(ImComplaintDO::getContent, reqVO.getContent())
                .betweenIfPresent(ImComplaintDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImComplaintDO::getId));
    }

}
