package com.diqin.cloud.module.im.service.customer_service;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerUpdateReqVO;
import com.diqin.cloud.module.im.controller.app.customer_service.vo.AppImCustomerServiceCurrentRespVO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;

import java.util.Collection;
import java.util.Map;

/**
 * IM 客服管理 Service 接口
 *
 * @author 速构构
 */
public interface ImCustomerServiceManagerService {

    /**
     * 获得客服分页
     */
    PageResult<ImCustomerServiceManagerRespVO> getCustomerServiceManagerPage(ImCustomerServiceManagerPageReqVO pageReqVO);

    /**
     * 获得客服详情
     */
    ImCustomerServiceManagerRespVO getCustomerService(Long id);

    /**
     * 创建客服
     */
    Long createCustomerService(ImCustomerServiceManagerSaveReqVO createReqVO);

    /**
     * 更新客服
     */
    void updateCustomerService(ImCustomerServiceManagerUpdateReqVO updateReqVO);

    /**
     * 删除客服
     */
    void deleteCustomerService(Long id);

    /**
     * 获取当前可接入的客服（在线且已关联真实用户，按「最低并发」挑选）；无在线客服时返回 null
     * <p>移动端「联系客服」据此打开 IM 私聊会话：返回的 {@code userId} 即客服绑定的 im_users.id，
     * 作为私聊的 targetId（向后兼容）；{@code csId} + {@code participantType=3} 标识该会话是「人工客服」独立参与方。
     */
    AppImCustomerServiceCurrentRespVO getCurrentCustomerService();

    /**
     * 自动分配：挑一个「在线且已绑定 IM 身份（im_user_id）」的客服，按近段时间活跃会话数（最低并发）升序取首个；
     * 跳过已达 max_concurrent 上限的客服；无可用时返回 null。
     * <p>供机器人「转人工」动作自动分配接手客服；返回的 DO 含 {@code userId}(IM 身份) / 昵称 / 欢迎语，便于引擎组消息。
     */
    ImCustomerServiceDO pickOnlineCustomerService();

    /**
     * 判断用户近一段时间内是否已与任意客服（im_user_id）有过私聊，
     * 用于「转人工」去重冷却：已存在活跃客服会话时不再重复转接开新会话。
     *
     * @param userId 用户 im_users.id
     * @return 存在任一近期客服私聊则为 true
     */
    boolean hasActiveCsConversation(Long userId);

    /**
     * 批量获取客服（按 id），用于后台消息列表 / 导出时的「发送方 / 接收方昵称」回填
     * <p>方案 C：客服成为独立参与方后，私聊以 im_customer_service.id + CS 类型寻址。</p>
     *
     * @param ids 客服编号集合
     * @return id → 客服配置
     */
    Map<Long, ImCustomerServiceDO> getCustomerServiceMap(Collection<Long> ids);

    /**
     * 根据关联的管理后台账号编号（system_users.id）获取客服配置
     * <p>管理端「客服工作台」用当前登录管理员编号反查其坐席身份；未绑定时返回 {@code null}。</p>
     *
     * @param adminUserId 管理后台账号编号（system_users.id）
     * @return 客服配置；未绑定时返回 {@code null}
     */
    ImCustomerServiceDO getCustomerServiceByAdminUserId(Long adminUserId);

    /**
     * 直接按客服编号设置在线状态（0-离线 1-在线 2-忙碌）。
     * <p>供客服工作台「连线自动忙碌 / 断线自动下线」以及坐席手动切换在线状态使用；
     * 状态变化会广播给在线客户端刷新「联系客服」。</p>
     *
     * @param csId   客服编号
     * @param status 目标状态：0-离线 1-在线 2-忙碌
     */
    void setStatus(Long csId, Integer status);

}
