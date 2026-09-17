package com.diqin.cloud.module.im.service.conversation;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.app.conversation.vo.AppImConversationRespVO;
import com.diqin.cloud.module.im.dal.dataobject.conversation.ImConversationDO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.dataobject.robot.ImRobotDO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import com.diqin.cloud.module.im.dal.mysql.conversation.ImConversationMapper;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceManagerService;
import com.diqin.cloud.module.im.service.robot.ImRobotManagerService;
import com.diqin.cloud.module.im.service.user.ImUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IM 私聊会话 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
@Slf4j
public class ImConversationServiceImpl implements ImConversationService {

    @Resource
    private ImConversationMapper conversationMapper;

    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;

    @Resource
    private ImRobotManagerService robotManagerService;

    @Resource
    private ImUserService imUserService;

    /** 末条消息内容摘要最大长度（对应表字段 varchar(512)） */
    private static final int MAX_SUMMARY_LEN = 512;

    @Override
    public void onMessageSent(Integer senderType, Long senderId, Integer receiverType, Long receiverId,
                              Long messageId, String content, Integer type, LocalDateTime sendTime) {
        // 1. 解析规范 id（过渡期双 ID 统一为服务侧自身 id）
        Long cSenderId = resolveCanonicalId(senderType, senderId);
        Long cReceiverId = resolveCanonicalId(receiverType, receiverId);

        // 2. 规范配对：A = 服务侧（类型值更大者），同类型取较小 id
        Integer aType, bType;
        Long aId, bId;
        if (senderType > receiverType
                || (senderType.equals(receiverType) && cSenderId <= cReceiverId)) {
            aType = senderType; aId = cSenderId;
            bType = receiverType; bId = cReceiverId;
        } else {
            aType = receiverType; aId = cReceiverId;
            bType = senderType; bId = cSenderId;
        }

        // 3. 接收方落在哪一侧（用于未读 +1）
        boolean receiverIsA = (aType.equals(receiverType) && aId.equals(cReceiverId));

        // 4. 查找或创建会话
        ImConversationDO conv = conversationMapper.selectByPair(aType, aId, bType, bId);
        if (conv == null) {
            conv = new ImConversationDO()
                    .setParticipantAType(aType).setParticipantAId(aId)
                    .setParticipantBType(bType).setParticipantBId(bId)
                    .setAUnreadCount(0).setBUnreadCount(0)
                    .setAPinned(false).setBPinned(false)
                    .setADeleted(false).setBDeleted(false);
            conversationMapper.insert(conv);
        }

        // 5. 更新末条消息 + 接收方未读 +1
        String summary = buildSummary(content);
        conversationMapper.updateLastMessage(conv.getId(), messageId, summary, type, sendTime);
        conversationMapper.incrementUnread(conv.getId(), receiverIsA);
    }

    @Override
    public void markRead(Integer readerType, Long readerId, Integer peerType, Long peerId) {
        Long cReaderId = resolveCanonicalId(readerType, readerId);
        Long cPeerId = resolveCanonicalId(peerType, peerId);

        Integer aType, bType;
        Long aId, bId;
        if (readerType > peerType
                || (readerType.equals(peerType) && cReaderId <= cPeerId)) {
            aType = readerType; aId = cReaderId;
            bType = peerType; bId = cPeerId;
        } else {
            aType = peerType; aId = cPeerId;
            bType = readerType; bId = cReaderId;
        }
        ImConversationDO conv = conversationMapper.selectByPair(aType, aId, bType, bId);
        if (conv == null) {
            return;
        }
        boolean readerIsA = (aType.equals(readerType) && aId.equals(cReaderId));
        conversationMapper.resetUnread(conv.getId(), readerIsA);
    }

    @Override
    public List<ImConversationDO> getCsConversations(Long csId) {
        return conversationMapper.selectList(new LambdaQueryWrapperX<ImConversationDO>()
                .eq(ImConversationDO::getParticipantAType, ImMessageParticipantTypeEnum.CS.getType())
                .eq(ImConversationDO::getParticipantAId, csId)
                .eq(ImConversationDO::getADeleted, false)
                .orderByDesc(ImConversationDO::getLastMessageTime));
    }

