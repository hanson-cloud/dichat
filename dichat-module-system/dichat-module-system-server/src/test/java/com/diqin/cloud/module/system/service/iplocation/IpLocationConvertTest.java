package com.diqin.cloud.module.system.service.iplocation;

import com.diqin.cloud.module.system.convert.iplocation.IpLocationConvert;
import com.diqin.cloud.module.system.dal.dataobject.iplocation.IpLocationDO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link IpLocationConvert} 的纯逻辑单元测试。
 * <p>不依赖 Spring 容器、不依赖 Redis / MySQL / 网络，仅校验 DO 与 Result 的全字段互转与 null 安全。</p>
 *
 * @author edward(QA)
 */
public class IpLocationConvertTest {

    // ==================== 测试固定数据（覆盖 ip-api 全字段） ====================

    private static final String IP = "112.65.0.1";
    private static final String LANG = "zh-CN";
    private static final String COUNTRY = "中国";
    private static final String REGION = "广东";
    private static final String CITY = "深圳";
    private static final String DISTRICT = "南山区";
    private static final String ISP = "China Telecom";
    private static final String LATITUDE = "22.5431";
    private static final String LONGITUDE = "114.0579";
    private static final String COUNTRY_CODE = "CN";
    private static final String REGION_CODE = "44";
    private static final String ZIP = "518000";
    private static final String TIMEZONE = "Asia/Shanghai";
    private static final String ORG = "Chinanet GD";
    private static final String ASN = "AS4134 Chinanet";

    /**
     * 构造填满全部业务字段的 DO（status 由调用方指定）
     */
    private static IpLocationDO buildDO(Integer status) {
        IpLocationDO dataObject = new IpLocationDO();
        dataObject.setId(1024L);
        dataObject.setIp(IP);
        dataObject.setLang(LANG);
        dataObject.setStatus(status);
        dataObject.setCountry(COUNTRY);
        dataObject.setRegion(REGION);
        dataObject.setCity(CITY);
        dataObject.setDistrict(DISTRICT);
        dataObject.setIsp(ISP);
        dataObject.setLatitude(LATITUDE);
        dataObject.setLongitude(LONGITUDE);
        dataObject.setCountryCode(COUNTRY_CODE);
        dataObject.setRegionCode(REGION_CODE);
        dataObject.setZip(ZIP);
        dataObject.setTimezone(TIMEZONE);
        dataObject.setOrg(ORG);
        dataObject.setAsn(ASN);
        return dataObject;
    }

    /**
     * 构造填满全部业务字段的 Result
     */
    private static IpLocationResult buildResult() {
        IpLocationResult dto = new IpLocationResult();
        dto.setIp(IP);
        dto.setLang(LANG);
        dto.setSuccess(true);
        dto.setCountry(COUNTRY);
        dto.setRegion(REGION);
        dto.setCity(CITY);
        dto.setDistrict(DISTRICT);
        dto.setIsp(ISP);
        dto.setLatitude(LATITUDE);
        dto.setLongitude(LONGITUDE);
        dto.setCountryCode(COUNTRY_CODE);
        dto.setRegionCode(REGION_CODE);
        dto.setZip(ZIP);
        dto.setTimezone(TIMEZONE);
        dto.setOrg(ORG);
        dto.setAsn(ASN);
        return dto;
    }

    // ==================== 1. DO -> Result 全字段 ====================

    @Test
    @DisplayName("DO→Result：全字段无遗漏、无错位，status=1 时 success=true")
    public void testConvertDoToResult_allFields() {
        // Arrange
        IpLocationDO dataObject = buildDO(1);

        // Act
        IpLocationResult dto = IpLocationConvert.convert(dataObject);

        // Assert：逐字段比对，防止漏字段 / 错位赋值
        assertEquals(IP, dto.getIp(), "ip 未正确映射");
        assertEquals(LANG, dto.getLang(), "lang 未正确映射");
        assertTrue(dto.isSuccess(), "status=1 应映射为 success=true");
        assertEquals(COUNTRY, dto.getCountry(), "country 未正确映射");
        assertEquals(REGION, dto.getRegion(), "region（省名）未正确映射");
        assertEquals(CITY, dto.getCity(), "city 未正确映射");
        assertEquals(DISTRICT, dto.getDistrict(), "district 未正确映射");
        assertEquals(ISP, dto.getIsp(), "isp 未正确映射");
        assertEquals(LATITUDE, dto.getLatitude(), "latitude 未正确映射");
        assertEquals(LONGITUDE, dto.getLongitude(), "longitude 未正确映射");
        assertEquals(COUNTRY_CODE, dto.getCountryCode(), "countryCode 未正确映射");
        assertEquals(REGION_CODE, dto.getRegionCode(), "regionCode（省代码）未正确映射");
        assertEquals(ZIP, dto.getZip(), "zip 未正确映射");
        assertEquals(TIMEZONE, dto.getTimezone(), "timezone 未正确映射");
        assertEquals(ORG, dto.getOrg(), "org 未正确映射");
        assertEquals(ASN, dto.getAsn(), "asn 未正确映射");
        // region 与 regionCode 不得互相错位（ip-api 的 region 是省代码、regionName 才是省名）
        assertEquals(REGION, dto.getRegion());
        assertEquals(REGION_CODE, dto.getRegionCode());
    }

