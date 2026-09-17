package com.diqin.cloud.module.pay.dal.mysql.demo;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.pay.dal.dataobject.demo.PayDemoOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 示例订单 Mapper
 *
 * @author hanson
 */
@Mapper
public interface PayDemoOrderMapper extends BaseMapperX<PayDemoOrderDO> {

    default PageResult<PayDemoOrderDO> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PayDemoOrderDO>()
                .orderByDesc(PayDemoOrderDO::getId));
    }

    default int updateByIdAndPayed(Long id, boolean wherePayed, PayDemoOrderDO updateObj) {
        return update(updateObj, new LambdaQueryWrapperX<PayDemoOrderDO>()
                .eq(PayDemoOrderDO::getId, id).eq(PayDemoOrderDO::getPayStatus, wherePayed));
    }

}
