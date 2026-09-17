package com.diqin.cloud.module.im.service.complaint;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerHandleReqVO;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerRespVO;
import com.diqin.cloud.module.im.controller.app.complaint.vo.AppImComplaintSubmitReqVO;

/**
 * 管理后台 - IM 用户投诉（举报） Service 接口
 *
 * @author dichat
 */
public interface ImComplaintManagerService {

    /**
     * 【管理后台】分页查询投诉（举报）记录列表
     *
     * @param pageReqVO 分页查询条件
     * @return 投诉记录分页列表（含投诉人 / 被投诉人昵称）
     */
    PageResult<ImComplaintManagerRespVO> getComplaintManagerPage(ImComplaintManagerPageReqVO pageReqVO);

    /**
     * 【管理后台】审核处置投诉（举报）记录
     *
     * @param operatorUserId 操作人管理员编号
     * @param reqVO          处置信息（含投诉编号、处罚措施、处理结果描述）
     */
    void handleComplaint(Long operatorUserId, ImComplaintManagerHandleReqVO reqVO);

    /**
     * 【C 端用户】提交意见反馈 / 举报
     * <p>写入 im_complaint，status=0（待处理）；category 默认 6（意见反馈），1-5 为举报。
     *
     * @param userId 提交人用户编号
     * @param reqVO  提交内容
     */
    void submitFeedback(Long userId, AppImComplaintSubmitReqVO reqVO);

}
