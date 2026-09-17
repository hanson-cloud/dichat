package com.diqin.cloud.module.im.service.customer_service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerRespVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerSaveReqVO;
import com.diqin.cloud.module.im.controller.admin.customer_service.vo.ImCustomerServiceManagerUpdateReqVO;
import com.diqin.cloud.module.im.controller.app.customer_service.vo.AppImCustomerServiceCurrentRespVO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceActiveStatDO;
import com.diqin.cloud.module.im.dal.dataobject.customer_service.ImCustomerServiceDO;
import com.diqin.cloud.module.im.dal.mysql.customer_service.ImCustomerServiceMapper;
import com.diqin.cloud.module.im.enums.message.ImMessageParticipantTypeEnum;
import com.diqin.cloud.module.im.framework.config.ImProperties;
import com.diqin.cloud.module.im.service.websocket.ImWebSocketService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.diqin.cloud.module.im.service.websocket.dto.ImPrivateMessageDTO;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.CUSTOMER_SERVICE_NOT_EXISTS;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.CUSTOMER_SERVICE_STATUS_INVALID;
import static com.diqin.cloud.module.im.enums.ErrorCodeConstants.CUSTOMER_SERVICE_USERNAME_DUPLICATE;
import static com.diqin.cloud.module.im.enums.ImLogRecordConstants.*;

/**
 * IM 客服管理 Service 实现类
 *
 * @author 速构构
 */
@Service
@Validated
public class ImCustomerServiceManagerServiceImpl implements ImCustomerServiceManagerService {

    @Resource
    private ImCustomerServiceMapper customerServiceMapper;

    @Resource
    private ImProperties imProperties;

    @Resource
    private ImWebSocketService imWebSocketService;

    @Override
    public PageResult<ImCustomerServiceManagerRespVO> getCustomerServiceManagerPage(ImCustomerServiceManagerPageReqVO pageReqVO) {
        PageResult<ImCustomerServiceDO> pageResult = customerServiceMapper.selectPage(pageReqVO);
        PageResult<ImCustomerServiceManagerRespVO> voPage = BeanUtils.toBean(pageResult, ImCustomerServiceManagerRespVO.class);
        // 填充「当前活跃会话数」：窗口内与不同对端的私聊数，用于列表展示客服实时负载
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(imProperties.getRobot().getLoadBalanceWindowMinutes());
        Map<Long, Integer> statMap = customerServiceMapper.selectActiveConversationStats(windowStart).stream()
                .collect(Collectors.toMap(ImCustomerServiceActiveStatDO::getCsId,
                        ImCustomerServiceActiveStatDO::getActiveConversations, (a, b) -> a));
        voPage.getList().forEach(vo -> vo.setActiveConversations(statMap.getOrDefault(vo.getId(), 0)));
        return voPage;
    }

    @Override
    public ImCustomerServiceManagerRespVO getCustomerService(Long id) {
        ImCustomerServiceDO cs = customerServiceMapper.selectById(id);
        return BeanUtils.toBean(cs, ImCustomerServiceManagerRespVO.class);
    }

