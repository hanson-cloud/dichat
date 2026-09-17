package com.diqin.cloud.module.pay.dal.mysql.wallet;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletRechargeDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface PayWalletRechargeMapper extends BaseMapperX<PayWalletRechargeDO> {

    default int updateByIdAndPaid(Long id, boolean wherePayStatus, PayWalletRechargeDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<PayWalletRechargeDO>()
                .eq(PayWalletRechargeDO::getId, id).eq(PayWalletRechargeDO::getPayStatus, wherePayStatus));
    }

    default int updateByIdAndRefunded(Long id, Integer whereRefundStatus, PayWalletRechargeDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<PayWalletRechargeDO>()
                .eq(PayWalletRechargeDO::getId, id).eq(PayWalletRechargeDO::getRefundStatus, whereRefundStatus));
    }

    default PageResult<PayWalletRechargeDO> selectPage(PageParam pageReqVO, Long walletId, Boolean payStatus) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<PayWalletRechargeDO>()
                .eq(PayWalletRechargeDO::getWalletId, walletId)
                .eq(PayWalletRechargeDO::getPayStatus, payStatus)
                .orderByDesc(PayWalletRechargeDO::getId));
    }

    default PageResult<PayWalletRechargeDO> selectAdminPage(PageParam pageReqVO, Boolean payStatus, LocalDateTime[] createTime) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<PayWalletRechargeDO>()
                .eq(payStatus != null, PayWalletRechargeDO::getPayStatus, payStatus)
                .betweenIfPresent(PayWalletRechargeDO::getCreateTime, createTime)
                .orderByDesc(PayWalletRechargeDO::getId));
    }

}