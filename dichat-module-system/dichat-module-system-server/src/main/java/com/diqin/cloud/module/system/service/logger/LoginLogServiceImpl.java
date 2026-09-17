package com.diqin.cloud.module.system.service.logger;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.tenant.core.aop.TenantIgnore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.diqin.cloud.module.system.dal.dataobject.logger.LoginLogDO;
import com.diqin.cloud.module.system.dal.mysql.logger.LoginLogMapper;
import com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录日志 Service 实现
 */
@Slf4j
@Service
@Validated
public class LoginLogServiceImpl implements LoginLogService, ApplicationRunner {

    @Resource
    private LoginLogMapper loginLogMapper;

    @Override
    public LoginLogDO getLoginLog(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createLoginLog(LoginLogCreateReqDTO reqDTO) {
        LoginLogDO loginLog = BeanUtils.toBean(reqDTO, LoginLogDO.class);
        // 未显式指定状态时，默认在线，保证历史（仅记录登录事件）数据也能被「我的设备」识别
        if (loginLog.getStatus() == null) {
            loginLog.setStatus(LoginLogStatusEnum.ONLINE.getStatus());
        }
        loginLogMapper.insert(loginLog);
    }

    @Override
    public void forceOffline(Long id) {
        LoginLogDO update = new LoginLogDO();
        update.setId(id);
        update.setStatus(LoginLogStatusEnum.FORCE_OFFLINE.getStatus());
        update.setLogoutTime(LocalDateTime.now());
        loginLogMapper.updateById(update);
    }

    @Override
    public void updateStatus(List<Long> ids, Integer status) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        LoginLogDO update = new LoginLogDO();
        update.setStatus(status);
        update.setLogoutTime(LocalDateTime.now());
        loginLogMapper.update(update, new LambdaUpdateWrapper<LoginLogDO>()
                .in(LoginLogDO::getId, ids));
    }

    /** 在线登录记录超时阈值（小时）：超过此时长无新登录记录的在线设备，判定为离线（兜底修正） */
    private static final int ONLINE_TIMEOUT_HOURS = 24;

    @Override
    @TenantIgnore // 应用启动兜底：重启后 WebSocket 会话全部丢失，残留「在线」记录需修正；定时线程无租户上下文
    public void run(ApplicationArguments args) {
        cleanStaleOnlineLoginLogs(true);
    }

    @Scheduled(initialDelay = 60_000, fixedDelay = 300_000)
    @TenantIgnore // 定时线程无租户上下文，读取全部租户登录日志做兜底修正
    public void scheduledCleanStaleOnlineLoginLogs() {
        cleanStaleOnlineLoginLogs(false);
    }

    @Override
    public void cleanStaleOnlineLoginLogs(boolean startup) {
        LoginLogPageReqVO reqVO = new LoginLogPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(200);
        reqVO.setUserType(UserTypeEnum.MEMBER.getValue());
        reqVO.setOnlineStatus(LoginLogStatusEnum.ONLINE.getStatus());
        List<LoginLogDO> onlineLogs = loginLogMapper.selectPage(reqVO).getList();
        if (CollUtil.isEmpty(onlineLogs)) {
            return;
        }
        LocalDateTime expireTime = LocalDateTime.now().minusHours(ONLINE_TIMEOUT_HOURS);
        List<Long> toOfflineIds = onlineLogs.stream()
                .filter(log -> startup
                        || (log.getCreateTime() != null && log.getCreateTime().isBefore(expireTime)))
                .map(LoginLogDO::getId)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(toOfflineIds)) {
            updateStatus(toOfflineIds, LoginLogStatusEnum.OFFLINE.getStatus());
            log.info("[cleanStaleOnlineLoginLogs] startup={} 扫描 {} 条在线登录日志，标记 {} 条为离线",
                    startup, onlineLogs.size(), toOfflineIds.size());
        }
    }

}
