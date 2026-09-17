package com.diqin.cloud.module.system.dal.mysql.iplocation;

import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.module.system.dal.dataobject.iplocation.IpLocationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IpLocationMapper extends BaseMapperX<IpLocationDO> {

    /**
     * 按 IP 查询缓存记录（语种固定 zh-CN，单条）。命中返回行，否则 null。
     */
    default IpLocationDO selectByIp(String ip) {
        return selectOne(IpLocationDO::getIp, ip);
    }

}
