package com.diqin.cloud.module.im.controller.admin.message;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.excel.core.util.ExcelUtils;
import com.diqin.cloud.module.im.controller.admin.message.vo.privates.ImPrivateMessageExcelVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.privates.ImPrivateMessageManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.privates.ImPrivateMessageManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceManagerService;
import com.diqin.cloud.module.im.service.message.ImPrivateMessageService;
import com.diqin.cloud.module.im.service.robot.ImRobotManagerService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 私聊消息")
@RestController
@RequestMapping("/im/manager/message/private")
@Validated
public class ImPrivateMessageManagerController {

    @Resource
    private ImPrivateMessageService privateMessageService;
    @Resource
    private ImUserService userService;
    @Resource
    private ImRobotManagerService robotManagerService;
    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;

    @GetMapping("/page")
    @Operation(summary = "获得私聊消息分页")
    @PreAuthorize("@ss.hasPermission('im:manager:message:query')")
    public CommonResult<PageResult<ImPrivateMessageManagerRespVO>> getPrivateMessagePage(
            @Valid ImPrivateMessageManagerPageReqVO pageReqVO) {
        // 1. 分页查询
        PageResult<ImPrivateMessageDO> pageResult = privateMessageService.getPrivateMessagePage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        // 2. 按参与方类型分别批量拉取昵称来源（im_users / im_robot / im_customer_service）
        Map<Long, ImUserDO> userMap;
        Map<Long, ImRobotDO> robotMap;
        Map<Long, ImCustomerServiceDO> csMap;
        if (!pageResult.getList().isEmpty()) {
            Set<Long> userIds = new HashSet<>();
            Set<Long> robotIds = new HashSet<>();
            Set<Long> csIds = new HashSet<>();
            splitParticipantIds(pageResult.getList(), userIds, robotIds, csIds);
            if (!userIds.isEmpty()) {
                userMap = userService.getUserMap(userIds);
            } else {
                userMap = MapUtil.empty();
            }
            if (!robotIds.isEmpty()) {
                robotMap = robotManagerService.getRobotMap(robotIds);
            } else {
                robotMap = MapUtil.empty();
            }
            if (!csIds.isEmpty()) {
                csMap = customerServiceManagerService.getCustomerServiceMap(csIds);
            } else {
                csMap = MapUtil.empty();
            }
        } else {
            csMap = MapUtil.empty();
            robotMap = MapUtil.empty();
            userMap = MapUtil.empty();
        }
        // 3. 转换为 VO，按类型回填发送人 / 接收人昵称
        return success(BeanUtils.toBean(pageResult, ImPrivateMessageManagerRespVO.class, vo -> {
            vo.setSenderNickname(resolveNickname(vo.getSenderType(), vo.getSenderId(), userMap, robotMap, csMap));
            vo.setReceiverNickname(resolveNickname(vo.getReceiverType(), vo.getReceiverId(), userMap, robotMap, csMap));
        }));
    }

    /**
     * 按消息的参与方类型，把发送人 / 接收人编号归集到对应的 id 集合中，
     * 以便分别向 im_users / im_robot / im_customer_service 批量查询昵称。
     */
    private void splitParticipantIds(List<ImPrivateMessageDO> messages,
                                     Set<Long> userIds, Set<Long> robotIds, Set<Long> csIds) {
        for (ImPrivateMessageDO m : messages) {
            collect(userIds, robotIds, csIds, m.getSenderType(), m.getSenderId());
            collect(userIds, robotIds, csIds, m.getReceiverType(), m.getReceiverId());
        }
    }

    private void collect(Set<Long> userIds, Set<Long> robotIds, Set<Long> csIds,
                         Integer type, Long id) {
        if (id == null) {
            return;
        }
        if (ImMessageParticipantTypeEnum.ROBOT.getType().equals(type)) {
            robotIds.add(id);
        } else if (ImMessageParticipantTypeEnum.CS.getType().equals(type)) {
            csIds.add(id);
        } else {
            userIds.add(id);
        }
    }

