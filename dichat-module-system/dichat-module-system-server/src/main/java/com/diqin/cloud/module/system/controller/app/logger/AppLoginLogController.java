package com.diqin.cloud.module.system.controller.app.logger;

import com.diqin.cloud.framework.common.enums.UserTypeEnum;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.security.core.util.SecurityFrameworkUtils;
import com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog.LoginLogRespVO;
import com.diqin.cloud.module.system.dal.dataobject.logger.LoginLogDO;
import com.diqin.cloud.module.system.enums.logger.LoginLogStatusEnum;
import com.diqin.cloud.module.system.service.logger.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 登录日志
 *
 * <p>登录日志（含在线设备）已统一收敛到 system 模块的 {@code system_login_log}，APP 端直接查询 system，
 * 不再经过 IM 模块代理。强制下线的「真实断开 WebSocket」仍由 IM 模块负责。</p>
 *
 * @author hanson
 */
@Tag(name = "用户 APP - 登录日志")
@RestController
@RequestMapping("/app/login-log")
@Validated
public class AppLoginLogController {

    @Resource
    private LoginLogService loginLogService;

    @GetMapping("/my")
    @Operation(summary = "查询我的登录设备列表", description = "返回当前登录用户的所有在线设备（按登录时间倒序），用于设备管理页")
    public CommonResult<List<LoginLogRespVO>> getMyLoginLogs() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        LoginLogPageReqVO reqVO = new LoginLogPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(200);
        reqVO.setUserId(userId);
        reqVO.setUserType(UserTypeEnum.MEMBER.getValue());
        reqVO.setOnlineStatus(LoginLogStatusEnum.ONLINE.getStatus());

        List<LoginLogDO> list = loginLogService.getLoginLogPage(reqVO).getList();
        if (list == null) {
            return success(List.of());
        }
        // 按登录时间倒序，最近登录的设备在前面
        list.sort(Comparator.comparing(LoginLogDO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return success(BeanUtils.toBean(list, LoginLogRespVO.class));
    }

}
