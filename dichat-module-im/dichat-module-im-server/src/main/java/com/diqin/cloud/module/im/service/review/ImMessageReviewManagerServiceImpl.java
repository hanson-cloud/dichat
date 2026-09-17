package com.diqin.cloud.module.im.service.review;

import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.review.vo.ImMessageReviewManagerReviewReqVO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImGroupMessageDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import com.diqin.cloud.module.im.dal.dataobject.review.ImMessageReviewDO;
import com.diqin.cloud.module.im.dal.mysql.message.ImGroupMessageMapper;
import com.diqin.cloud.module.im.dal.mysql.message.ImPrivateMessageMapper;
import com.diqin.cloud.module.im.dal.mysql.review.ImMessageReviewMapper;
import com.diqin.cloud.module.im.service.group.ImGroupMemberService;
import com.diqin.cloud.module.im.service.websocket.ImWebSocketService;
import com.diqin.cloud.module.im.service.websocket.dto.ImGroupMessageDTO;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.MESSAGE_REVIEW_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 消息审核管理 Service 实现类
 *
 * <p>审核通过时自动推送消息给接收方（先审后发），审核驳回时不推送。
 *
 * @author 速构构
 */
@Service
@Validated
public class ImMessageReviewManagerServiceImpl implements ImMessageReviewManagerService {

    @Resource
    private ImMessageReviewMapper messageReviewMapper;
    @Resource
    private ImPrivateMessageMapper privateMessageMapper;
    @Resource
    private ImGroupMessageMapper groupMessageMapper;
    @Resource
    private ImWebSocketService imWebSocketService;
    @Resource
    private ImGroupMemberService groupMemberService;

    @Override
    public PageResult<ImMessageReviewManagerRespVO> getMessageReviewPage(ImMessageReviewManagerPageReqVO pageReqVO) {
        PageResult<ImMessageReviewDO> pageResult = messageReviewMapper.selectPage(pageReqVO);
        return BeanUtils.toBean(pageResult, ImMessageReviewManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_MESSAGE_REVIEW_TYPE, subType = IM_MESSAGE_REVIEW_REVIEW_SUB_TYPE, bizNo = "{{#reviewReqVO.id}}", success = IM_MESSAGE_REVIEW_REVIEW_SUCCESS)
    public void reviewMessage(ImMessageReviewManagerReviewReqVO reviewReqVO, Long reviewerId) {
        ImMessageReviewDO review = messageReviewMapper.selectById(reviewReqVO.getId());
        if (review == null) {
            throw ServiceExceptionUtil.exception(MESSAGE_REVIEW_NOT_EXISTS);
        }

        // 更新审核状态
        messageReviewMapper.updateById(new ImMessageReviewDO()
                .setId(reviewReqVO.getId())
                .setReviewStatus(reviewReqVO.getReviewStatus())
                .setReviewerId(reviewerId)
                .setReviewReason(reviewReqVO.getReviewReason() == null ? "" : reviewReqVO.getReviewReason())
                .setReviewTime(LocalDateTime.now()));

        LogRecordContext.putVariable("reqVO", reviewReqVO);

        // 审核通过 → 推送消息给接收方（先审后发）
        if (reviewReqVO.getReviewStatus() == 1) {
            pushApprovedMessage(review);
        }
    }

    /**
     * 审核通过后推送被拦截的消息给接收方
     */
    private void pushApprovedMessage(ImMessageReviewDO review) {
        if (review.getChatType() == 1) {
            // 私聊消息
            ImPrivateMessageDO msg = privateMessageMapper.selectById(review.getMessageId());
            if (msg != null) {
                ImPrivateMessageDTO dto = ImPrivateMessageDTO.ofSend(msg);
                imWebSocketService.sendPrivateMessageAsync(msg.getReceiverId(), dto);
            }
        } else {
            // 群聊消息
            ImGroupMessageDO msg = groupMessageMapper.selectById(review.getMessageId());
            if (msg != null) {
                List<Long> memberUserIds = groupMemberService.getActiveGroupMemberUserIdsByGroupId(msg.getGroupId());
                ImGroupMessageDTO dto = ImGroupMessageDTO.ofSend(msg);
                // 推给全部可见成员（含发送方，确保多端同步）
                imWebSocketService.sendGroupMessageAsync(Set.copyOf(memberUserIds), dto);
            }
        }
    }

    @Override
    public void autoCreateReview(Long messageId, Integer chatType, Integer msgType, Long senderId, String contentRaw) {
        ImMessageReviewDO review = new ImMessageReviewDO();
        review.setMessageId(messageId);
        review.setChatType(chatType);
        review.setMsgType(msgType);
        review.setSenderId(senderId);
        review.setContentPreview(contentRaw);
        review.setReviewStatus(0); // 待审核
        messageReviewMapper.insert(review);
    }

}
