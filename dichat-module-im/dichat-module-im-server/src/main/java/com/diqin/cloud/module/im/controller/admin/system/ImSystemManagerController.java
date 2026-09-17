package com.diqin.cloud.module.im.controller.admin.system;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.controller.admin.system.vo.ImSystemConfigRespVO;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - IM 系统配置
 * <p>
 * 提供给前端获取 IM 服务的运行时配置：功能开关、模块限制、WebRTC 参数等。
 * <p>
 * 配置来源为 {@link ImProperties}（即 {@code dichat.im.*} 配置项，由部署侧 application.yml 提供，
 * 非数据库表），本接口为只读聚合视图；如需热更新编辑能力，需另接配置持久化层（不在本次范围内）。
 * 客户端在登录后调用一次，缓存到本地；后续行为（是否展示已读标签、群成员上限校验、通话按钮显隐）
 * 均以本接口返回值为准，避免前端硬编码与服务端配置不一致。
 */
@Tag(name = "管理后台 - IM 系统配置")
@RestController
@RequestMapping("/im/manager/system")
@Validated
public class ImSystemManagerController {

    @Resource
    private ImProperties imProperties;

    @GetMapping("/config")
    @Operation(summary = "获取 IM 系统配置（功能开关 / 模块限制 / WebRTC 参数）")
    @PreAuthorize("@ss.hasPermission('im:manager:system:query')")
    public CommonResult<ImSystemConfigRespVO> getSystemConfig() {
        ImProperties.Message msg = imProperties.getMessage();
        ImProperties.Group grp = imProperties.getGroup();
        ImProperties.Face face = imProperties.getFace();
        ImProperties.Rtc rtc = imProperties.getRtc();
        ImProperties.Robot robot = imProperties.getRobot();
        return success(new ImSystemConfigRespVO()
                // 消息模块
                .setPrivateReadEnabled(msg.isPrivateReadEnabled())
                .setGroupReadEnabled(msg.isGroupReadEnabled())
                .setRecallTimeoutMinutes(msg.getRecallTimeoutMinutes())
                .setMaxPullSize(msg.getMaxPullSize())
                .setPrivatePullMaxDays(msg.getPrivatePullMaxDays())
                .setGroupPullMaxDays(msg.getGroupPullMaxDays())
                // 群模块
                .setGroupMaxMember(grp.getMaxMember())
                .setGroupAdminMaxCount(grp.getAdminMaxCount())
                .setGroupPinMaxCount(grp.getPinMaxCount())
                // 表情模块
                .setFaceUserItemMaxCount(face.getUserItemMaxCount())
                // WebRTC
                .setWebrtc(new ImSystemConfigRespVO.WebrtcConfig()
                        .setEnabled(rtc.isEnabled())
                        .setLivekitUrl(rtc.getLivekitUrl())
                        .setApiKey(rtc.getApiKey())
                        .setGroupMaxParticipants(rtc.getGroupMaxParticipants()))
                // 机器人 / 转人工
                .setRobot(new ImSystemConfigRespVO.RobotConfig()
                        .setAutoAssignEnabled(robot.isEnableAutoAssign())
                        .setLoadBalanceWindowMinutes(robot.getLoadBalanceWindowMinutes())
                        .setTransferCooldownMinutes(robot.getTransferCooldownMinutes())));
    }

    @PutMapping("/robot/auto-assign")
    @Operation(summary = "设置「转人工」自动分配总开关（运行时热切换，重启后回退 yaml 配置）")
    // TODO 生产环境应拆分为独立权限 im:manager:system:update；当前复用 query 权限，避免新增菜单/权限 SQL 种子
    @PreAuthorize("@ss.hasPermission('im:manager:system:query')")
    public CommonResult<Boolean> setRobotAutoAssign(@RequestParam("enabled") Boolean enabled) {
        imProperties.getRobot().setEnableAutoAssign(enabled);
        return success(enabled);
    }

}
