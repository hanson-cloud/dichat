package com.diqin.cloud.module.system.api.logger;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogPageReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogRespDTO;
import com.diqin.cloud.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 登录日志")
public interface LoginLogApi {

    String PREFIX = ApiConstants.PREFIX + "/login-log";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建登录日志")
    CommonResult<Boolean> createLoginLog(@Valid @RequestBody LoginLogCreateReqDTO reqDTO);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得登录日志")
    CommonResult<LoginLogRespDTO> getLoginLog(@RequestParam("id") Long id);

    @PostMapping(PREFIX + "/page")
    @Operation(summary = "获得登录日志分页（RPC，供 IM 等模块代理查询）")
    CommonResult<PageResult<LoginLogRespDTO>> getLoginLogPage(@RequestBody LoginLogPageReqDTO pageReqVO);

    @PostMapping(PREFIX + "/force-offline")
    @Operation(summary = "强制下线指定登录记录")
    CommonResult<Boolean> forceOffline(@RequestParam("id") Long id);

    @PostMapping(PREFIX + "/update-status")
    @Operation(summary = "批量更新登录记录的会话状态")
    CommonResult<Boolean> updateStatus(@RequestParam("ids") List<Long> ids, @RequestParam("status") Integer status);

}
