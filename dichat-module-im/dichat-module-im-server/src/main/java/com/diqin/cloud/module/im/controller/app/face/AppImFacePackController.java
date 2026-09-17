package com.diqin.cloud.module.im.controller.app.face;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.face.vo.pack.AppImFacePackUserRespVO;
import com.diqin.cloud.module.im.dal.dataobject.face.ImFacePackDO;
import com.diqin.cloud.module.im.dal.dataobject.face.ImFacePackItemDO;
import com.diqin.cloud.module.im.service.face.ImFacePackItemService;
import com.diqin.cloud.module.im.service.face.ImFacePackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertList;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertMultiMap;

@Tag(name = "管理后台 - IM 表情包")
@RestController
@RequestMapping("/im/face-pack")
@Validated
public class AppImFacePackController {

    @Resource
    private ImFacePackService facePackService;
    @Resource
    private ImFacePackItemService facePackItemService;

    @GetMapping("/list")
    @Operation(summary = "获得启用的表情包列表（含表情）")
    public CommonResult<List<AppImFacePackUserRespVO>> getFacePackList() {
        // 1.1 拉所有启用表情包
        List<ImFacePackDO> packs = facePackService.getEnabledFacePackList();
        if (packs.isEmpty()) {
            return success(List.of());
        }
        // 1.2 拉这些包下所有启用表情，按 packId 分组
        List<ImFacePackItemDO> items = facePackItemService.getEnabledItemListByPackIds(
                convertList(packs, ImFacePackDO::getId));
        Map<Long, List<ImFacePackItemDO>> itemsByPackId = convertMultiMap(items, ImFacePackItemDO::getPackId);

        // 2. 拼装：BeanUtils 把 pack 字段映射 + 自己塞 items
        List<AppImFacePackUserRespVO> result = convertList(packs, pack -> {
            AppImFacePackUserRespVO vo = BeanUtils.toBean(pack, AppImFacePackUserRespVO.class);
            vo.setItems(BeanUtils.toBean(itemsByPackId.getOrDefault(pack.getId(), List.of()), AppImFacePackUserRespVO.Item.class));
            return vo;
        });
        return success(result);
    }

}
