package com.diqin.cloud.module.system.api.logger;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogPageReqDTO;
import com.diqin.cloud.module.system.api.logger.dto.LoginLogRespDTO;
import com.diqin.cloud.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.diqin.cloud.module.system.service.logger.LoginLogService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class LoginLogApiImpl implements LoginLogApi {

    @Resource
    private LoginLogService loginLogService;

    @Override
    public CommonResult<Boolean> createLoginLog(LoginLogCreateReqDTO reqDTO) {
        loginLogService.createLoginLog(reqDTO);
        return success(true);
    }

    @Override
    public CommonResult<LoginLogRespDTO> getLoginLog(Long id) {
        return success(BeanUtils.toBean(loginLogService.getLoginLog(id), LoginLogRespDTO.class));
    }

    @Override
    public CommonResult<PageResult<LoginLogRespDTO>> getLoginLogPage(LoginLogPageReqDTO pageReqVO) {
        LoginLogPageReqVO bean = BeanUtils.toBean(pageReqVO, LoginLogPageReqVO.class);
        PageResult<?> pageResult = loginLogService.getLoginLogPage(bean);
        return success(BeanUtils.toBean(pageResult, LoginLogRespDTO.class));
    }

    @Override
    public CommonResult<Boolean> forceOffline(Long id) {
        loginLogService.forceOffline(id);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> updateStatus(List<Long> ids, Integer status) {
        loginLogService.updateStatus(ids, status);
        return success(true);
    }

}
