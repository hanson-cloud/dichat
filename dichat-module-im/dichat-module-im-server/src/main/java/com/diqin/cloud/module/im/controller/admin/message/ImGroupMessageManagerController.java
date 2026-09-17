package com.diqin.cloud.module.im.controller.admin.message;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.excel.core.util.ExcelUtils;
import com.diqin.cloud.module.im.controller.admin.message.vo.group.ImGroupMessageExcelVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.group.ImGroupMessageManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.group.ImGroupMessageManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImGroupMessageDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.group.ImGroupService;
import com.diqin.cloud.module.im.service.message.ImGroupMessageService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.*;
import static com.diqin.cloud.module.im.enums.ImCommonConstants.AT_USER_ID_ALL;

@Tag(name = "管理后台 - IM 群聊消息")
@RestController
@RequestMapping("/im/manager/message/group")
@Validated
public class ImGroupMessageManagerController {

    @Resource
    private ImGroupMessageService groupMessageService;
    @Resource
    private ImGroupService groupService;
    @Resource
    private ImUserService userService;

    @GetMapping("/page")
    @Operation(summary = "获得群聊消息分页")
    @PreAuthorize("@ss.hasPermission('im:manager:message:query')")
    public CommonResult<PageResult<ImGroupMessageManagerRespVO>> getGroupMessagePage(
            @Valid ImGroupMessageManagerPageReqVO pageReqVO) {
        // 1. 分页查询
        PageResult<ImGroupMessageDO> pageResult = groupMessageService.getGroupMessagePage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        // 2.1 批量查询群名称、发送人昵称、@ 用户昵称（-1 表示 @所有人，跳过查询，由前端判断渲染）
        Map<Long, ImGroupDO> groupMap = groupService.getGroupMap(
                convertSet(pageResult.getList(), ImGroupMessageDO::getGroupId));
        Set<Long> userIds = convertSetByFlatMap(pageResult.getList(), m -> Stream.concat(
                Stream.of(m.getSenderId()),
                CollUtil.emptyIfNull(m.getAtUserIds()).stream()
                        .filter(id -> !Objects.equals(id, AT_USER_ID_ALL))));
        Map<Long, ImUserDO> userMap = userService.getUserMap(userIds);
        // 2.2 转换为 VO，填充群名 / 发送人昵称 / @ 用户昵称（-1 位置留 null，由前端展示「@所有人」）
        return success(BeanUtils.toBean(pageResult, ImGroupMessageManagerRespVO.class, vo -> {
            MapUtils.findAndThen(groupMap, vo.getGroupId(), group -> vo.setGroupName(group.getName()));
            MapUtils.findAndThen(userMap, vo.getSenderId(), user -> vo.setSenderNickname(user.getNickname()));
            if (CollUtil.isNotEmpty(vo.getAtUserIds())) {
                vo.setAtUserNicknames(convertList(vo.getAtUserIds(), id -> {
                    ImUserDO user = userMap.get(id);
                    return user != null ? user.getNickname() : null;
                }));
            }
        }));
    }

    @GetMapping("/get")
    @Operation(summary = "获得群聊消息详情")
    @Parameter(name = "id", description = "消息编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('im:manager:message:query')")
    public CommonResult<ImGroupMessageManagerRespVO> getGroupMessage(@RequestParam("id") Long id) {
        ImGroupMessageDO message = groupMessageService.getGroupMessage(id);
        return success(BeanUtils.toBean(message, ImGroupMessageManagerRespVO.class));
    }

    @GetMapping("/export")
    @Operation(summary = "导出群聊消息 Excel")
    @PreAuthorize("@ss.hasPermission('im:manager:message:group:export')")
    public void exportGroupMessageExcel(@Valid ImGroupMessageManagerPageReqVO reqVO,
            HttpServletResponse response) throws IOException {
        // 1. 查询（按筛选条件全量）
        List<ImGroupMessageDO> list = groupMessageService.getGroupMessageExportList(reqVO);
        if (CollUtil.isEmpty(list)) {
            ExcelUtils.write(response, "群聊消息.xls", "数据", ImGroupMessageExcelVO.class, List.of());
            return;
        }
        // 2. 批量回填群名称 / 发送人昵称
        Map<Long, ImGroupDO> groupMap = groupService.getGroupMap(
                convertSet(list, ImGroupMessageDO::getGroupId));
        Map<Long, ImUserDO> userMap = userService.getUserMap(
                convertSet(list, ImGroupMessageDO::getSenderId));
        List<ImGroupMessageExcelVO> vos = BeanUtils.toBean(list, ImGroupMessageExcelVO.class, vo -> {
            MapUtils.findAndThen(groupMap, vo.getGroupId(), group -> vo.setGroupName(group.getName()));
            MapUtils.findAndThen(userMap, vo.getSenderId(), user -> vo.setSenderNickname(user.getNickname()));
        });
        // 3. 导出
        ExcelUtils.write(response, "群聊消息.xls", "数据", ImGroupMessageExcelVO.class, vos);
    }

}
