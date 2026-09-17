package com.diqin.cloud.module.im.service.group;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.enums.CommonStatusEnum;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerUpdateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.group.ImGroupMemberDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.group.ImGroupMemberMapper;
import com.diqin.cloud.module.im.enums.group.ImGroupAddSourceEnum;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * 管理后台 - IM 群成员 Service 实现类
 * <p>
 * 写操作委托给既有的 {@link ImGroupMemberService}（入群 / 踢出 / 改角色等业务逻辑统一收口），
 * 本类只负责管理后台专属的分页查询 + 用户昵称回填。
 *
 * @author dichat
 */
@Service
public class ImGroupMemberManagerServiceImpl implements ImGroupMemberManagerService {

    @Resource
    private ImGroupMemberMapper groupMemberMapper;
    @Resource
    private ImGroupMemberService groupMemberService;
    @Resource
    private ImUserService userService;

    @Override
    public PageResult<ImGroupMemberManagerRespVO> getGroupMemberManagerPage(ImGroupMemberManagerPageReqVO pageReqVO) {
        PageResult<ImGroupMemberDO> pageResult = groupMemberMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 批量查询用户，回填昵称
        Set<Long> userIds = convertSet(pageResult.getList(), ImGroupMemberDO::getUserId);
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userService.getUserList(userIds), ImUserDO::getId);
        return BeanUtils.toBean(pageResult, ImGroupMemberManagerRespVO.class, vo -> MapUtils.findAndThen(userMap, vo.getUserId(), user -> vo.setUserNickname(user.getNickname())));
    }

    @Override
    @LogRecord(type = IM_GROUP_MEMBER_TYPE, subType = IM_GROUP_MEMBER_CREATE_SUB_TYPE, bizNo = "{{#reqVO.groupId}}-{{#reqVO.userId}}", success = IM_GROUP_MEMBER_CREATE_SUCCESS)
    public Long createGroupMember(ImGroupMemberManagerSaveReqVO reqVO, Long operatorUserId) {
        // 已是有效成员则不允许重复添加
        ImGroupMemberDO existing = groupMemberService.getGroupMember(reqVO.getGroupId(), reqVO.getUserId());
        if (existing != null && CommonStatusEnum.ENABLE.getStatus().equals(existing.getStatus())) {
            throw new IllegalArgumentException("该用户已是群成员");
        }
        ImGroupMemberDO member = groupMemberService.addGroupMember(
                reqVO.getGroupId(), reqVO.getUserId(), reqVO.getRole(),
                ImGroupAddSourceEnum.INVITE.getSource(), operatorUserId);
        LogRecordContext.putVariable("reqVO", reqVO);
        return member.getId();
    }

    @Override
    @LogRecord(type = IM_GROUP_MEMBER_TYPE, subType = IM_GROUP_MEMBER_UPDATE_ROLE_SUB_TYPE, bizNo = "{{#reqVO.groupId}}-{{#reqVO.userId}}", success = IM_GROUP_MEMBER_UPDATE_ROLE_SUCCESS)
    public void updateGroupMemberRole(ImGroupMemberManagerUpdateReqVO reqVO) {
        LogRecordContext.putVariable("reqVO", reqVO);
        groupMemberService.updateGroupMemberRole(
                reqVO.getGroupId(), Collections.singleton(reqVO.getUserId()), reqVO.getRole());
    }

    @Override
    @LogRecord(type = IM_GROUP_MEMBER_TYPE, subType = IM_GROUP_MEMBER_DELETE_SUB_TYPE, bizNo = "{{#groupId}}-{{#userId}}", success = IM_GROUP_MEMBER_DELETE_SUCCESS)
    public void deleteGroupMember(Long groupId, Long userId) {
        groupMemberService.removeGroupMember(groupId, userId);
    }

}
