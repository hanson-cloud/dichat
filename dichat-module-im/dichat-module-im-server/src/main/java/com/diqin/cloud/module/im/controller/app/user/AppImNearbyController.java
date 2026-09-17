package com.diqin.cloud.module.im.controller.app.user;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.im.controller.app.user.vo.AppImNearbyUserRespVO;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.diqin.cloud.module.im.service.user.bo.NearbyUserBO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "用户APP - 附近的人")
@RestController
@RequestMapping("/im/user/nearby")
@Validated
public class AppImNearbyController {

    @Resource
    private ImUserService userService;

    @GetMapping
    @Operation(summary = "附近的人",
            description = "基于地理位置返回附近用户，按距离由近及远排序；仅返回开启「允许附近的人看到我」的用户。")
    public CommonResult<List<AppImNearbyUserRespVO>> getNearbyUsers(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "3000") Integer radius,
            @RequestParam(defaultValue = "50") Integer limit) {
        validateCoord(lat, lng);
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        List<NearbyUserBO> bos = userService.getNearbyUsers(lat, lng, userId, radius, limit);
        List<AppImNearbyUserRespVO> result = bos.stream()
                .map(bo -> {
                    AppImNearbyUserRespVO vo = BeanUtils.toBean(bo.getUser(), AppImNearbyUserRespVO.class);
                    vo.setDistance(bo.getDistance() == null ? null : Math.round(bo.getDistance()));
                    return vo;
                })
                .collect(Collectors.toList());
        return success(result);
    }

    /**
     * 校验经纬度合法性：非空 + 纬度 [-90,90] + 经度 [-180,180]
     */
    private void validateCoord(Double lat, Double lng) {
        if (lat == null || lng == null
                || lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.NEARBY_LOCATION_INVALID);
        }
    }
}
