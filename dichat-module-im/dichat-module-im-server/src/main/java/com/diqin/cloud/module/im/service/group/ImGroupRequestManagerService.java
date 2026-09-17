package com.diqin.cloud.module.im.service.group;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupRequestManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.group.vo.ImGroupRequestManagerRespVO;

/**
 * 管理后台 - IM 加群申请 Service 接口
 *
 * @author dichat
 */
public interface ImGroupRequestManagerService {

    /**
     * 【管理后台】分页查询加群申请列表
     *
     * @param pageReqVO 分页查询条件
     * @return 加群申请分页列表（含群名与各方昵称）
     */
    PageResult<ImGroupRequestManagerRespVO> getGroupRequestManagerPage(ImGroupRequestManagerPageReqVO pageReqVO);

}
