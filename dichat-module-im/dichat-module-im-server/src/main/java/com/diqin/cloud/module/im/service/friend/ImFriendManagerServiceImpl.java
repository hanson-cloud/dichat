package com.diqin.cloud.module.im.service.friend;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.friend.ImFriendDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.friend.ImFriendMapper;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 管理后台 - IM 好友关系 Service 实现类
 *
 * @author dichat
 */
@Service
public class ImFriendManagerServiceImpl implements ImFriendManagerService {

    @Resource
    private ImFriendMapper friendMapper;
    @Resource
    private ImUserService userService;

    @Override
    public PageResult<ImFriendManagerRespVO> getFriendManagerPage(ImFriendManagerPageReqVO pageReqVO) {
        PageResult<ImFriendDO> pageResult = friendMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 批量查询用户，回填双方昵称
        Set<Long> userIds = convertSet(pageResult.getList(), ImFriendDO::getUserId);
        userIds.addAll(convertSet(pageResult.getList(), ImFriendDO::getFriendUserId));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userService.getUserList(userIds), ImUserDO::getId);
        return BeanUtils.toBean(pageResult, ImFriendManagerRespVO.class, vo -> {
            MapUtils.findAndThen(userMap, vo.getUserId(), user -> vo.setUserNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getFriendUserId(), user -> vo.setFriendNickname(user.getNickname()));
        });
    }

}
