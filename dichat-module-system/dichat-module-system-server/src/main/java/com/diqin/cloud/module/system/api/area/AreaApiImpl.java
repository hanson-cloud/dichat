package com.diqin.cloud.module.system.api.area;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.system.api.area.dto.AreaRegionRespDTO;
import com.diqin.cloud.module.system.service.area.AreaService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AreaApi 实现（提供 RESTful API 接口，给 Feign 调用）
 *
 * @author hanson
 */
@RestController
@Validated
@AllArgsConstructor
public class AreaApiImpl implements AreaApi {

    private final AreaService areaService;

    @Override
    public CommonResult<Long> getByIp(String ip) {
        return CommonResult.success(areaService.getAreaIdByIp(ip));
    }

    @Override
    public CommonResult<AreaRegionRespDTO> getRegionByIp(String ip) {
        return CommonResult.success(areaService.getRegionByIp(ip));
    }

    @Override
    public CommonResult<List<AreaRegionRespDTO>> getAreaListByAreaIds(List<Long> ids) {
        return CommonResult.success(areaService.getAreaListByAreaIds(ids));
    }

}