    @Test
    @DisplayName("DO→Result：status=0（历史负缓存行）应映射为 success=false")
    public void testConvertDoToResult_statusFailed() {
        // Arrange
        IpLocationDO dataObject = buildDO(0);

        // Act
        IpLocationResult dto = IpLocationConvert.convert(dataObject);

        // Assert
        assertFalse(dto.isSuccess(), "status=0 应映射为 success=false，避免负缓存被当成成功结果下发");
        assertEquals(IP, dto.getIp());
    }

    // ==================== 2. Result -> DO 全字段 ====================

    @Test
    @DisplayName("Result→DO：全字段无遗漏、无错位")
    public void testConvertResultToDo_allFields() {
        // Arrange
        IpLocationResult dto = buildResult();

        // Act
        IpLocationDO dataObject = IpLocationConvert.convert(dto);

        // Assert：逐字段比对
        assertEquals(IP, dataObject.getIp(), "ip 未正确映射");
        assertEquals(LANG, dataObject.getLang(), "lang 未正确映射");
        assertEquals(COUNTRY, dataObject.getCountry(), "country 未正确映射");
        assertEquals(REGION, dataObject.getRegion(), "region（省名）未正确映射");
        assertEquals(CITY, dataObject.getCity(), "city 未正确映射");
        assertEquals(DISTRICT, dataObject.getDistrict(), "district 未正确映射");
        assertEquals(ISP, dataObject.getIsp(), "isp 未正确映射");
        assertEquals(LATITUDE, dataObject.getLatitude(), "latitude 未正确映射");
        assertEquals(LONGITUDE, dataObject.getLongitude(), "longitude 未正确映射");
        assertEquals(COUNTRY_CODE, dataObject.getCountryCode(), "countryCode 未正确映射");
        assertEquals(REGION_CODE, dataObject.getRegionCode(), "regionCode（省代码）未正确映射");
        assertEquals(ZIP, dataObject.getZip(), "zip 未正确映射");
        assertEquals(TIMEZONE, dataObject.getTimezone(), "timezone 未正确映射");
        assertEquals(ORG, dataObject.getOrg(), "org 未正确映射");
        assertEquals(ASN, dataObject.getAsn(), "asn 未正确映射");
        // 契约说明：status 不由 convert 负责，由 IpLocationServiceImpl#upsert 显式设置
        assertNull(dataObject.getStatus(), "status 应保持 null，由 upsert 统一赋值");
        assertNull(dataObject.getId(), "id 应保持 null，由 upsert 按 existingId 决定 insert / updateById");
    }

    // ==================== 3. 往返一致性 ====================

    @Test
    @DisplayName("往返转换：DO→Result→DO 业务字段保持一致")
    public void testRoundTrip_keepsAllBusinessFields() {
        // Arrange
        IpLocationDO source = buildDO(1);

        // Act
        IpLocationDO target = IpLocationConvert.convert(IpLocationConvert.convert(source));

        // Assert
        assertEquals(source.getIp(), target.getIp());
        assertEquals(source.getLang(), target.getLang());
        assertEquals(source.getCountry(), target.getCountry());
        assertEquals(source.getRegion(), target.getRegion());
        assertEquals(source.getCity(), target.getCity());
        assertEquals(source.getDistrict(), target.getDistrict());
        assertEquals(source.getIsp(), target.getIsp());
        assertEquals(source.getLatitude(), target.getLatitude());
        assertEquals(source.getLongitude(), target.getLongitude());
        assertEquals(source.getCountryCode(), target.getCountryCode());
        assertEquals(source.getRegionCode(), target.getRegionCode());
        assertEquals(source.getZip(), target.getZip());
        assertEquals(source.getTimezone(), target.getTimezone());
        assertEquals(source.getOrg(), target.getOrg());
        assertEquals(source.getAsn(), target.getAsn());
    }

    // ==================== 4. null 安全 ====================

    @Test
    @DisplayName("null 安全：两个方向的 null 入参均返回 null")
    public void testConvert_nullSafe() {
        assertNull(IpLocationConvert.convert((IpLocationDO) null), "convert((IpLocationDO) null) 应返回 null");
        assertNull(IpLocationConvert.convert((IpLocationResult) null), "convert((IpLocationResult) null) 应返回 null");
    }

}
