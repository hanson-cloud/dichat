package com.diqin.cloud.module.im.dal.mysql.bank;

import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * IM 支持的银行 Mapper
 *
 * @author dichat
 */
@Mapper
public interface ImBankMapper extends BaseMapperX<ImBankDO> {

}
