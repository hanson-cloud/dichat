package com.diqin.cloud.module.im.service.blacklist;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.blacklist.vo.ImBlacklistManagerSaveReqVO;

/**
 * 管理后台 - IM 全局黑名单 Service 接口
 *
 * @author dichat
 */
public interface ImBlacklistManagerService {

    /**
     * 【管理后台】分页查询黑名单记录列表
     *
     * @param pageReqVO 分页查询条件
     * @return 黑名单记录分页列表（含操作人 / 被拉黑人昵称）
     */
    PageResult<ImBlacklistManagerRespVO> getBlacklistManagerPage(ImBlacklistManagerPageReqVO pageReqVO);

    /**
     * 【管理后台】新增黑名单记录
     *
     * @param saveReqVO 新增信息（含操作人、被拉黑人、原因）
     * @return 新增记录编号
     */
    Long createBlacklist(ImBlacklistManagerSaveReqVO saveReqVO);

    /**
     * 【管理后台】移除黑名单记录
     *
     * @param id 黑名单记录编号
     */
    void deleteBlacklist(Long id);

}
