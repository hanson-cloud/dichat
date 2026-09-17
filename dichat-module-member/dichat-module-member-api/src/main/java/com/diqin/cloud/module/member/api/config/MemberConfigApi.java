package com.diqin.cloud.module.member.api.config;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.member.api.config.dto.MemberConfigRespDTO;
import com.diqin.cloud.module.member.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = ApiConstants.NAME) 
@Tag(name = "RPC 服务 - 用户配置")
public interface MemberConfigApi {

    String PREFIX = ApiConstants.PREFIX + "/config";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得用户配置")
    CommonResult<MemberConfigRespDTO> getConfig();

}
