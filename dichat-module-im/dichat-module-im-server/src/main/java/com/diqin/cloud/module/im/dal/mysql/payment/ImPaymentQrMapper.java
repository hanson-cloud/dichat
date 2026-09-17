package com.diqin.cloud.module.im.dal.mysql.payment;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.dal.dataobject.payment.ImPaymentQrDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImPaymentQrMapper extends BaseMapperX<ImPaymentQrDO> {

    default ImPaymentQrDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<ImPaymentQrDO>()
                .eq(ImPaymentQrDO::getCode, code));
    }

    default List<ImPaymentQrDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<ImPaymentQrDO>()
                .eq(ImPaymentQrDO::getUserId, userId)
                .orderByDesc(ImPaymentQrDO::getId));
    }

    default void delete(Long id, Long userId) {
        this.delete(Wrappers.<ImPaymentQrDO>lambdaQuery()
                .eq(ImPaymentQrDO::getId, id)
                .eq(ImPaymentQrDO::getUserId, userId));
    }
}
