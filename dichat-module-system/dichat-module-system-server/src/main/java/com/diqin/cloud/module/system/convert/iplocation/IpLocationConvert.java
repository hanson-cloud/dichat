package com.diqin.cloud.module.system.convert.iplocation;

import com.diqin.cloud.module.system.dal.dataobject.iplocation.IpLocationDO;
import com.diqin.cloud.module.system.service.iplocation.IpLocationResult;

/**
 * IP 定位 DO 与 Result 互转（字段一一对应，无 MapStruct 依赖）。
 */
public class IpLocationConvert {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 0;

    public static IpLocationResult convert(IpLocationDO bean) {
        if (bean == null) {
            return null;
        }
        IpLocationResult dto = new IpLocationResult();
        dto.setIp(bean.getIp());
        dto.setLang(bean.getLang());
        dto.setSuccess(STATUS_SUCCESS == bean.getStatus());
        dto.setCountry(bean.getCountry());
        dto.setRegion(bean.getRegion());
        dto.setCity(bean.getCity());
        dto.setDistrict(bean.getDistrict());
        dto.setIsp(bean.getIsp());
        dto.setLatitude(bean.getLatitude());
        dto.setLongitude(bean.getLongitude());
        dto.setCountryCode(bean.getCountryCode());
        dto.setRegionCode(bean.getRegionCode());
        dto.setZip(bean.getZip());
        dto.setTimezone(bean.getTimezone());
        dto.setOrg(bean.getOrg());
        dto.setAsn(bean.getAsn());
        return dto;
    }

    public static IpLocationDO convert(IpLocationResult dto) {
        if (dto == null) {
            return null;
        }
        IpLocationDO dataObject = new IpLocationDO();
        dataObject.setIp(dto.getIp());
        dataObject.setLang(dto.getLang());
        dataObject.setCountry(dto.getCountry());
        dataObject.setRegion(dto.getRegion());
        dataObject.setCity(dto.getCity());
        dataObject.setDistrict(dto.getDistrict());
        dataObject.setIsp(dto.getIsp());
        dataObject.setLatitude(dto.getLatitude());
        dataObject.setLongitude(dto.getLongitude());
        dataObject.setCountryCode(dto.getCountryCode());
        dataObject.setRegionCode(dto.getRegionCode());
        dataObject.setZip(dto.getZip());
        dataObject.setTimezone(dto.getTimezone());
        dataObject.setOrg(dto.getOrg());
        dataObject.setAsn(dto.getAsn());
        return dataObject;
    }

}
