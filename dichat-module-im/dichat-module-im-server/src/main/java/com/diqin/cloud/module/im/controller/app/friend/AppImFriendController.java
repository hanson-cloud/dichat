package com.diqin.cloud.module.im.controller.app.friend;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.friend.vo.AppImFriendRespVO;
import com.diqin.cloud.module.im.controller.app.friend.vo.AppImFriendUpdateReqVO;
import com.diqin.cloud.module.im.controller.app.online.vo.AppImUserOnlineRespVO;
import com.diqin.cloud.module.im.dal.dataobject.friend.ImFriendDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.friend.ImFriendService;
import com.diqin.cloud.module.im.service.online.ImOnlineService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.github.yulichang.toolkit.StrUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.*;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 好友")
@RestController
@RequestMapping("/im/friend")
@Validated
public class AppImFriendController {

    @Resource
    private ImFriendService friendService;

    @Resource
    private ImUserService userService;

    @Resource
    private ImOnlineService onlineService;

    @GetMapping("/list")
    @Operation(summary = "获得当前登录用户的好友列表")
    public CommonResult<List<AppImFriendRespVO>> getMyFriendList() {
        // 含 DISABLE 历史好友：保留给前端展示「已删除好友」的历史对话信息；前端按 status 决定会话级联清理
        List<ImFriendDO> friends = friendService.getFriendList(getLoginUserId());
        return success(buildFriendRespVOList(friends));
    }

