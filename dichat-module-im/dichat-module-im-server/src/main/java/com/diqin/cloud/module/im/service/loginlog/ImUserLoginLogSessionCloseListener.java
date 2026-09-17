package com.diqin.cloud.module.im.service.loginlog;

import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.websocket.core.session.WebSocketSessionClosedEvent;
import com.diqin.cloud.module.system.api.logger.LoginLogApi;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogPageReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogRespDTO;
import com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket 会话关闭监听器
 *
 * <p>当用户 WebSocket 会话断开时，将对应 system 登录日志标记为「已离线」，
 * 解决 APP 直接关闭、网络断开等非主动退出场景下后台仍显示「在线」的问题。
 * 登录日志已统一收敛到 system 模块的 system_login_log，本类通过 {@link LoginLogApi} 操作。</p>
 *
 * @author hanson
 */
@Slf4j
@Component
public class ImUserLoginLogSessionCloseListener {

    @Resource
    private LoginLogApi loginLogApi;

    @EventListener(WebSocketSessionClosedEvent.class)
    public void onSessionClosed(WebSocketSessionClosedEvent event) {
        // 当前登录日志只记录 IM 用户（会员）的登录行为，管理员走 system_login_log
        if (!UserTypeEnum.MEMBER.getValue().equals(event.getUserType())) {
            return;
        }
        Long userId = event.getUserId();
        Integer terminal = event.getTerminal();
        if (userId == null || terminal == null) {
            log.warn("[onSessionClosed] 缺少用户或终端信息，无法更新登录日志，userId={}, terminal={}", userId, terminal);
            return;
        }
        // 查该用户该终端最新的一条在线记录
        LoginLogPageReqDTO reqVO = new LoginLogPageReqDTO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(1);
        reqVO.setUserType(UserTypeEnum.MEMBER.getValue());
        reqVO.setUserId(userId);
        reqVO.setTerminal(terminal);
        reqVO.setOnlineStatus(LoginLogStatusEnum.ONLINE.getStatus());

        PageResult<LoginLogRespDTO> pageResult = loginLogApi.getLoginLogPage(reqVO).getCheckedData();
        List<LoginLogRespDTO> list = pageResult == null ? null : pageResult.getList();
        if (list == null || list.isEmpty()) {
            return;
        }
        Long logId = list.getFirst().getId();
        loginLogApi.updateStatus(new ArrayList<>(List.of(logId)), LoginLogStatusEnum.OFFLINE.getStatus());
        log.info("[onSessionClosed] WebSocket 会话关闭，userId={}, terminal={}, 标记离线登录日志 {}", userId, terminal, logId);
    }

}
