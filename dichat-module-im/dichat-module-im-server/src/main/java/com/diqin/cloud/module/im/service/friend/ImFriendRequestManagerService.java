package com.diqin.cloud.module.im.service.friend;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendRequestManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendRequestManagerRespVO;

/**
 * 管理后台 - IM 好友申请 Service 接口
 *
 * @author dichat
 */
public interface ImFriendRequestManagerService {

    /**
     * 【管理后台】分页查询好友申请列表
     *
     * @param pageReqVO 分页查询条件
     * @return 好友申请分页列表（含双方昵称）
     */
    PageResult<ImFriendRequestManagerRespVO> getFriendRequestManagerPage(ImFriendRequestManagerPageReqVO pageReqVO);

}
