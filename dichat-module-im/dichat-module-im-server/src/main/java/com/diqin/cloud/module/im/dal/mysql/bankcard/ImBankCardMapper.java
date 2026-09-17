package com.diqin.cloud.module.im.dal.mysql.bankcard;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.bankcard.vo.ImBankCardManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bankcard.ImBankCardDO;
import com.diqin.cloud.module.im.enums.bankcard.ImBankCardStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * IM 银行卡 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImBankCardMapper extends BaseMapperX<ImBankCardDO> {

    default ImBankCardDO selectByUserIdAndCard(Long userId, String bankCode, String cardNoMask) {
        return selectOne(new LambdaQueryWrapperX<ImBankCardDO>()
                .eq(ImBankCardDO::getUserId, userId)
                .eq(ImBankCardDO::getBankCode, bankCode)
                .eq(ImBankCardDO::getCardNoMask, cardNoMask)
                .ne(ImBankCardDO::getStatus, ImBankCardStatusEnum.UNBIND.getStatus()));
    }

    default List<ImBankCardDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<ImBankCardDO>()
                .eq(ImBankCardDO::getUserId, userId)
                .ne(ImBankCardDO::getStatus, ImBankCardStatusEnum.UNBIND.getStatus())
                .orderByDesc(ImBankCardDO::getIsDefault)
                .orderByDesc(ImBankCardDO::getId));
    }

    default ImBankCardDO selectDefaultByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<ImBankCardDO>()
                .eq(ImBankCardDO::getUserId, userId)
                .eq(ImBankCardDO::getIsDefault, Boolean.TRUE)
                .ne(ImBankCardDO::getStatus, ImBankCardStatusEnum.UNBIND.getStatus()));
    }

    default PageResult<ImBankCardDO> selectPage(ImBankCardManagerPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImBankCardDO>()
                .eqIfPresent(ImBankCardDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ImBankCardDO::getStatus, reqVO.getStatus())
                .eqIfPresent(ImBankCardDO::getBankCode, reqVO.getBankCode())
                .betweenIfPresent(ImBankCardDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImBankCardDO::getId));
    }

}
