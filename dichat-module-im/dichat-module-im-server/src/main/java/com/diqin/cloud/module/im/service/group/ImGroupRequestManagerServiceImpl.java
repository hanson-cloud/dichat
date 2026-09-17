package com.diqin.cloud.module.im.service.group;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupRequestManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupRequestManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupDO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupRequestDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.group.ImGroupRequestMapper;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 管理后台 - IM 加群申请 Service 实现类
 *
 * @author dichat
 */
@Service
public class ImGroupRequestManagerServiceImpl implements ImGroupRequestManagerService {

    @Resource
    private ImGroupRequestMapper groupRequestMapper;
    @Resource
    private ImUserService userService;
    @Resource
    private ImGroupService groupService;

    @Override
    public PageResult<ImGroupRequestManagerRespVO> getGroupRequestManagerPage(ImGroupRequestManagerPageReqVO pageReqVO) {
        PageResult<ImGroupRequestDO> pageResult = groupRequestMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 批量查询用户与群，回填群名及各方昵称
        Set<Long> groupIds = convertSet(pageResult.getList(), ImGroupRequestDO::getGroupId);
        Set<Long> userIds = convertSet(pageResult.getList(), ImGroupRequestDO::getUserId);
        Set<Long> inviterIds = convertSet(pageResult.getList(), ImGroupRequestDO::getInviterUserId);
        Set<Long> handleIds = convertSet(pageResult.getList(), ImGroupRequestDO::getHandleUserId);
        Set<Long> allUserIds = new HashSet<>();
        allUserIds.addAll(userIds);
        allUserIds.addAll(inviterIds);
        allUserIds.addAll(handleIds);
        allUserIds.remove(null);
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userService.getUserList(allUserIds), ImUserDO::getId);
        Map<Long, ImGroupDO> groupMap = groupService.getGroupMap(groupIds);
        return BeanUtils.toBean(pageResult, ImGroupRequestManagerRespVO.class, vo -> {
            MapUtils.findAndThen(groupMap, vo.getGroupId(), group -> vo.setGroupName(group.getName()));
            MapUtils.findAndThen(userMap, vo.getUserId(), user -> vo.setUserNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getInviterUserId(), user -> vo.setInviterNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getHandleUserId(), user -> vo.setHandleNickname(user.getNickname()));
        });
    }

}
