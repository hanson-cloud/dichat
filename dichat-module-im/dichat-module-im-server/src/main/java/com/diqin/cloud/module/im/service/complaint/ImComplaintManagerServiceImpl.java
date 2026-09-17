package com.diqin.cloud.module.im.service.complaint;

import cn.hutool.core.collection.CollUtil;
import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.collection.CollectionUtils;
import com.diqin.cloud.framework.common.util.collection.MapUtils;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerHandleReqVO;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.complaint.vo.ImComplaintManagerRespVO;
import com.diqin.cloud.module.im.controller.app.complaint.vo.AppImComplaintSubmitReqVO;
import com.diqin.cloud.module.im.dal.dataobject.complaint.ImComplaintDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.complaint.ImComplaintMapper;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.im.service.user.ImUserService;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertSet;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * 管理后台 - IM 用户投诉（举报） Service 实现类
 *
 * @author dichat
 */
@Service
public class ImComplaintManagerServiceImpl implements ImComplaintManagerService {

    @Resource
    private ImComplaintMapper complaintMapper;
    @Resource
    private ImUserService userService;

    @Override
    public PageResult<ImComplaintManagerRespVO> getComplaintManagerPage(ImComplaintManagerPageReqVO pageReqVO) {
        PageResult<ImComplaintDO> pageResult = complaintMapper.selectPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty(pageResult.getTotal());
        }
        // 批量查询用户，回填投诉人 / 被投诉人昵称
        Set<Long> userIds = convertSet(pageResult.getList(), ImComplaintDO::getComplainantId);
        userIds.addAll(convertSet(pageResult.getList(), ImComplaintDO::getRespondentId, Objects::nonNull));
        Map<Long, ImUserDO> userMap = CollectionUtils.convertMap(userService.getUserList(userIds), ImUserDO::getId);
        return BeanUtils.toBean(pageResult, ImComplaintManagerRespVO.class, vo -> {
            MapUtils.findAndThen(userMap, vo.getComplainantId(), user -> vo.setComplainantNickname(user.getNickname()));
            MapUtils.findAndThen(userMap, vo.getRespondentId(), user -> vo.setRespondentNickname(user.getNickname()));
        });
    }

    @Override
    @LogRecord(type = IM_COMPLAINT_TYPE, subType = IM_COMPLAINT_HANDLE_SUB_TYPE, bizNo = "{{#reqVO.id}}", success = IM_COMPLAINT_HANDLE_SUCCESS)
    public void handleComplaint(Long operatorUserId, ImComplaintManagerHandleReqVO reqVO) {
        ImComplaintDO complaint = complaintMapper.selectById(reqVO.getId());
        if (complaint == null) {
            throw new IllegalArgumentException("投诉记录不存在");
        }
        complaintMapper.updateById(new ImComplaintDO().setId(reqVO.getId())
                .setStatus(2) // 已处理
                .setPunishment(reqVO.getPunishment())
                .setHandleResult(reqVO.getHandleResult())
                .setHandleUserId(operatorUserId)
                .setHandleTime(LocalDateTime.now()));
        LogRecordContext.putVariable("reqVO", reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFeedback(Long userId, AppImComplaintSubmitReqVO reqVO) {
        int category = reqVO.getCategory() == null ? 6 : reqVO.getCategory();
        // 1-5 为举报类别，6 为意见反馈；其它视为非法
        if (category < 1 || category > 6) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.FEEDBACK_CATEGORY_INVALID);
        }
        ImComplaintDO complaint = new ImComplaintDO();
        complaint.setComplainantId(userId);
        complaint.setRespondentId(null); // 意见反馈无特定被诉人
        complaint.setCategory(category);
        complaint.setContent(reqVO.getContent());
        // 前端可携带联系方式（选填），复用 evidenceUrls 字段承载，避免新增列
        if (reqVO.getContact() != null && !reqVO.getContact().trim().isEmpty()) {
            complaint.setEvidenceUrls(reqVO.getContact().trim());
        }
        complaint.setStatus(0); // 待处理
        complaintMapper.insert(complaint);
    }

}