    @Override
    @LogRecord(type = IM_CUSTOMER_SERVICE_TYPE, subType = IM_CUSTOMER_SERVICE_CREATE_SUB_TYPE, bizNo = "{{#result}}", success = IM_CUSTOMER_SERVICE_CREATE_SUCCESS)
    public Long createCustomerService(ImCustomerServiceManagerSaveReqVO createReqVO) {
        validateUsernameUnique(null, createReqVO.getUsername());
        ImCustomerServiceDO cs = BeanUtils.toBean(createReqVO, ImCustomerServiceDO.class);
        customerServiceMapper.insert(cs);
        LogRecordContext.putVariable("cs", cs);
        // 方案 C：客服资料变更，广播通知在线客户端重新拉取「联系客服」
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.CS.getType(), cs.getId()));
        return cs.getId();
    }

    @Override
    @LogRecord(type = IM_CUSTOMER_SERVICE_TYPE, subType = IM_CUSTOMER_SERVICE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}", success = IM_CUSTOMER_SERVICE_UPDATE_SUCCESS)
    public void updateCustomerService(ImCustomerServiceManagerUpdateReqVO updateReqVO) {
        if (customerServiceMapper.selectById(updateReqVO.getId()) == null) {
            throw ServiceExceptionUtil.exception(CUSTOMER_SERVICE_NOT_EXISTS);
        }
        validateUsernameUnique(updateReqVO.getId(), updateReqVO.getUsername());
        ImCustomerServiceDO update = BeanUtils.toBean(updateReqVO, ImCustomerServiceDO.class);
        customerServiceMapper.updateById(update);
        // 方案 C：客服资料变更，广播通知在线客户端重新拉取「联系客服」
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.CS.getType(), updateReqVO.getId()));
    }

    @Override
    @LogRecord(type = IM_CUSTOMER_SERVICE_TYPE, subType = IM_CUSTOMER_SERVICE_DELETE_SUB_TYPE, bizNo = "{{#id}}", success = IM_CUSTOMER_SERVICE_DELETE_SUCCESS)
    public void deleteCustomerService(Long id) {
        if (customerServiceMapper.selectById(id) == null) {
            throw ServiceExceptionUtil.exception(CUSTOMER_SERVICE_NOT_EXISTS);
        }
        customerServiceMapper.deleteById(id);
        // 方案 C：客服资料变更，广播通知在线客户端重新拉取「联系客服」
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.CS.getType(), id));
    }

    private void validateUsernameUnique(Long id, String username) {
        ImCustomerServiceDO exist = customerServiceMapper.selectByUsername(username);
        if (exist != null && (!exist.getId().equals(id))) {
            throw ServiceExceptionUtil.exception(CUSTOMER_SERVICE_USERNAME_DUPLICATE, username);
        }
    }

    @Override
    public AppImCustomerServiceCurrentRespVO getCurrentCustomerService() {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(imProperties.getRobot().getLoadBalanceWindowMinutes());
        ImCustomerServiceDO cs = customerServiceMapper.selectCurrent(windowStart);
        if (cs == null) {
            return null;
        }
        // 方案 C：补全独立参与方标识。客服以自身 id（csId）+ participantType 作为唯一地址，
        // 客户端据此打开 / 发送「客服会话」，不再依赖 im_users.id。
        AppImCustomerServiceCurrentRespVO respVO = BeanUtils.toBean(cs, AppImCustomerServiceCurrentRespVO.class);
        respVO.setCsId(cs.getId());
        respVO.setParticipantType(ImMessageParticipantTypeEnum.CS.getType());
        return respVO;
    }

    @Override
    public ImCustomerServiceDO pickOnlineCustomerService() {
        // 在线(status=1)，按近窗活跃会话数（最低并发）升序取首个
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(imProperties.getRobot().getLoadBalanceWindowMinutes());
        return customerServiceMapper.selectCurrent(windowStart);
    }

    @Override
    public boolean hasActiveCsConversation(Long userId) {
        if (userId == null) {
            return false;
        }
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(imProperties.getRobot().getTransferCooldownMinutes());
        return customerServiceMapper.existsRecentConversationWithCs(userId, windowStart);
    }

    @Override
    public Map<Long, ImCustomerServiceDO> getCustomerServiceMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return MapUtil.empty();
        }
        List<ImCustomerServiceDO> list = customerServiceMapper.selectByIds(ids);

        return list.stream()
                .filter(e -> e.getId() != null) // 防止数据库脏数据导致 NPE
                .collect(Collectors.toMap(
                        ImCustomerServiceDO::getId,
                        Function.identity(),
                        (oldVal, newVal) -> oldVal // 明确重复Key时的保留策略
                ));
    }

    @Override
    public ImCustomerServiceDO getCustomerServiceByAdminUserId(Long adminUserId) {
        return customerServiceMapper.selectByAdminUserId(adminUserId);
    }

    @Override
    public void setStatus(Long csId, Integer status) {
        if (status == null || status < 0 || status > 2) {
            throw ServiceExceptionUtil.exception(CUSTOMER_SERVICE_STATUS_INVALID);
        }
        ImCustomerServiceDO cs = customerServiceMapper.selectById(csId);
        if (cs == null) {
            throw ServiceExceptionUtil.exception(CUSTOMER_SERVICE_NOT_EXISTS);
        }
        // 幂等：状态未变则跳过写库与广播
        if (Objects.equals(cs.getStatus(), status)) {
            return;
        }
        customerServiceMapper.update(null, Wrappers.<ImCustomerServiceDO>lambdaUpdate()
                .eq(ImCustomerServiceDO::getId, csId)
                .set(ImCustomerServiceDO::getStatus, status));
        // 在线状态变化影响「联系客服」分配，广播通知在线客户端刷新
        imWebSocketService.broadcastToMembersAsync(
                ImPrivateMessageDTO.ofParticipantSync(ImMessageParticipantTypeEnum.CS.getType(), csId));
    }

}
