package com.diqin.cloud.module.im.service.redpacket;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.redpacket.vo.ImRedPacketRefundStatusRespVO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketDO;
import com.diqin.cloud.module.im.dal.dataobject.redpacket.ImRedPacketGrabDO;
import com.diqin.cloud.module.im.dto.redpacket.ImRedPacketSendReqDTO;

import java.util.List;

/**
 * IM 红包 Service 接口
 *
 * @author dichat
 */
public interface ImRedPacketService {

    /**
     * 发送红包（冻结发送方钱包余额）
     */
    ImRedPacketDO send(Long userId, ImRedPacketSendReqDTO reqDTO);

    /**
     * 领取红包（增加时加领取方钱包余额）
     */
    ImRedPacketGrabDO grabRedPacket(Long userId, String no);

    /**
     * 按单号查询红包
     */
    ImRedPacketDO getRedPacketByNo(String no);

    /**
     * 查询当前用户对某红包的领取记录
     */
    ImRedPacketGrabDO getMyGrab(String no, Long userId);

    /**
     * 我发出的红包分页
     */
    PageResult<ImRedPacketDO> getRedPacketPageMySent(Long userId, PageParam pageReqVO);

    /**
     * 我抢到的红包分页
     */
    PageResult<ImRedPacketDO> getRedPacketPageMyGrabbed(Long userId, PageParam pageReqVO);

    // ==================== 管理后台 ====================

    /**
     * 按编号查询红包
     */
    ImRedPacketDO getRedPacket(Long id);

    /**
     * 红包分页
     */
    PageResult<ImRedPacketDO> getRedPacketPage(ImRedPacketManagerPageReqVO reqVO);

    /**
     * 红包领取明细列表
     */
    List<ImRedPacketGrabDO> getGrabList(Long redPacketId);

    /**
     * 退款（仅已过期且未领完）
     */
    void refundRedPacket(Long id);

    /**
     * 批量退款超时红包（Job 入口）
     *
     * @return 成功退款的红包数量
     */
    int refundExpiredPackets();

    /**
     * 获得红包退款状态（管理后台）
     */
    ImRedPacketRefundStatusRespVO getRedPacketRefundStatus(Long id);

}
