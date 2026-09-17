package com.diqin.cloud.module.im.controller.admin.face;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.face.vo.useritem.ImFaceUserItemManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.face.vo.useritem.ImFaceUserItemManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.face.ImFaceUserItemDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.face.ImFaceUserItemService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - IM 用户表情")
@RestController
@RequestMapping("/im/manager/face-user-item")
@Validated
public class ImFaceUserItemManagerController {

    @Resource
    private ImFaceUserItemService faceUserItemService;
    @Resource
    private ImUserService userService;

    @GetMapping("/page")
    @Operation(summary = "获得用户表情分页")
    @PreAuthorize("@ss.hasPermission('im:manager:face-user-item:query')")
    public CommonResult<PageResult<ImFaceUserItemManagerRespVO>> getFaceUserItemPage(
            @Valid ImFaceUserItemManagerPageReqVO pageReqVO) {
        PageResult<ImFaceUserItemDO> pageResult = faceUserItemService.getFaceUserItemPage(pageReqVO);
        // 关联回填用户昵称
        List<ImUserDO> userList = userService.getUserList(convertSet(pageResult.getList(), ImFaceUserItemDO::getUserId));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userList, ImUserDO::getId);
        List<ImFaceUserItemManagerRespVO> voList = CollectionUtils.convertList(pageResult.getList(), item -> {
            ImFaceUserItemManagerRespVO vo = BeanUtils.toBean(item, ImFaceUserItemManagerRespVO.class);
            ImUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                vo.setUserNickname(user.getNickname());
            }
            return vo;
        });
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户表情")
    @Parameter(name = "id", description = "编号", required = true, example = "4096")
    @PreAuthorize("@ss.hasPermission('im:manager:face-user-item:delete')")
    public CommonResult<Boolean> deleteFaceUserItem(@RequestParam("id") Long id) {
        faceUserItemService.deleteFaceUserItem(id);
        return success(true);
    }

}
