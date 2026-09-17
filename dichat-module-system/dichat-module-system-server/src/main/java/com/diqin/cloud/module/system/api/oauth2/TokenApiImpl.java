package com.diqin.cloud.module.system.api.oauth2;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.system.service.oauth2.OAuth2TokenService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class TokenApiImpl implements TokenApi {

    @Resource
    private OAuth2TokenService oauth2TokenService;

    @Override
    public CommonResult<Boolean> revokeToken(String accessToken) {
        // removeAccessToken(String) 会一并删除关联的刷新令牌，确保该设备无法续期
        oauth2TokenService.removeAccessToken(accessToken);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> revokeByUser(Long userId, Integer userType) {
        // 无活跃会话时的兜底：吊销该用户全部令牌（access + refresh）
        oauth2TokenService.removeAccessToken(userId, userType);
        return success(true);
    }

}
