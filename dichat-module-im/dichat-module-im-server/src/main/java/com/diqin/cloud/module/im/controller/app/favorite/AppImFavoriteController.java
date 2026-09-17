package com.diqin.cloud.module.im.controller.app.favorite;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.favorite.vo.AppImFavoriteCreateReqVO;
import com.diqin.cloud.module.im.controller.app.favorite.vo.AppImFavoriteRespVO;
import com.diqin.cloud.module.im.dal.dataobject.favorite.ImUserFavoriteDO;
import com.diqin.cloud.module.im.service.favorite.ImUserFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 收藏")
@RestController
@RequestMapping("/im/favorite")
@Validated
public class AppImFavoriteController {

    @Resource
    private ImUserFavoriteService favoriteService;

    @PostMapping("/create")
    @Operation(summary = "收藏消息")
    public CommonResult<Long> createFavorite(@Valid @RequestBody AppImFavoriteCreateReqVO reqVO) {
        Long userId = getLoginUserId();
        Long id = favoriteService.createFavorite(userId, reqVO.getMessageId(), reqVO.getMessageType(),
                reqVO.getMessageContent(), reqVO.getSenderName(), reqVO.getConvKey());
        return success(id);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "取消收藏")
    public CommonResult<Boolean> deleteFavorite(@RequestParam Long id) {
        favoriteService.deleteFavorite(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取收藏列表")
    public CommonResult<List<AppImFavoriteRespVO>> getFavoriteList() {
        List<ImUserFavoriteDO> list = favoriteService.getFavoriteList(getLoginUserId());
        return success(BeanUtils.toBean(list, AppImFavoriteRespVO.class));
    }

    @GetMapping("/check")
    @Operation(summary = "检查消息是否已收藏")
    public CommonResult<Boolean> checkFavorited(@RequestParam Long messageId) {
        return success(favoriteService.isFavorited(getLoginUserId(), messageId));
    }
}
