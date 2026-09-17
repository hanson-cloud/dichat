package com.diqin.cloud.module.im.controller.app.face;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.face.vo.userItem.AppImFaceUserItemRespVO;
import com.diqin.cloud.module.im.controller.app.face.vo.userItem.AppImFaceUserItemSaveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.face.ImFaceUserItemDO;
import com.diqin.cloud.module.im.service.face.ImFaceUserItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 个人表情")
@RestController
@RequestMapping("/im/face-user-item")
@Validated
public class AppImFaceUserItemController {

    @Resource
    private ImFaceUserItemService faceUserItemService;

    @GetMapping("/list")
    @Operation(summary = "获得我的个人表情列表")
    public CommonResult<List<AppImFaceUserItemRespVO>> getFaceUserItemList() {
        List<ImFaceUserItemDO> items = faceUserItemService.getFaceUserItemList(getLoginUserId());
        return success(BeanUtils.toBean(items, AppImFaceUserItemRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "添加个人表情")
    public CommonResult<Long> createFaceUserItem(@Valid @RequestBody AppImFaceUserItemSaveReqVO reqVO) {
        return success(faceUserItemService.createFaceUserItem(getLoginUserId(), reqVO));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除个人表情")
    @Parameter(name = "id", description = "编号", required = true, example = "4096")
    public CommonResult<Boolean> deleteFaceUserItem(@RequestParam("id") Long id) {
        faceUserItemService.deleteFaceUserItem(getLoginUserId(), id);
        return success(true);
    }

}
