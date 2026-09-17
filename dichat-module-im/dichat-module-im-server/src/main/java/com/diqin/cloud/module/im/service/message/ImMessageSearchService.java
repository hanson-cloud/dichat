package com.diqin.cloud.module.im.service.message;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchReqVO;
import com.diqin.cloud.module.im.controller.admin.message.vo.search.ImMessageSearchRespVO;

/**
 * IM 全局消息搜索 Service 接口
 */
public interface ImMessageSearchService {

    /**
     * 全局搜索消息（跨私聊+群聊）
     */
    PageResult<ImMessageSearchRespVO> searchMessages(ImMessageSearchReqVO reqVO);

}
