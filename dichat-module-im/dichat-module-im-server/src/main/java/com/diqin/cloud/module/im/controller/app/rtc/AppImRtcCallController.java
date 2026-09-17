package com.diqin.cloud.module.im.controller.app.rtc;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.module.im.controller.app.rtc.vo.AppImRtcCallCreateReqVO;
import com.diqin.cloud.module.im.controller.app.rtc.vo.AppImRtcCallInviteReqVO;
import com.diqin.cloud.module.im.controller.app.rtc.vo.AppImRtcCallRespVO;
import com.diqin.cloud.module.im.controller.app.rtc.vo.AppImRtcGroupCallRespVO;
import com.diqin.cloud.module.im.dal.dataobject.rtc.ImRtcCallDO;
import com.diqin.cloud.module.im.dal.dataobject.rtc.ImRtcParticipantDO;
import com.diqin.cloud.module.im.enums.rtc.ImRtcCallStatusEnum;
import com.diqin.cloud.module.im.enums.rtc.ImRtcParticipantStatusEnum;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import com.diqin.cloud.module.im.service.rtc.ImRtcCallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - IM 实时通话")
@RestController
@RequestMapping("/im/rtc")
@Validated
public class AppImRtcCallController {

    @Resource
    private ImRtcCallService rtcCallService;
    @Resource
    private ImProperties imProperties;

    @PostMapping("/create")
    @Operation(summary = "创建新通话；按 conversationType 区分私聊 / 群聊")
    public CommonResult<AppImRtcCallRespVO> createCall(@Valid @RequestBody AppImRtcCallCreateReqVO reqVO) {
        Long userId = getLoginUserId();
        ImRtcCallDO call = rtcCallService.createCall(userId, reqVO);
        return success(buildCallRespVO(call, userId));
    }

    @PostMapping("/invite")
    @Operation(summary = "通话中追加邀请；仅群通话可用")
    public CommonResult<Boolean> inviteCall(@Valid @RequestBody AppImRtcCallInviteReqVO reqVO) {
        rtcCallService.inviteCall(getLoginUserId(), reqVO);
        return success(true);
    }

    @PostMapping("/join")
    @Operation(summary = "加入已有群通话；用于胶囊条「加入」按钮")
    @Parameter(name = "room", description = "业务通话编号", required = true, example = "f47ac10b58cc4372a567")
    public CommonResult<AppImRtcCallRespVO> joinCall(@RequestParam("room") String room) {
        Long userId = getLoginUserId();
        return success(buildCallRespVO(rtcCallService.joinCall(userId, room), userId));
    }

