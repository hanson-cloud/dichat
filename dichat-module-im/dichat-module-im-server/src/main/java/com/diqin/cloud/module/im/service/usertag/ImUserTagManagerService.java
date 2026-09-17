package com.diqin.cloud.module.im.service.usertag;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagManagerUpdateReqVO;

/**
 * IM 用户标签管理 Service
 *
 * @author 速构构
 */
public interface ImUserTagManagerService {

    /**
     * 获得用户标签分页
     */
    PageResult<ImUserTagManagerRespVO> getTagManagerPage(ImUserTagManagerPageReqVO pageReqVO);

    /**
     * 获得用户标签详情
     */
    ImUserTagManagerRespVO getTag(Long id);

    /**
     * 创建用户标签
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTag(ImUserTagManagerSaveReqVO createReqVO);

    /**
     * 更新用户标签
     *
     * @param updateReqVO 更新信息
     */
    void updateTag(ImUserTagManagerUpdateReqVO updateReqVO);

    /**
     * 删除用户标签（级联删除关联关系）
     *
     * @param id 编号
     */
    void deleteTag(Long id);

}
