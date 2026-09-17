package com.diqin.cloud.module.system.service.logger;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.diqin.cloud.module.system.dal.dataobject.logger.LoginLogDO;

import jakarta.validation.Valid;

import java.util.List;

/**
 * 登录日志 Service 接口
 */
public interface LoginLogService {

    /**
     * 获得登录日志
     *
     * @param id 编号
     * @return 登录日志
     */
    LoginLogDO getLoginLog(Long id);

    /**
     * 获得登录日志分页
     *
     * @param pageReqVO 分页条件
     * @return 登录日志分页
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 创建登录日志
     *
     * @param reqDTO 日志信息
     */
    void createLoginLog(@Valid LoginLogCreateReqDTO reqDTO);

    /**
     * 强制下线：将指定登录记录标记为「已强制下线」并记录登出时间
     *
     * @param id 登录日志编号
     */
    void forceOffline(Long id);

    /**
     * 批量更新登录记录的会话状态（用于 WebSocket 断开、定时清理等场景）
     *
     * @param ids 登录日志编号集合
     * @param status 目标状态，参见 LoginLogStatusEnum
     */
    void updateStatus(List<Long> ids, Integer status);

    /**
     * 清理过期的在线登录日志（定时任务调用）
     *
     * @param startup 是否应用启动兜底：true 时把所有残留「在线」标记离线（重启后 WS 会话全失），false 时仅标记超时的在线记录
     */
    void cleanStaleOnlineLoginLogs(boolean startup);

}
