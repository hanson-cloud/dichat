package com.diqin.cloud.module.im.service.friend;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendRequestManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendRequestManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.friend.ImFriendRequestDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.friend.ImFriendRequestMapper;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 管理后台 - IM 好友申请 Service 实现类
 *
 * @author dichat
 */
@Service
public class ImFriendRequestManagerServiceImpl implements ImFriendRequestManagerService {

    @Resource
    private ImFriendRequestMapper friendRequestMapper;
    @Resource
    private ImUserService userService;

    @Override
    public PageResult<ImFriendRequestManagerRespVO> getFriendRequestManagerPage(ImFriendRequestManagerPageReqVO pageReqVO) {
        PageResult<ImFriendRequestDO> pageResult = friendRequestMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 批量查询用户，回填双方昵称
        Set<Long> userIds = convertSet(pageResult.getList(), ImFriendRequestDO::getFromUserId);
        userIds.addAll(convertSet(pageResult.getList(), ImFriendRequestDO::getToUserId));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userService.getUserList(userIds), ImUserDO::getId);
        return BeanUtils.toBean(pageResult, ImFriendRequestManagerRespVO.class, vo -> {
            MapUtils.findAndThen(userMap, vo.getFromUserId(), user -> vo.setFromNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getToUserId(), user -> vo.setToNickname(user.getNickname()));
        });
    }

}
