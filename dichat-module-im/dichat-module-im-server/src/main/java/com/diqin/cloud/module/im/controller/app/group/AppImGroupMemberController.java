package com.diqin.cloud.module.im.controller.app.group;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberRespVO;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupMemberDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.group.ImGroupMemberService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertList;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getLoginUserId;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.GROUP_MEMBER_NOT_IN_GROUP;

@Tag(name = "管理后台 - 群成员")
@RestController
@RequestMapping("/im/group-member")
@Validated
public class AppImGroupMemberController {

    @Resource
    private ImGroupMemberService groupMemberService;

    @Resource
    private ImUserService userService;

    @PutMapping("/update")
    @Operation(summary = "更新群成员")
    public CommonResult<Boolean> updateGroupMember(@Valid @RequestBody AppImGroupMemberUpdateReqVO updateReqVO) {
        groupMemberService.updateGroupMember(getLoginUserId(), updateReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得群成员")
    @Parameters({
            @Parameter(name = "id", description = "编号（与 groupId + userId 二选一）", example = "1024"),
            @Parameter(name = "groupId", description = "群编号（与 userId 配合查）", example = "1"),
            @Parameter(name = "userId", description = "用户编号（与 groupId 配合查）", example = "100")
    })
    public CommonResult<AppImGroupMemberRespVO> getGroupMember(@RequestParam(value = "id", required = false) Long id,
                                                            @RequestParam(value = "groupId", required = false) Long groupId,
                                                            @RequestParam(value = "userId", required = false) Long userId) {
        // 1. 查询群成员
        ImGroupMemberDO member;
        if (id != null) {
            member = groupMemberService.getGroupMember(id);
        } else if (groupId != null && userId != null) {
            member = groupMemberService.getGroupMember(groupId, userId);
        } else {
            // 避免 selectByGroupIdAndUserId 收到 null 参数走全表扫 / 抛 SQL 异常
            throw new IllegalArgumentException("参数缺失：需传 id 或 (groupId, userId)");
        }
        if (member == null) {
            return success(null);
        }

        // 2. 校验当前登录用户是该成员所在群的有效成员
        Long loginUserId = getLoginUserId();
        groupMemberService.validateMemberInGroup(member.getGroupId(), loginUserId);

        // 3. 转化 VO
        AppImGroupMemberRespVO memberVO = BeanUtils.toBean(member, AppImGroupMemberRespVO.class);
        ImUserDO user = userService.getUser(member.getUserId());
        if (user != null) {
            memberVO.setNickname(user.getNickname()).setAvatar(user.getAvatar());
        }
        hidePrivateFieldsIfNotSelf(memberVO, member.getUserId(), loginUserId);
        return success(memberVO);
    }

    @GetMapping("/list")
    @Operation(summary = "获得指定群的成员列表")
    @Parameter(name = "groupId", description = "群编号", required = true, example = "1024")
    public CommonResult<List<AppImGroupMemberRespVO>> getGroupMemberList(@RequestParam("groupId") Long groupId) {
        // 1.1 查询群成员列表（包含 DISABLE 已退群的成员，不按时间过滤）
        // 说明：保留已退群成员，是为了前端展示历史消息时，仍能通过该接口拿到已退群成员的昵称 / 头像信息，避免显示为空
        List<ImGroupMemberDO> members = groupMemberService.getGroupMemberListByGroupId(groupId);
        // 1.2 校验当前登录用户是否为群的有效成员，非成员不可查看
        Long loginUserId = getLoginUserId();
        if (CollUtil.findOne(members, member -> loginUserId.equals(member.getUserId())
                && CommonStatusEnum.ENABLE.getStatus().equals(member.getStatus())) == null) {
            throw exception(GROUP_MEMBER_NOT_IN_GROUP);
        }

        // 2.批量聚合 ImUserDO 信息（昵称 / 头像）
        List<ImUserDO> userList = userService.getUserList(convertList(members, ImGroupMemberDO::getUserId));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userList, ImUserDO::getId);
        return success(convertList(members, m -> {
            AppImGroupMemberRespVO vo = BeanUtils.toBean(m, AppImGroupMemberRespVO.class);
            MapUtils.findAndThen(userMap, m.getUserId(), user ->
                    vo.setNickname(user.getNickname()).setAvatar(user.getAvatar()));
            hidePrivateFieldsIfNotSelf(vo, m.getUserId(), loginUserId);
            return vo;
        }));
    }

    /**
     * 非本人查看时，置空成员的私人设置字段（groupRemark / silent）
     */
    private void hidePrivateFieldsIfNotSelf(AppImGroupMemberRespVO vo, Long memberUserId, Long loginUserId) {
        if (ObjUtil.notEqual(loginUserId, memberUserId)) {
            vo.setGroupRemark(null).setSilent(null);
        }
    }

}
