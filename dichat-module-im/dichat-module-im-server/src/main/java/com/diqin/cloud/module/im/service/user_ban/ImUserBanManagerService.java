package com.diqin.cloud.module.im.service.user_ban;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.user_ban.vo.*;

/**
 * IM 用户封禁管理 Service 接口
 */
public interface ImUserBanManagerService {
    PageResult<ImUserBanManagerRespVO> getUserBanPage(ImUserBanManagerPageReqVO pageReqVO);
    ImUserBanManagerRespVO getUserBan(Long id);
    Long createUserBan(ImUserBanManagerSaveReqVO createReqVO, Long bannedBy);
    void updateUserBan(ImUserBanManagerUpdateReqVO updateReqVO);
    void unbanUserBan(ImUserBanManagerUnbanReqVO unbanReqVO, Long unbannedBy);
    void deleteUserBan(Long id);
}
