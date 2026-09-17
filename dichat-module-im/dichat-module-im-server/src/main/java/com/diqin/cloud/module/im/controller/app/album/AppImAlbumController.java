package com.diqin.cloud.module.im.controller.app.album;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.album.vo.AppImAlbumCreateReqVO;
import com.diqin.cloud.module.im.controller.app.album.vo.AppImAlbumRespVO;
import com.diqin.cloud.module.im.dal.dataobject.album.ImUserAlbumDO;
import com.diqin.cloud.module.im.service.album.ImUserAlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 相册")
@RestController
@RequestMapping("/im/album")
@Validated
public class AppImAlbumController {

    @Resource
    private ImUserAlbumService albumService;

    @PostMapping("/create")
    @Operation(summary = "上传相册照片")
    public CommonResult<Long> createAlbum(@Valid @RequestBody AppImAlbumCreateReqVO reqVO) {
        Long userId = getLoginUserId();
        Long id = albumService.createAlbum(userId, reqVO.getImageUrl(), reqVO.getThumbUrl());
        return success(id);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除相册照片")
    public CommonResult<Boolean> deleteAlbum(@RequestParam Long id) {
        albumService.deleteAlbum(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取我的相册列表")
    public CommonResult<List<AppImAlbumRespVO>> getAlbumList() {
        List<ImUserAlbumDO> list = albumService.getAlbumList(getLoginUserId());
        return success(BeanUtils.toBean(list, AppImAlbumRespVO.class));
    }
}
