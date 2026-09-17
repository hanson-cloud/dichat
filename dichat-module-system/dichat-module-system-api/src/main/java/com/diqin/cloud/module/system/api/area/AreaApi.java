package com.diqin.cloud.module.system.api.area;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.system.api.area.dto.AreaRegionRespDTO;
import com.diqin.cloud.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * RPC 服务 - 区域数据
 * <p>
 * 提供「IP → 区域编号 area_id」「area_id → 区域节点信息」两类能力，
 * 底层基于 ip-api.com 在线解析 + 静态区域树（{@code area.csv}，{@code AreaUtils}）映射，
 * <b>不依赖 ip2region</b>。供 IM 等模块做登录归属地解析与看板聚合。
 *
 * @author hanson
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 区域数据")
public interface AreaApi {

    String PREFIX = ApiConstants.PREFIX + "/area";

    @GetMapping(PREFIX + "/get-by-ip")
    @Operation(summary = "根据 IP 获取区域编号 area_id")
    @Parameter(name = "ip", description = "IP", example = "127.0.0.1", required = true)
    CommonResult<Long> getByIp(@RequestParam("ip") String ip);

    @GetMapping(PREFIX + "/get-region-by-ip")
    @Operation(summary = "根据 IP 获取区域节点（含 area_id / 名称 / 父级 / 类型）")
    @Parameter(name = "ip", description = "IP", example = "127.0.0.1", required = true)
    CommonResult<AreaRegionRespDTO> getRegionByIp(@RequestParam("ip") String ip);

    @PostMapping(PREFIX + "/list-by-ids")
    @Operation(summary = "批量获取区域节点信息（含父级与类型，供看板拼树 / 出名）")
    @Parameter(name = "ids", description = "区域编号数组", example = "1,2", required = true)
    CommonResult<List<AreaRegionRespDTO>> getAreaListByAreaIds(@RequestParam("ids") List<Long> ids);

}
