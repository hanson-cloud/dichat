package com.diqin.cloud.module.im.service.customer_service;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsConversationPageReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsConversationRespVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsMessageListReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCsMessageSendReqVO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.dataobject.message.ImPrivateMessageDO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 管理端「客服工作台」Service
 * <p>以当前登录管理员绑定的客服身份，提供会话列表、消息查询、代发消息与实时消息流（SSE）。</p>
 *
 * @author 速构构
 */
public interface ImCustomerServiceConsoleService {

    /**
     * 解析当前管理员对应的客服身份；未绑定则抛 {@code CUSTOMER_SERVICE_ADMIN_NOT_BOUND}
     *
     * @param adminUserId 管理后台账号编号（system_users.id）
     * @return 客服配置
     */
    ImCustomerServiceDO resolveCs(Long adminUserId);

    /**
     * 会话列表（按最近消息时间倒序分页）
     *
     * @param reqVO       分页请求
     * @param adminUserId 当前登录管理员编号
     */
    PageResult<ImCsConversationRespVO> getConversationPage(ImCsConversationPageReqVO reqVO, Long adminUserId);

    /**
     * 客服打开与某用户的会话时，清零该会话在客服侧的未读数
     *
     * @param peerId       对端用户编号（im_users.id）
     * @param adminUserId  当前登录管理员编号
     */
    void markRead(Long peerId, Long adminUserId);

    /**
     * 指定用户会话的消息列表（游标翻页，按 id 倒序）
     *
     * @param reqVO       请求（含 peerId / maxId / limit）
     * @param adminUserId 当前登录管理员编号
     */
    List<ImPrivateMessageDO> getMessageList(ImCsMessageListReqVO reqVO, Long adminUserId);

    /**
     * 以客服身份向指定用户代发消息（落库 + 推送给 APP 端用户）
     *
     * @param reqVO       发送请求
     * @param adminUserId 当前登录管理员编号
     * @return 落库后的消息
     */
    ImPrivateMessageDO sendMessage(ImCsMessageSendReqVO reqVO, Long adminUserId);

    /**
     * 打开实时消息流（SSE）；按当前管理员对应客服编号订阅，收到用户新消息即时推送
     *
     * @param adminUserId 当前登录管理员编号
     */
    SseEmitter openStream(Long adminUserId);

    /**
     * 获取当前管理员所绑定客服的在线状态（0-离线 1-在线 2-忙碌）
     *
     * @param adminUserId 当前登录管理员编号
     * @return 状态值；未绑定客服时由 {@link #resolveCs} 抛错
     */
    Integer getMyStatus(Long adminUserId);

    /**
     * 手动切换当前客服的在线状态（0-离线 1-在线 2-忙碌）。
     * <p>「连线→忙碌」「断线→离线」由 SSE 连接状态自动驱动；
     * 本方法供坐席在工作台内手动在「在线 / 忙碌 / 下线」间切换。</p>
     *
     * @param adminUserId 当前登录管理员编号
     * @param status      目标状态
     */
    void setStatus(Long adminUserId, Integer status);

}
