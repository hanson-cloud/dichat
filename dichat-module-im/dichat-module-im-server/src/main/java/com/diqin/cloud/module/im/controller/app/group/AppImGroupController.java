package com.diqin.cloud.module.im.controller.app.group;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.group.vo.*;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberInviteReqVO;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberRemoveReqVO;
import com.diqin.cloud.module.im.controller.app.group.vo.member.AppImGroupMemberUpdateReqVO;
import com.diqin.cloud.module.im.controller.app.message.vo.group.AppImGroupMessageRespVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupDO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupMemberDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImGroupMessageDO;
import com.diqin.cloud.module.im.service.group.ImGroupMemberService;
import com.diqin.cloud.module.im.service.group.ImGroupService;
import com.diqin.cloud.module.im.service.message.ImGroupMessageService;
import com.diqin.cloud.module.im.service.online.ImOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.*;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

/**
 * App - IM 群聊
 *
 * <p>参考 member 模块的路径规范，使用 {@code /im/group} 前缀。
 * <br>网关路由 /app-api/im/** 通过 RewritePath 去掉 /app-api 前缀后转发到 /im/group/list 等。
 * <br>完整访问路径为 /app-api/im/group/list 等，与 member 模块的 /app-api/member/xxx 风格一致。
 *
 * @author hanson
 */
@Tag(name = "用户 APP - IM 群聊")
@RestController
@RequestMapping("/im/group")
@Validated
public class AppImGroupController {

    @Resource
    private ImGroupService groupService;

    @Resource
    private ImGroupMemberService groupMemberService;

    @Resource
    private ImGroupMessageService groupMessageService;
    @Resource
    private ImOnlineService onlineService;

    // ==================== 群的写操作 ====================

    @PostMapping("/create")
    @Operation(summary = "创建群")
    public CommonResult<AppImGroupRespVO> createGroup(@Valid @RequestBody AppImGroupCreateReqVO createReqVO) {
        ImGroupDO group = groupService.createGroup(createReqVO, getLoginUserId());
        // 新建群必无 pinnedMessages，跳过关联回填
        return success(BeanUtils.toBean(group, AppImGroupRespVO.class));
    }

    @PutMapping("/update")
    @Operation(summary = "更新群")
    public CommonResult<AppImGroupRespVO> updateGroup(@Valid @RequestBody AppImGroupUpdateReqVO updateReqVO) {
        ImGroupDO group = groupService.updateGroup(updateReqVO, getLoginUserId());
        return success(buildGroupRespVO(group, getLoginUserId()));
    }

    @DeleteMapping("/delete/{groupId}")
    @Operation(summary = "解散群聊", description = "解散群聊，仅群主可操作")
    @Parameter(name = "groupId", description = "群编号", required = true, example = "1024")
    public CommonResult<Boolean> deleteGroup(@NotNull(message = "群编号不能为空") @PathVariable Long groupId) {
        groupService.dissolveGroup(groupId, getLoginUserId());
        return success(true);
    }

    // ==================== 群的读操作 ====================

    @GetMapping("/get/{groupId}")
    @Operation(summary = "获得群")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppImGroupRespVO> getGroup(@NotNull(message = "群编号不能为空") @PathVariable Long groupId) {
        ImGroupDO group = groupService.getGroup(groupId);
        return success(buildGroupRespVO(group, getLoginUserId()));
    }

    @GetMapping("/list")
    @Operation(summary = "查询群聊列表", description = "查询当前登录用户加入的所有群聊；可选 keyword 按群名/公告模糊过滤")
    public CommonResult<List<AppImGroupRespVO>> findGroups(
            @RequestParam(value = "keyword", required = false) String keyword) {
        Long loginUserId = getLoginUserId();
        List<ImGroupDO> groups = groupService.getMyGroupList(loginUserId, keyword);
        return success(buildGroupRespVOList(groups, loginUserId));
    }

