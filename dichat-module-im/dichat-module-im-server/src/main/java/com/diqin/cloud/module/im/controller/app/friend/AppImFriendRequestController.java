package com.diqin.cloud.module.im.controller.app.friend;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.friend.vo.request.AppImFriendRequestApplyReqVO;
import com.diqin.cloud.module.im.controller.app.friend.vo.request.AppImFriendRequestRespVO;
import com.diqin.cloud.module.im.dal.dataobject.friend.ImFriendRequestDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.friend.ImFriendRequestService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertList;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSetByFlatMap;
import static com.diqin.cloud.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

/**
 * IM 好友申请记录 Controller
 *
 * @author hanson
 */
@Tag(name = "用户 APP - IM 好友申请")
@RestController
@RequestMapping("/im/friend-request")
@Validated
public class AppImFriendRequestController {

    @Resource
    private ImFriendRequestService friendRequestService;

    @Resource
    private ImUserService userService;

    @PostMapping("/apply")
    @Operation(summary = "发起好友申请")
    public CommonResult<Long> applyFriend(@Valid @RequestBody AppImFriendRequestApplyReqVO reqVO) {
        ImFriendRequestDO request = friendRequestService.applyFriend(getLoginUserId(), reqVO);
        return success(request != null ? request.getId() : null);
    }

    @PutMapping("/agree")
    @Operation(summary = "同意好友申请")
    @Parameter(name = "id", description = "申请编号", required = true, example = "1024")
    public CommonResult<Boolean> agreeFriendRequest(
            @RequestParam("id") @NotNull(message = "申请编号不能为空") Long id) {
        friendRequestService.agreeFriendRequest(getLoginUserId(), id);
        return success(true);
    }

    @PutMapping("/refuse")
    @Operation(summary = "拒绝好友申请")
    public CommonResult<Boolean> refuseFriendRequest(
            @RequestParam("id") @NotNull(message = "申请编号不能为空") Long id,
            @RequestParam(value = "handleContent", required = false)
            @Size(max = 255, message = "处理理由最多 255 个字符") String handleContent) {
        friendRequestService.refuseFriendRequest(getLoginUserId(), id, handleContent);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "查询「我相关」的好友申请列表（游标分页：传 maxId 加载更多）")
    public CommonResult<List<AppImFriendRequestRespVO>> getMyFriendRequestList(
            @Parameter(description = "当前列表最旧记录的 id；首页不传")
            @RequestParam(value = "maxId", required = false) Long maxId,
            @Parameter(description = "单次拉取条数", required = true)
            @RequestParam("limit") @Min(1) @Max(200) Integer limit) {
        List<ImFriendRequestDO> list = friendRequestService.getMyFriendRequestList(getLoginUserId(), maxId, limit);
        return success(buildList(list));
    }

    @GetMapping("/get")
    @Operation(summary = "按 id 单查「我相关」的申请记录（带越权过滤；WebSocket 通知到达后用）")
    @Parameter(name = "id", description = "申请记录编号", required = true)
    public CommonResult<AppImFriendRequestRespVO> getMyFriendRequest(@RequestParam("id") Long id) {
        ImFriendRequestDO request = friendRequestService.getFriendRequest(id);
        // 越权过滤：fromUser / toUser 必有一方是当前用户，否则当不存在返回 null
        Long currentUserId = getLoginUserId();
        if (request == null || (ObjUtil.notEqual(request.getFromUserId(), currentUserId)
                && ObjUtil.notEqual(request.getToUserId(), currentUserId))) {
            return success(null);
        }
        return success(CollUtil.getFirst(buildList(Collections.singletonList(request))));
    }

    // ========== 私有方法：VO 组装 ==========

    private List<AppImFriendRequestRespVO> buildList(List<ImFriendRequestDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        // 双向 OR 列表，userIds 取 from + to 两组并集
        Set<Long> userIds = convertSetByFlatMap(list,
                request -> Stream.of(request.getFromUserId(), request.getToUserId()));
        List<ImUserDO> userList = userService.getUserList(userIds);
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userList, ImUserDO::getId);
        return convertList(list, request -> {
            AppImFriendRequestRespVO vo = BeanUtils.toBean(request, AppImFriendRequestRespVO.class);
            MapUtils.findAndThen(userMap, request.getFromUserId(), user ->
                    vo.setFromNickname(user.getNickname()).setFromAvatar(user.getAvatar()).setFromUsername(user.getUsername()));
            MapUtils.findAndThen(userMap, request.getToUserId(), user ->
                    vo.setToNickname(user.getNickname()).setToAvatar(user.getAvatar()).setToUsername(user.getUsername()));
            return vo;
        });
    }

}
