package com.diqin.cloud.module.trade.convert.aftersale;

import com.diqin.cloud.module.trade.dal.dataobject.aftersale.AfterSaleLogDO;
import com.diqin.cloud.module.trade.service.aftersale.bo.AfterSaleLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AfterSaleLogConvert {

    AfterSaleLogConvert INSTANCE = Mappers.getMapper(AfterSaleLogConvert.class);

    AfterSaleLogDO convert(AfterSaleLogCreateReqBO bean);

}
