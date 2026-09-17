package com.diqin.cloud.module.im.controller.app.system;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.controller.app.system.vo.AppImSystemConfigRespVO;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 系统配置
 *
 * @author hanson
 */
@Tag(name = "用户 APP - IM 系统配置")
@RestController
@RequestMapping("/im/system")
@Validated
public class AppImSystemController {

    @Resource
    private ImProperties imProperties;

    @GetMapping("/config")
    @Operation(summary = "获取 IM 系统配置", description = "获取 IM 功能开关、模块限制、WebRTC 参数等配置")
    public CommonResult<AppImSystemConfigRespVO> getSystemConfig() {
        ImProperties.Message msg = imProperties.getMessage();
        ImProperties.Group grp = imProperties.getGroup();
        ImProperties.Face face = imProperties.getFace();
        ImProperties.Rtc rtc = imProperties.getRtc();
        return success(new AppImSystemConfigRespVO()
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
                .setWebrtc(new AppImSystemConfigRespVO.WebrtcConfig()
                        .setEnabled(rtc.isEnabled())
                        .setLivekitUrl(rtc.getLivekitUrl())
                        .setApiKey(rtc.getApiKey())
                        .setGroupMaxParticipants(rtc.getGroupMaxParticipants())));
    }
}
