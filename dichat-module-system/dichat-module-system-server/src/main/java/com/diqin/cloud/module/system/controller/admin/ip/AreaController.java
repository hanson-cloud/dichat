package com.diqin.cloud.module.system.controller.admin.ip;

import jakarta.annotation.Resource;

import cn.hutool.core.lang.Assert;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.ip.core.Area;
import com.diqin.cloud.framework.ip.core.utils.AreaUtils;
import com.diqin.cloud.module.system.service.area.AreaService;
import com.diqin.cloud.module.system.controller.admin.ip.vo.AreaNodeRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 地区")
@RestController
@RequestMapping("/system/area")
@Validated
public class AreaController {

    @GetMapping("/tree")
    @Operation(summary = "获得地区树")
    public CommonResult<List<AreaNodeRespVO>> getAreaTree() {
        Area area = AreaUtils.getArea(Area.ID_CHINA);
        Assert.notNull(area, "获取不到中国");
        return success(BeanUtils.toBean(area.getChildren(), AreaNodeRespVO.class));
    }

    @Resource
    private AreaService areaService;

    @GetMapping("/get-by-ip")
    @Operation(summary = "获得 IP 对应的地区名")
    @Parameter(name = "ip", description = "IP", required = true)
    public CommonResult<String> getAreaByIp(@RequestParam("ip") String ip) {
        // 经 system 区域服务（ip-api.com 在线解析 + 静态区域树，不依赖 ip2region）得到 area_id
        Long areaId = areaService.getAreaIdByIp(ip);
        if (areaId == null) {
            return success("未知");
        }
        // 格式化返回（引用静态区域树 area.csv）
        return success(AreaUtils.format(areaId.intValue()));
    }

}
