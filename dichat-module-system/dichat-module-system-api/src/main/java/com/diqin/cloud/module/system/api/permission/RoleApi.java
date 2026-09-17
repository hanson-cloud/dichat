package com.diqin.cloud.module.system.api.permission;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.module.system.api.permission.dto.RoleRespDTO;
import com.diqin.cloud.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@FeignClient(name = ApiConstants.NAME) 
@Tag(name = "RPC 服务 - 角色")
public interface RoleApi {

    String PREFIX = ApiConstants.PREFIX + "/role";

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验角色是否合法")
    @Parameter(name = "ids", description = "角色编号数组", example = "1,2", required = true)
    CommonResult<Boolean> validRoleList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得角色")
    @Parameter(name = "id", description = "角色编号", example = "1", required = true)
    CommonResult<RoleRespDTO> getRole(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得角色列表")
    @Parameter(name = "ids", description = "角色编号数组", example = "1,2", required = true)
    CommonResult<List<RoleRespDTO>> getRoleList(@RequestParam("ids") Collection<Long> ids);

    default Map<Long, RoleRespDTO> getRoleMap(Collection<Long> ids) {
        return CollectionUtils.convertMap(getRoleList(ids).getCheckedData(), RoleRespDTO::getId);
    }

}
