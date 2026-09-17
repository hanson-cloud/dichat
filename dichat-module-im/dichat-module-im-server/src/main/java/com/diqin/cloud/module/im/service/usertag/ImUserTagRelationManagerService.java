package com.diqin.cloud.module.im.service.usertag;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.usertag.vo.ImUserTagRelationManagerSaveReqVO;

/**
 * IM 用户标签关联关系 Service
 *
 * @author 速构构
 */
public interface ImUserTagRelationManagerService {

    /**
     * 获得标签关联分页（按标签或用户筛选，回填昵称与标签名）
     */
    PageResult<ImUserTagRelationManagerRespVO> getRelationManagerPage(ImUserTagRelationManagerPageReqVO pageReqVO);

    /**
     * 批量给用户打标
     *
     * @param createReqVO 标签编号 + 用户编号列表
     */
    void createRelation(ImUserTagRelationManagerSaveReqVO createReqVO);

    /**
     * 取消用户标签
     *
     * @param tagId  标签编号
     * @param userId 用户编号
     */
    void deleteRelation(Long tagId, Long userId);

}
