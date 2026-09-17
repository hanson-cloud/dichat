package com.diqin.cloud.module.im.service.review;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerReviewReqVO;

/**
 * IM 消息审核管理 Service 接口
 *
 * @author 速构构
 */
public interface ImMessageReviewManagerService {

    /**
     * 获得消息审核分页
     */
    PageResult<ImMessageReviewManagerRespVO> getMessageReviewPage(ImMessageReviewManagerPageReqVO pageReqVO);

    /**
     * 审核消息（通过/驳回）
     */
    void reviewMessage(ImMessageReviewManagerReviewReqVO reviewReqVO, Long reviewerId);

    /**
     * 自动创建审核记录（消息发送后由 service 层调用）
     */
    void autoCreateReview(Long messageId, Integer chatType, Integer msgType, Long senderId, String contentRaw);

}
