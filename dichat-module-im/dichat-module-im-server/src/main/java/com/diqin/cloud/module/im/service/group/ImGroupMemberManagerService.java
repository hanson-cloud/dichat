package com.diqin.cloud.module.im.service.group;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupMemberManagerUpdateReqVO;

/**
 * 管理后台 - IM 群成员 Service 接口
 *
 * @author dichat
 */
public interface ImGroupMemberManagerService {

    /**
     * 【管理后台】分页查询群成员列表
     *
     * @param pageReqVO 分页查询条件
     * @return 群成员分页列表（含用户昵称）
     */
    PageResult<ImGroupMemberManagerRespVO> getGroupMemberManagerPage(ImGroupMemberManagerPageReqVO pageReqVO);

    /**
     * 【管理后台】添加群成员
     *
     * @param reqVO          添加信息（群编号 / 用户编号 / 角色）
     * @param operatorUserId 操作人管理员编号
     * @return 新成员记录编号
     */
    Long createGroupMember(ImGroupMemberManagerSaveReqVO reqVO, Long operatorUserId);

    /**
     * 【管理后台】修改群成员角色
     *
     * @param reqVO 修改信息（群编号 / 用户编号 / 新角色）
     */
    void updateGroupMemberRole(ImGroupMemberManagerUpdateReqVO reqVO);

    /**
     * 【管理后台】移除群成员
     *
     * @param groupId 群编号
     * @param userId  用户编号
     */
    void deleteGroupMember(Long groupId, Long userId);

}
