package com.diqin.cloud.module.im.controller.app.customer_service;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.controller.app.customer_service.vo.AppImCustomerServiceCurrentRespVO;
import com.diqin.cloud.module.im.service.customer_service.ImCustomerServiceManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 客服接入
 * <p>
 * 供移动端「联系客服」调用：返回当前可接入的在线客服（含关联用户编号 userId），
 * App 据此打开与该 userId 的 IM 私聊会话。无在线客服时返回 null，由前端兜底提示。
 * <p>
 * 路由说明：网关将 {@code /app-api/im/**} 转发到本服务（真实部署会重写为 {@code /im/**}），
 * 为兼容两种网关配置，本接口同时注册 {@code /im/app/customer-service/current}
 * 与 {@code /app-api/im/app/customer-service/current} 两个路径。
 *
 * @author 速构构
 */
@Tag(name = "用户 APP - 客服接入")
@RestController
@Validated
public class AppImCustomerServiceController {

    @Resource
    private ImCustomerServiceManagerService customerServiceManagerService;

    @GetMapping({"/im/app/customer-service/current" })
    @Operation(summary = "获取当前可接入的在线客服（用于「联系客服」打开 IM 私聊会话）")
    public CommonResult<AppImCustomerServiceCurrentRespVO> getCurrentCustomerService() {
        return success(customerServiceManagerService.getCurrentCustomerService());
    }

}
