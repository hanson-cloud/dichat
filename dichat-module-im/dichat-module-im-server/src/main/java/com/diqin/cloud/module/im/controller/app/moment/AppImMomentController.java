package com.diqin.cloud.module.im.controller.app.moment;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.app.moment.vo.AppImMomentCommentRespVO;
import com.diqin.cloud.module.im.controller.app.moment.vo.AppImMomentCreateReqVO;
import com.diqin.cloud.module.im.controller.app.moment.vo.AppImMomentRespVO;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentCommentDO;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.service.moment.ImMomentService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户APP - IM 朋友圈")
@RestController
@RequestMapping("/im/moment")
@Validated
public class AppImMomentController {

    @Resource
    private ImMomentService momentService;
    @Resource
    private ImUserService userService;

    @PostMapping("/create")
    @Operation(summary = "发布朋友圈")
    public CommonResult<Long> createMoment(@Valid @RequestBody AppImMomentCreateReqVO reqVO) {
        return success(momentService.createMoment(getLoginUserId(), reqVO.getContent(),
                reqVO.getImages(), reqVO.getVisibility(),
                reqVO.getLocation(),
                toJson(reqVO.getVisibleUserIds()),
                toJson(reqVO.getInvisibleUserIds()),
                toJson(reqVO.getRemindUserIds())));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除朋友圈")
    public CommonResult<Boolean> deleteMoment(@RequestParam Long id) {
        momentService.deleteMoment(getLoginUserId(), id);
        return success(true);
    }

    @GetMapping("/feed")
    @Operation(summary = "获取好友朋友圈feed流")
    public CommonResult<List<AppImMomentRespVO>> getFriendMoments(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = getLoginUserId();
        List<ImMomentDO> list = momentService.getFriendMoments(userId, pageNo, pageSize);
        return success(buildResp(list, userId));
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的朋友圈")
    public CommonResult<List<AppImMomentRespVO>> getMyMoments(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = getLoginUserId();
        List<ImMomentDO> list = momentService.getMyMoments(userId, pageNo, pageSize);
        return success(buildResp(list, userId));
    }

    @PostMapping("/like")
    @Operation(summary = "点赞")
    public CommonResult<Boolean> like(@RequestParam Long momentId) {
        momentService.likeMoment(getLoginUserId(), momentId);
        return success(true);
    }

    @PostMapping("/unlike")
    @Operation(summary = "取消点赞")
    public CommonResult<Boolean> unlike(@RequestParam Long momentId) {
        momentService.unlikeMoment(getLoginUserId(), momentId);
        return success(true);
    }

    @PostMapping("/comment")
    @Operation(summary = "评论")
    public CommonResult<Long> comment(@RequestParam Long momentId,
                                       @RequestParam(required = false) Long replyUserId,
                                       @RequestParam String content) {
        return success(momentService.commentMoment(getLoginUserId(), momentId, replyUserId, content));
    }

    @GetMapping("/comments")
    @Operation(summary = "获取评论列表")
    public CommonResult<List<AppImMomentCommentRespVO>> getComments(@RequestParam Long momentId) {
        return success(buildCommentResp(momentService.getComments(momentId)));
    }

    /** 列表转 JSON 字符串；空列表返回 null（不落库） */
    private String toJson(List<Long> list) {
        return (list != null && !list.isEmpty()) ? JSONUtil.toJsonStr(list) : null;
    }

    private List<AppImMomentCommentRespVO> buildCommentResp(List<ImMomentCommentDO> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        // 收集需要查询昵称/头像的用户 id（评论者 + 被回复者），一次批量查，避免 N+1
        Set<Long> userIds = new HashSet<>();
        for (ImMomentCommentDO c : list) {
            if (c.getUserId() != null) userIds.add(c.getUserId());
            if (c.getReplyUserId() != null) userIds.add(c.getReplyUserId());
        }
        Map<Long, ImUserDO> userMap = userService.getUserMap(userIds);
        List<AppImMomentCommentRespVO> result = new ArrayList<>(list.size());
        for (ImMomentCommentDO c : list) {
            AppImMomentCommentRespVO vo = BeanUtils.toBean(c, AppImMomentCommentRespVO.class);
            ImUserDO u = c.getUserId() != null ? userMap.get(c.getUserId()) : null;
            if (u != null) {
                vo.setNickname(u.getNickname());
                vo.setAvatar(u.getAvatar());
            }
            if (c.getReplyUserId() != null) {
                ImUserDO ru = userMap.get(c.getReplyUserId());
                vo.setReplyNickname(ru != null ? ru.getNickname() : null);
            }
            result.add(vo);
        }
        return result;
    }

    private List<AppImMomentRespVO> buildResp(List<ImMomentDO> list, Long userId) {
        return list.stream().map(m -> {
            AppImMomentRespVO vo = BeanUtils.toBean(m, AppImMomentRespVO.class);
            var user = userService.getUser(m.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            vo.setLiked(momentService.isLiked(userId, m.getId()));
            return vo;
        }).collect(Collectors.toList());
    }
}
