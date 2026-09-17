package com.diqin.cloud.module.im.service.moment;

import com.diqin.cloud.module.im.dal.dataobject.friend.ImFriendDO;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentCommentDO;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentDO;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentLikeDO;
import com.diqin.cloud.module.im.dal.mysql.moment.ImMomentCommentMapper;
import com.diqin.cloud.module.im.dal.mysql.moment.ImMomentLikeMapper;
import com.diqin.cloud.module.im.dal.mysql.moment.ImMomentMapper;
import com.diqin.cloud.module.im.service.friend.ImFriendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;

@Slf4j
@Service
@Validated
public class ImMomentServiceImpl implements ImMomentService {

    @Resource
    private ImMomentMapper momentMapper;
    @Resource
    private ImMomentLikeMapper likeMapper;
    @Resource
    private ImMomentCommentMapper commentMapper;
    @Resource
    private ImFriendService friendService;

    @Override
    public Long createMoment(Long userId, String content, String images, Integer visibility,
                             String location, String visibleUserIds, String invisibleUserIds, String remindUserIds) {
        // 业务校验：不给谁看 与 提醒谁看 不能包含同一人
        if (invisibleUserIds != null && remindUserIds != null) {
            Set<Long> remindSet = new HashSet<>(parseIds(remindUserIds));
            for (Long id : parseIds(invisibleUserIds)) {
                if (remindSet.contains(id)) {
                    throw exception(ErrorCodeConstants.MOMENT_INVISIBLE_REMIND_CONFLICT);
                }
            }
        }
        ImMomentDO moment = ImMomentDO.builder()
                .userId(userId)
                .content(content)
                .images(images)
                .visibility(visibility != null ? visibility : 0)
                .location(location)
                .visibleUserIds(visibleUserIds)
                .invisibleUserIds(invisibleUserIds)
                .remindUserIds(remindUserIds)
                .likeCount(0)
                .commentCount(0)
                .build();
        momentMapper.insert(moment);
        return moment.getId();
    }

    @Override
    public void deleteMoment(Long userId, Long momentId) {
        momentMapper.delete(momentId, userId);
    }

    @Override
    public List<ImMomentDO> getFriendMoments(Long userId, Integer pageNo, Integer pageSize) {
        List<Long> friendIds = friendService.getFriendList(userId).stream()
                .map(ImFriendDO::getFriendUserId).collect(Collectors.toList());
        friendIds.add(userId); // 包含自己的
        List<ImMomentDO> list = momentMapper.selectFriendMoments(friendIds, userId, pageNo, pageSize);
        // Java 层按可见性精确过滤：好友发的"部分可见/不给谁看"需比对当前用户白/黑名单
        return list.stream().filter(m -> {
            if (m.getUserId().equals(userId)) {
                return true; // 自己发的，全部可见
            }
            Integer vis = m.getVisibility();
            if (vis == null || vis == 0) {
                return true; // 公开
            }
            if (vis == 2) { // 部分可见：白名单包含当前用户才可见
                return parseIds(m.getVisibleUserIds()).contains(userId);
            }
            if (vis == 3) { // 不给谁看：黑名单不含当前用户才可见
                return !parseIds(m.getInvisibleUserIds()).contains(userId);
            }
            return false; // 仅自己可见（好友发的）：对其他人不可见
        }).collect(Collectors.toList());
    }

    /** 解析 JSON 数组字符串（visibleUserIds / invisibleUserIds 存储格式，如 "[1,2,3]"）为 Long 列表 */
    private List<Long> parseIds(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.parseArray(json).toList(Long.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ImMomentDO> getMyMoments(Long userId, Integer pageNo, Integer pageSize) {
        return momentMapper.selectListByUserId(userId, pageNo, pageSize);
    }

    @Override
    public void likeMoment(Long userId, Long momentId) {
        ImMomentLikeDO exist = likeMapper.selectByMomentIdAndUserId(momentId, userId);
        if (exist != null) return;
        likeMapper.insert(ImMomentLikeDO.builder().momentId(momentId).userId(userId).build());
        momentMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ImMomentDO>()
                .eq(ImMomentDO::getId, momentId)
                .setSql("like_count = like_count + 1"));
    }

    @Override
    public void unlikeMoment(Long userId, Long momentId) {
        ImMomentLikeDO exist = likeMapper.selectByMomentIdAndUserId(momentId, userId);
        if (exist == null) return;
        likeMapper.deleteById(exist.getId());
        momentMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ImMomentDO>()
                .eq(ImMomentDO::getId, momentId)
                .setSql("like_count = like_count - 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long commentMoment(Long userId, Long momentId, Long replyUserId, String content) {
        ImMomentCommentDO comment = ImMomentCommentDO.builder()
                .momentId(momentId).userId(userId).replyUserId(replyUserId).content(content).build();
        commentMapper.insert(comment);
        momentMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ImMomentDO>()
                .eq(ImMomentDO::getId, momentId)
                .setSql("comment_count = comment_count + 1"));
        return comment.getId();
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        commentMapper.delete(commentId, userId);
    }

    @Override
    public List<ImMomentCommentDO> getComments(Long momentId) {
        return commentMapper.selectListByMomentId(momentId);
    }

    @Override
    public boolean isLiked(Long userId, Long momentId) {
        return likeMapper.selectByMomentIdAndUserId(momentId, userId) != null;
    }
}
