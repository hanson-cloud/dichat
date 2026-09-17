package com.diqin.cloud.server.controller;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.servlet.ServletUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.diqin.cloud.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;

/**
 * 默认 Controller，解决部分 module 未开启时的 404 提示。
 * 例如说，/bpm/** 路径，工作流
 *
 * @author 芋道源码
 */
@RestController
@Slf4j
public class DefaultController {

    @RequestMapping(value = { "/admin-api/product/**"// 商品中心
    }) // 营销中心
    public CommonResult<Boolean> mall404() {
        return CommonResult.error(NOT_IMPLEMENTED.code(),
                "[产品模块 dichat-module-mall - 已禁用]");
    }

    @RequestMapping(value = { "/admin-api/im/**"})
    public CommonResult<Boolean> im404() {
        return CommonResult.error(NOT_IMPLEMENTED.code(),
                "[IM 即时通讯 dichat-module-im - 已禁用]");
    }

    @RequestMapping(value = { "/admin-api/pay/**"})
    public CommonResult<Boolean> pay404() {
        return CommonResult.error(NOT_IMPLEMENTED.code(),
                "[支付模块 dichat-module-pay - 已弃用]");
    }

    @RequestMapping(value = { "/admin-api/ai/**"})
    public CommonResult<Boolean> ai404() {
        return CommonResult.error(NOT_IMPLEMENTED.code(),
                "[AI 大模型 dichat-module-ai - 已禁用]");
    }

    /**
     * 测试接口：打印 query、header、body
     */
    @RequestMapping(value = { "/test" })
    @PermitAll
    public CommonResult<Boolean> test(HttpServletRequest request) {
        // 打印查询参数
        log.info("Query: {}", ServletUtils.getParamMap(request));
        // 打印请求头
        log.info("Header: {}", ServletUtils.getHeaderMap(request));
        // 打印请求体
        log.info("Body: {}", ServletUtils.getBody(request));
        return CommonResult.success(true);
    }

}