    @GetMapping("/square")
    @Operation(summary = "群聊广场", description = "检索公开群（is_public=1 且未封禁、未解散）；"
            + "keyword 按群名/话题模糊匹配（OR）；category 精确匹配；按成员数倒序、id 倒序")
    public CommonResult<PageResult<AppImGroupRespVO>> publicGroupSquare(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        PageResult<ImGroupDO> pageResult = groupService.searchPublicGroups(keyword, category, pageNo, pageSize);
        // 广场列表不复用 buildGroupRespVOList（避免越权拿置顶消息），仅做基础 VO 映射
        List<AppImGroupRespVO> vos = convertList(pageResult.getList(), group ->
                BeanUtils.toBean(group, AppImGroupRespVO.class));
        return success(new PageResult<>(vos, pageResult.getTotal()));
    }

    // ==================== 群成员的写操作 ====================
    @PostMapping("/invite")
    @Operation(summary = "邀请进群", description = "邀请好友进群；仅群主和管理员可操作")
    public CommonResult<Boolean> invite(@Valid @RequestBody AppImGroupMemberInviteReqVO reqVO) {
        groupService.inviteGroupMember(getLoginUserId(), reqVO);
        return success(true);
    }

    @DeleteMapping("/quit")
    @Operation(summary = "退出群")
    @Parameter(name = "groupId", description = "群编号", required = true)
    public CommonResult<Boolean> quitGroup(@RequestParam("groupId") Long groupId) {
        groupService.quitGroup(groupId, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/members/remove")
    @Operation(summary = "将成员移出群聊", description = "将成员移出群聊；仅群主和管理员可操作")
    public CommonResult<Boolean> removeMembers(@Valid @RequestBody AppImGroupMemberRemoveReqVO reqVO) {
        groupService.removeGroupMember(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/add-admin")
    @Operation(summary = "添加群管理员")
    public CommonResult<Boolean> addGroupAdmin(@Valid @RequestBody AppImGroupAdminAddReqVO reqVO) {
        groupService.addGroupAdmin(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/remove-admin")
    @Operation(summary = "撤销群管理员", description = "撤销群管理员；仅群主可操作")
    public CommonResult<Boolean> removeGroupAdmin(@Valid @RequestBody AppImGroupAdminRemoveReqVO reqVO) {
        groupService.removeGroupAdmin(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/transfer-owner")
    @Operation(summary = "转让群主", description = "转让群主身份；仅群主可操作")
    public CommonResult<Boolean> transferGroupOwner(@Valid @RequestBody AppImGroupTransferOwnerReqVO transferReqVO) {
        groupService.transferGroupOwner(getLoginUserId(), transferReqVO);
        return success(true);
    }

    // ==================== 群消息置顶 ====================

    @PutMapping("/pin-message")
    @Operation(summary = "置顶群消息（群主 / 管理员）")
    public CommonResult<Boolean> pinGroupMessage(@Valid @RequestBody AppImGroupMessagePinReqVO reqVO) {
        groupService.pinGroupMessage(getLoginUserId(), reqVO.getId(), reqVO.getMessageId());
        return success(true);
    }

    @PutMapping("/unpin-message")
    @Operation(summary = "取消置顶群消息（群主 / 管理员）")
    public CommonResult<Boolean> unpinGroupMessage(@Valid @RequestBody AppImGroupMessagePinReqVO reqVO) {
        groupService.unpinGroupMessage(getLoginUserId(), reqVO.getId(), reqVO.getMessageId());
        return success(true);
    }

    // ==================== 在线状态 / 免打扰 ====================

    @GetMapping("/members/online")
    @Operation(summary = "查询群在线成员编号", description = "返回指定群中当前在线的成员用户编号列表")
    @Parameter(name = "groupId", description = "群编号", required = true, example = "1024")
    public CommonResult<List<Long>> getOnlineGroupMembers(@RequestParam("groupId") @NotNull(message = "群编号不能为空") Long groupId) {
        // 1. 校验当前用户是群成员（避免越权查询任意群在线状态）
        groupMemberService.validateMemberInGroup(groupId, getLoginUserId());
        // 2. 获取有效成员 userId 列表
        List<Long> memberUserIds = groupMemberService.getActiveGroupMemberUserIdsByGroupId(groupId);
        if (CollUtil.isEmpty(memberUserIds)) {
            return success(Collections.emptyList());
        }
        // 3. 过滤在线用户
        return success(onlineService.filterOnlineUserIds(memberUserIds));
    }

    @PutMapping("/dnd")
    @Operation(summary = "开启/关闭群免打扰")
    public CommonResult<Boolean> setGroupDnd(@Valid @RequestBody AppImGroupMemberUpdateReqVO reqVO) {
        groupMemberService.updateGroupMember(getLoginUserId(), reqVO);
        return success(true);
    }

    // ==================== 群禁言 ====================

    @PutMapping("/mute-all")
    @Operation(summary = "全群禁言 / 取消（群主 / 管理员）")
    public CommonResult<Boolean> muteAll(@Valid @RequestBody AppImGroupMuteAllReqVO reqVO) {
        groupService.muteAll(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/mute-member")
    @Operation(summary = "禁言群成员", description = "禁言指定群成员；群主/管理员可操作")
    public CommonResult<Boolean> muteMember(@Valid @RequestBody AppImGroupMuteMemberReqVO reqVO) {
        groupService.muteMember(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/cancel-mute-member")
    @Operation(summary = "取消成员禁言", description = "取消指定群成员的禁言；群主/管理员可操作")
    public CommonResult<Boolean> cancelMuteMember(@Valid @RequestBody AppImGroupCancelMuteMemberReqVO reqVO) {
        groupService.cancelMuteMember(getLoginUserId(), reqVO);
        return success(true);
    }

    /** 单群转 VO + 关联回填 pinnedMessages（仅当登录用户是该群有效成员） */
    private AppImGroupRespVO buildGroupRespVO(ImGroupDO group, Long loginUserId) {
        if (group == null) {
            return null;
        }
        return buildGroupRespVOList(Collections.singletonList(group), loginUserId).getFirst();
    }

    /**
     * 群列表批量转 VO + 关联回填 pinnedMessages
     * <p>
     * 仅当登录用户是某群的有效成员时才回填该群的 pinnedMessages，避免非成员 / 已退群用户越权拿到置顶消息内容
     */
    private List<AppImGroupRespVO> buildGroupRespVOList(List<ImGroupDO> groups, Long loginUserId) {
        if (CollUtil.isEmpty(groups)) {
            return Collections.emptyList();
        }
        // 仅当前用户是有效成员的群才允许回填置顶消息
        Set<Long> activeGroupIds = convertSet(
                groupMemberService.getActiveGroupMemberListByUserId(loginUserId), ImGroupMemberDO::getGroupId);
        Set<Long> allMessageIds = convertSetByFlatMap(groups, group -> activeGroupIds.contains(group.getId())
                ? CollUtil.emptyIfNull(group.getPinnedMessageIds()).stream() : Stream.empty());
        Map<Long, ImGroupMessageDO> messageMap = groupMessageService.getGroupMessageMap(allMessageIds);
        // 转换输出
        return convertList(groups, group -> {
            AppImGroupRespVO vo = BeanUtils.toBean(group, AppImGroupRespVO.class);
            // 当前登录用户非该群有效成员（含从未加入）→ quit=true，前端据此隐藏成员操作、展示「申请加入」
            vo.setQuit(!activeGroupIds.contains(group.getId()));
            if (!activeGroupIds.contains(group.getId()) || CollUtil.isEmpty(group.getPinnedMessageIds())) {
                return vo;
            }
            // 按 pin 顺序输出，已被删除的消息（messageMap 没命中）跳过
            List<ImGroupMessageDO> pinnedMesages = convertList(group.getPinnedMessageIds(), messageMap::get);
            return vo.setPinnedMessages(BeanUtils.toBean(pinnedMesages, AppImGroupMessageRespVO.class));
        });
    }
}
