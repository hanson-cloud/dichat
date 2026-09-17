package com.diqin.cloud.module.im.dal.mysql.withdraw;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawDO;
import com.diqin.cloud.module.im.enums.withdraw.ImWithdrawStatusEnum;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 提现 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImWithdrawMapper extends BaseMapperX<ImWithdrawDO> {

    default ImWithdrawDO selectByNo(String no) {
        return selectOne(ImWithdrawDO::getNo, no);
    }

    default PageResult<ImWithdrawDO> selectPageByUser(Long userId, PageParam pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<ImWithdrawDO>()
                .eq(ImWithdrawDO::getUserId, userId)
                .orderByDesc(ImWithdrawDO::getId));
    }

    default PageResult<ImWithdrawDO> selectPage(ImWithdrawManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImWithdrawDO>()
                .eqIfPresent(ImWithdrawDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ImWithdrawDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImWithdrawDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImWithdrawDO::getId));
    }

    /**
     * 该用户是否存在进行中（待审核）的提现申请，用于创建时防重复提交
     */
    default boolean existsPendingByUserId(Long userId) {
        return selectCount(new LambdaQueryWrapperX<ImWithdrawDO>()
                .eq(ImWithdrawDO::getUserId, userId)
                .eq(ImWithdrawDO::getStatus, ImWithdrawStatusEnum.PENDING.getStatus())) > 0;
    }

}
