package com.diqin.cloud.module.system.api.oauth2;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 令牌")
public interface TokenApi {

    String PREFIX = ApiConstants.PREFIX + "/oauth2";

    @PostMapping(PREFIX + "/revoke")
    @Operation(summary = "吊销指定的访问令牌（强制下线时精确踢单台设备）")
    CommonResult<Boolean> revokeToken(@RequestParam("accessToken") String accessToken);

    @PostMapping(PREFIX + "/revoke-by-user")
    @Operation(summary = "吊销指定用户全部令牌（强制下线时无活跃会话时的兜底）")
    CommonResult<Boolean> revokeByUser(@RequestParam("userId") Long userId,
                                       @RequestParam("userType") Integer userType);

}