    @GetMapping("/get")
    @Operation(summary = "获得好友详情")
    @Parameter(name = "friendUserId", description = "好友的用户编号", required = true, example = "2048")
    public CommonResult<AppImFriendRespVO> getFriend(@RequestParam("friendUserId") Long friendUserId) {
        ImFriendDO friend = friendService.getFriend(getLoginUserId(), friendUserId);
        return success(buildFriendRespVO(friend));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除好友（单向软删除）")
    @Parameters({
            @Parameter(description = "好友的用户编号", required = true, example = "2048"),
            @Parameter(description = "是否级联清理本端相关数据（如私聊会话）")
    })
    public CommonResult<Boolean> deleteFriend(
            @RequestParam("friendUserId") @NotNull(message = "好友用户编号不能为空") Long friendUserId,
            @RequestParam(value = "clear", required = false) Boolean clear) {
        friendService.deleteFriend(getLoginUserId(), friendUserId, clear);
        return success(true);
    }

    @DeleteMapping("/delete/{friendId}")
    @Operation(summary = "删除好友", description = "单向软删除好友关系")
    @Parameter(name = "friendId", description = "好友的用户编号", required = true, example = "2048")
    public CommonResult<Boolean> deleteFriend(@PathVariable("friendId") @NotNull(message = "好友用户编号不能为空") Long friendId) {
        friendService.deleteFriend(getLoginUserId(), friendId, null);
        return success(true);
    }


    @PutMapping("/update")
    @Operation(summary = "更新好友单边属性（备注 / 免打扰 / 联系人置顶）")
    public CommonResult<Boolean> updateFriend(@Valid @RequestBody AppImFriendUpdateReqVO reqVO) {
        friendService.updateFriend(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/dnd")
    @Operation(summary = "开启/关闭好友免打扰")
    @Parameter(name = "friendId", description = "好友的用户编号", required = true, example = "2048")
    @Parameter(name = "dnd", description = "是否免打扰", required = true, example = "true")
    public CommonResult<Boolean> setFriendDnd(@Valid @RequestBody AppImFriendUpdateReqVO reqVO) {
        friendService.updateFriend(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/block")
    @Operation(summary = "拉黑好友（必须先是好友；单边屏蔽对方私聊消息）")
    @Parameter(name = "friendUserId", description = "好友的用户编号", required = true, example = "2048")
    public CommonResult<Boolean> blockFriend(
            @RequestParam("friendUserId") @NotNull(message = "好友用户编号不能为空") Long friendUserId) {
        friendService.blockFriend(getLoginUserId(), friendUserId);
        return success(true);
    }

    @PutMapping("/unblock")
    @Operation(summary = "移出黑名单")
    @Parameter(name = "friendUserId", description = "好友的用户编号", required = true, example = "2048")
    public CommonResult<Boolean> unblockFriend(
            @RequestParam("friendUserId") @NotNull(message = "好友用户编号不能为空") Long friendUserId) {
        friendService.unblockFriend(getLoginUserId(), friendUserId);
        return success(true);
    }

    @GetMapping("/online")
    @Operation(summary = "查询好友在线状态", description = "返回当前登录用户所有有效好友的在线终端信息")
    public CommonResult<List<AppImUserOnlineRespVO>> getFriendOnlineList() {
        Long loginUserId = getLoginUserId();
        List<ImFriendDO> friends = friendService.getEnableFriendList(loginUserId);
        if (CollUtil.isEmpty(friends)) {
            return success(Collections.emptyList());
        }
        List<Long> friendUserIds = convertList(friends, ImFriendDO::getFriendUserId);
        Map<Long, List<Integer>> terminalMap = onlineService.getOnlineTerminalMap(friendUserIds);
        return success(terminalMap.entrySet().stream()
                .flatMap(e -> e.getValue().stream()
                        .map(terminal -> new AppImUserOnlineRespVO(e.getKey(), terminal, true)))
                .collect(Collectors.toList()));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户（用于添加新好友）",
            description = "按微信标准搜索：优先级为 账号精确 > 昵称精确 > 昵称前缀 > 昵称模糊。"
                    + "自动排除当前用户本人、已是好友的用户。")
    @Parameter(name = "name", description = "昵称关键词", required = true, example = "张三")
    public CommonResult<List<AppImFriendRespVO>> searchUsers(@RequestParam("name") String name) {
        // 1. 校验关键词：空串直接返回空列表，避免下游 RPC 触发全表扫描
        if (StrUtils.isBlank(name) || name.trim().isEmpty()) {
            return success(Collections.emptyList());
        }
        Long loginUserId = getLoginUserId();

        // 2. 按微信标准搜索用户（Service 层已排除本人）
        List<ImUserDO> users = userService.searchUsers(name.trim(), loginUserId);
        if (CollUtil.isEmpty(users)) {
            return success(Collections.emptyList());
        }

        // 3. 过滤：排除已是好友的用户（搜索加好友场景不需要重复推荐）
        List<Long> candidateIds = convertList(users, ImUserDO::getId);
        List<ImFriendDO> existingFriends = friendService.getActiveFriendList(loginUserId, candidateIds);
        Set<Long> excludeFriendIds = convertSet(existingFriends, ImFriendDO::getFriendUserId);

        // 5. 排除后兜底
        List<ImFriendDO> result = new ArrayList<>();
        for (ImUserDO user : users) {
            if (excludeFriendIds.contains(user.getId())) {
                continue;
            }
            // 复用 ImFriendDO 作为 VO（userId 字段填搜索目标的 id），保持响应结构与好友列表对齐
            result.add(ImFriendDO.builder()
                    .userId(loginUserId).friendUserId(user.getId())
                    .displayName(null).addSource(null)
                    .build());
        }
        return success(buildFriendRespVOList(result));
    }

    // ========== 私有方法：VO 组装 ==========

    private List<AppImFriendRespVO> buildFriendRespVOList(Collection<ImFriendDO> friends) {
        if (CollUtil.isEmpty(friends)) {
            return Collections.emptyList();
        }
        // 批量聚合 IM 用户信息（昵称 / 头像），避免 N+1
        List<ImUserDO> userList = userService.getUserList(convertList(friends, ImFriendDO::getFriendUserId));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userList, ImUserDO::getId);
        return convertList(friends, friend -> {
            AppImFriendRespVO vo = BeanUtils.toBean(friend, AppImFriendRespVO.class);
            MapUtils.findAndThen(userMap, friend.getFriendUserId(), user ->
                    vo.setNickname(user.getNickname()).setAvatar(user.getAvatar()));
            // 备注 / 昵称的拼音，给前端做字母分桶 + 拼音搜索
            vo.setDisplayNamePinyin(StrUtil.isBlank(vo.getDisplayName()) ? "" : PinyinUtil.getPinyin(vo.getDisplayName()))
                    .setNicknamePinyin(StrUtil.isBlank(vo.getNickname()) ? "" : PinyinUtil.getPinyin(vo.getNickname()));
            return vo;
        });
    }

    private AppImFriendRespVO buildFriendRespVO(ImFriendDO friend) {
        if (friend == null) {
            return null;
        }
        return CollUtil.getFirst(buildFriendRespVOList(singleton(friend)));
    }

}