    @PostMapping("/accept")
    @Operation(summary = "接听通话")
    @Parameter(name = "room", description = "业务通话编号", required = true, example = "f47ac10b58cc4372a567")
    public CommonResult<AppImRtcCallRespVO> accept(@RequestParam("room") String room) {
        Long userId = getLoginUserId();
        return success(buildCallRespVO(rtcCallService.acceptCall(userId, room), userId));
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝通话")
    @Parameter(name = "room", description = "业务通话编号", required = true, example = "f47ac10b58cc4372a567")
    public CommonResult<Boolean> reject(@RequestParam("room") String room) {
        rtcCallService.rejectCall(getLoginUserId(), room);
        return success(true);
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消邀请；主叫接通前调用")
    @Parameter(name = "room", description = "业务通话编号", required = true, example = "f47ac10b58cc4372a567")
    public CommonResult<Boolean> cancel(@RequestParam("room") String room) {
        rtcCallService.cancelCall(getLoginUserId(), room);
        return success(true);
    }

    @PostMapping("/leave")
    @Operation(summary = "离开通话；接通后调用")
    @Parameter(name = "room", description = "业务通话编号", required = true, example = "f47ac10b58cc4372a567")
    public CommonResult<Boolean> leave(@RequestParam("room") String room) {
        rtcCallService.leaveCall(getLoginUserId(), room);
        return success(true);
    }

    @PostMapping("/no-answer-call-check")
    @Operation(summary = "前端 RUNNING 端 timer 兜底；触发后端立即扫描该 room 的振铃超时（接口静默）")
    @Parameter(name = "room", description = "业务通话编号", required = true, example = "f47ac10b58cc4372a567")
    public CommonResult<Boolean> noAnswerCallCheck(@RequestParam("room") String room) {
        rtcCallService.noAnswerCallCheck(getLoginUserId(), room);
        return success(true);
    }

    @GetMapping("/get-active-call")
    @Operation(summary = "查询当前进行中的通话；用于群聊顶部「N 人正在通话」胶囊条")
    @Parameter(name = "groupId", description = "群编号", required = true, example = "2048")
    public CommonResult<AppImRtcGroupCallRespVO> getActiveCall(@RequestParam("groupId") Long groupId) {
        ImRtcCallDO call = rtcCallService.getActiveCall(getLoginUserId(), groupId);
        return success(buildGroupActiveRespVO(call));
    }

    // ========== VO 拼装 ==========

    /**
     * 拼装 invite / join / accept / refresh-token 的响应 VO；含 token + 参与者分桶
     *
     * @param call   通话主表
     * @param userId 当前用户编号；token 按该用户签发
     * @return 响应 VO；call 为空返回 null
     */
    private AppImRtcCallRespVO buildCallRespVO(ImRtcCallDO call, Long userId) {
        if (call == null) {
            return null;
        }
        List<ImRtcParticipantDO> participants = rtcCallService.getCallParticipantList(call.getRoom());
        boolean ended = ImRtcCallStatusEnum.isEnded(call.getStatus());
        return new AppImRtcCallRespVO()
                .setRoom(call.getRoom())
                .setLivekitUrl(imProperties.getRtc().getLivekitUrl())
                // 仅非 ENDED 场景才签 token，ENDED 场景不签，前端根据 token 是否存在，来判断是否展示「通话已结束」的提示
                .setToken(ended ? null : rtcCallService.signCallToken(userId, call.getRoom()))
                .setConversationType(call.getConversationType()).setMediaType(call.getMediaType())
                .setStatus(call.getStatus()).setEndReason(call.getEndReason())
                .setInviterId(call.getInviterUserId()).setGroupId(call.getGroupId())
                .setInviteeIds(filterUserIds(participants, ImRtcParticipantStatusEnum.INVITING))
                .setJoinedUserIds(filterUserIds(participants, ImRtcParticipantStatusEnum.JOINED));
    }

    /**
     * 拼装 get-active-call 的响应 VO
     *
     * @param call 通话主表
     * @return 响应 VO：只用于群聊胶囊条，不含 token
     */
    private AppImRtcGroupCallRespVO buildGroupActiveRespVO(ImRtcCallDO call) {
        if (call == null) {
            return null;
        }
        List<ImRtcParticipantDO> participants = rtcCallService.getCallParticipantList(call.getRoom());
        return new AppImRtcGroupCallRespVO().setRoom(call.getRoom()).setMediaType(call.getMediaType())
                .setGroupId(call.getGroupId()).setInviterId(call.getInviterUserId())
                .setJoinedUserIds(filterUserIds(participants, ImRtcParticipantStatusEnum.JOINED))
                .setInviteeIds(filterUserIds(participants, ImRtcParticipantStatusEnum.INVITING));
    }

    /**
     * 按状态过滤参与者 userId；用 LinkedHashSet 保留前端展示顺序
     *
     * @param participants 参与者列表
     * @param status       目标状态
     * @return userId 集合
     */
    private static Set<Long> filterUserIds(List<ImRtcParticipantDO> participants,
                                           ImRtcParticipantStatusEnum status) {
        return CollectionUtils.convertLinkedSet(participants, ImRtcParticipantDO::getUserId,
                participant -> Objects.equals(participant.getStatus(), status.getStatus()));
    }

}