    @Override
    public List<ImConversationDO> getUserConversations(Long userId) {
        List<ImConversationDO> list = conversationMapper.selectList(new LambdaQueryWrapperX<ImConversationDO>()
                .and(w -> w.eq(ImConversationDO::getParticipantAType, ImMessageParticipantTypeEnum.USER.getType())
                                .eq(ImConversationDO::getParticipantAId, userId)
                                .eq(ImConversationDO::getADeleted, false)
                                .or()
                                .eq(ImConversationDO::getParticipantBType, ImMessageParticipantTypeEnum.USER.getType())
                                .eq(ImConversationDO::getParticipantBId, userId)
                                .eq(ImConversationDO::getBDeleted, false))
                .orderByDesc(ImConversationDO::getLastMessageTime));
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<AppImConversationRespVO> getUserConversationList(Long userId) {
        List<ImConversationDO> conversations = getUserConversations(userId);
        if (conversations.isEmpty()) {
            return List.of();
        }
        // 1. 逐条会话确定「用户侧 / 对端」视角，并收集对端编号用于批量回填
        List<AppImConversationRespVO> result = new ArrayList<>(conversations.size());
        List<Long> userPeerIds = new ArrayList<>();
        List<Long> robotPeerIds = new ArrayList<>();
        List<Long> csPeerIds = new ArrayList<>();
        Integer userType = ImMessageParticipantTypeEnum.USER.getType();
        Integer robotType = ImMessageParticipantTypeEnum.ROBOT.getType();
        Integer csType = ImMessageParticipantTypeEnum.CS.getType();
        for (ImConversationDO c : conversations) {
            boolean userIsA = userType.equals(c.getParticipantAType()) && c.getParticipantAId().equals(userId);
            Integer peerType = userIsA ? c.getParticipantBType() : c.getParticipantAType();
            Long peerId = userIsA ? c.getParticipantBId() : c.getParticipantAId();

            AppImConversationRespVO vo = new AppImConversationRespVO();
            vo.setConversationId(c.getId());
            vo.setPeerType(peerType);
            vo.setPeerId(peerId);
            vo.setLastMessageContent(c.getLastMessageContent());
            vo.setLastMessageType(c.getLastMessageType());
            vo.setLastMessageTime(c.getLastMessageTime());
            vo.setUnreadCount(userIsA ? c.getAUnreadCount() : c.getBUnreadCount());
            vo.setPinned(userIsA ? c.getAPinned() : c.getBPinned());

            if (userType.equals(peerType)) {
                userPeerIds.add(peerId);
            } else if (robotType.equals(peerType)) {
                robotPeerIds.add(peerId);
            } else if (csType.equals(peerType)) {
                csPeerIds.add(peerId);
            }
            result.add(vo);
        }

        // 2. 批量回填对端昵称 / 头像（按类型分表查询）
        Map<Long, ImUserDO> userMap = userPeerIds.isEmpty() ? Map.of() : imUserService.getUserMap(userPeerIds);
        Map<Long, ImRobotDO> robotMap = robotPeerIds.isEmpty() ? Map.of() : robotManagerService.getRobotMap(robotPeerIds);
        Map<Long, ImCustomerServiceDO> csMap = csPeerIds.isEmpty() ? Map.of() : customerServiceManagerService.getCustomerServiceMap(csPeerIds);
        for (AppImConversationRespVO vo : result) {
            if (userType.equals(vo.getPeerType())) {
                ImUserDO u = userMap.get(vo.getPeerId());
                if (u != null) {
                    vo.setPeerNickname(u.getNickname());
                    vo.setPeerAvatar(u.getAvatar());
                }
            } else if (robotType.equals(vo.getPeerType())) {
                ImRobotDO r = robotMap.get(vo.getPeerId());
                if (r != null) {
                    vo.setPeerNickname(r.getNickname());
                    vo.setPeerAvatar(r.getAvatar());
                }
            } else if (csType.equals(vo.getPeerType())) {
                ImCustomerServiceDO cs = csMap.get(vo.getPeerId());
                if (cs != null) {
                    vo.setPeerNickname(cs.getNickname());
                    vo.setPeerAvatar(cs.getAvatar());
                }
            }
        }
        return result;
    }

    /**
     * 把参与方编号解析为「规范 id」。方案 C 下客服/机器人/用户均以各自表主键作为唯一地址，
     * 不再经由 im_users.id 桥接，故传入编号即规范 id，原样返回即可。
     */
    private Long resolveCanonicalId(Integer type, Long id) {
        return id;
    }

    /**
     * 末条消息内容摘要：文本原样截断；非文本类型存空串（前端按 type 显示占位文案）。
     */
    private String buildSummary(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        String plain = content;
        if (plain.length() > MAX_SUMMARY_LEN) {
            plain = plain.substring(0, MAX_SUMMARY_LEN);
        }
        return plain;
    }

}
