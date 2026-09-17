package com.diqin.cloud.module.im.service.friend;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.friend.vo.ImFriendManagerRespVO;

/**
 * 管理后台 - IM 好友关系 Service 接口
 *
 * @author dichat
 */
public interface ImFriendManagerService {

    /**
     * 【管理后台】分页查询好友关系列表
     *
     * @param pageReqVO 分页查询条件
     * @return 好友关系分页列表（含双方昵称）
     */
    PageResult<ImFriendManagerRespVO> getFriendManagerPage(ImFriendManagerPageReqVO pageReqVO);

}