    /**
     * 按参与方类型解析昵称：用户取 im_users.nickname；机器人 / 客服取各自昵称（回退用户名）。
     * <p>方案 C：机器人 / 客服均以自身 id 作为唯一地址，{@code csMap}/{@code robotMap} 直接按自身 id 索引即可。</p>
     */
    private String resolveNickname(Integer type, Long id,
                                    Map<Long, ImUserDO> userMap,
                                    Map<Long, ImRobotDO> robotMap,
                                    Map<Long, ImCustomerServiceDO> csMap) {
        if (id == null) {
            return null;
        }
        if (ImMessageParticipantTypeEnum.ROBOT.getType().equals(type)) {
            ImRobotDO robot = robotMap.get(id);
            return robot == null ? null : (StrUtil.isNotBlank(robot.getNickname()) ? robot.getNickname() : robot.getUsername());
        }
        if (ImMessageParticipantTypeEnum.CS.getType().equals(type)) {
            ImCustomerServiceDO cs = csMap.get(id);
            return cs == null ? null : (StrUtil.isNotBlank(cs.getNickname()) ? cs.getNickname() : cs.getUsername());
        }
        ImUserDO user = userMap.get(id);
        return user == null ? null : user.getNickname();
    }

    @GetMapping("/get")
    @Operation(summary = "获得私聊消息详情")
    @Parameter(name = "id", description = "消息编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('im:manager:message:query')")
    public CommonResult<ImPrivateMessageManagerRespVO> getPrivateMessage(@RequestParam("id") Long id) {
        ImPrivateMessageDO message = privateMessageService.getPrivateMessage(id);
        return success(BeanUtils.toBean(message, ImPrivateMessageManagerRespVO.class));
    }

    @GetMapping("/export")
    @Operation(summary = "导出私聊消息 Excel")
    @PreAuthorize("@ss.hasPermission('im:manager:message:private:export')")
    public void exportPrivateMessageExcel(@Valid ImPrivateMessageManagerPageReqVO reqVO,
            HttpServletResponse response) throws IOException {
        // 1. 查询（按筛选条件全量）
        List<ImPrivateMessageDO> list = privateMessageService.getPrivateMessageExportList(reqVO);
        if (CollUtil.isEmpty(list)) {
            ExcelUtils.write(response, "私聊消息.xls", "数据", ImPrivateMessageExcelVO.class, List.of());
            return;
        }
        // 2. 按参与方类型分别批量拉取昵称来源
        Map<Long, ImUserDO> userMap = MapUtil.empty();
        Map<Long, ImRobotDO> robotMap = MapUtil.empty();
        Map<Long, ImCustomerServiceDO> csMap = MapUtil.empty();
        Set<Long> userIds = new HashSet<>();
        Set<Long> robotIds = new HashSet<>();
        Set<Long> csIds = new HashSet<>();
        splitParticipantIds(list, userIds, robotIds, csIds);
        if (!userIds.isEmpty()) {
            userMap = userService.getUserMap(userIds);
        }
        if (!robotIds.isEmpty()) {
            robotMap = robotManagerService.getRobotMap(robotIds);
        }
        if (!csIds.isEmpty()) {
            csMap = customerServiceManagerService.getCustomerServiceMap(csIds);
        }
        // 3. 按类型回填昵称
        Map<Long, ImUserDO> finalUserMap = userMap;
        Map<Long, ImRobotDO> finalRobotMap = robotMap;
        Map<Long, ImCustomerServiceDO> finalCsMap = csMap;
        List<ImPrivateMessageExcelVO> vos = BeanUtils.toBean(list, ImPrivateMessageExcelVO.class, vo -> {
            vo.setSenderNickname(resolveNickname(vo.getSenderType(), vo.getSenderId(), finalUserMap, finalRobotMap, finalCsMap));
            vo.setReceiverNickname(resolveNickname(vo.getReceiverType(), vo.getReceiverId(), finalUserMap, finalRobotMap, finalCsMap));
        });
        // 3. 导出
        ExcelUtils.write(response, "私聊消息.xls", "数据", ImPrivateMessageExcelVO.class, vos);
    }

}
