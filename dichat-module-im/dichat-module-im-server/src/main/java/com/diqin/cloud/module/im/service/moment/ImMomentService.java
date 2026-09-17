package com.diqin.cloud.module.im.service.moment;

import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentCommentDO;
import com.diqin.cloud.module.im.dal.dataobject.moment.ImMomentDO;

import java.util.List;

public interface ImMomentService {

    Long createMoment(Long userId, String content, String images, Integer visibility,
                       String location, String visibleUserIds, String invisibleUserIds, String remindUserIds);

    void deleteMoment(Long userId, Long momentId);

    List<ImMomentDO> getFriendMoments(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 获取指定用户的所有动态列表（按发布时间倒序分页）
     *
     * @param userId 用户ID，用于查询该用户发布的动态
     * @param pageNo 页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 倒序分页后的动态列表，如果用户没有动态则返回空列表
     */
    List<ImMomentDO> getMyMoments(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 点赞功能方法
     *
     * @param userId   用户ID，用于标识执行点赞操作的用户
     * @param momentId 朋友圈动态ID，用于标识被点赞的动态
     */
    void likeMoment(Long userId, Long momentId);

    /**
     * 取消点赞某条动态的方法
     *
     * @param userId   用户ID，标识执行取消点赞操作的用户
     * @param momentId 动态ID，标识被取消点赞的动态
     */
    void unlikeMoment(Long userId, Long momentId);

    /**
     * 评论动态的方法
     *
     * @param userId      当前用户ID，用于标识评论者
     * @param momentId    被评论的动态ID，用于定位具体动态
     * @param replyUserId 被回复的用户ID，用于回复特定用户
     * @param content     评论内容，用户输入的评论文本
     * @return 返回Long类型结果，可能是评论ID或其他状态标识
     */
    Long commentMoment(Long userId, Long momentId, Long replyUserId, String content);

    /**
     * 删除评论的方法
     *
     * @param userId    用户ID，用于验证操作权限
     * @param commentId 要删除的评论ID
     */
    void deleteComment(Long userId, Long commentId);

    List<ImMomentCommentDO> getComments(Long momentId);

    boolean isLiked(Long userId, Long momentId);
}
