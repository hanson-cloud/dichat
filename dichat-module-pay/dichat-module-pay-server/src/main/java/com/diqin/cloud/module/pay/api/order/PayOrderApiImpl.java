package com.diqin.cloud.module.pay.api.order;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.pay.api.order.dto.PayOrderCreateReqDTO;
import com.diqin.cloud.module.pay.api.order.dto.PayOrderRespDTO;
import com.diqin.cloud.module.pay.convert.order.PayOrderConvert;
import com.diqin.cloud.module.pay.dal.dataobject.order.PayOrderDO;
import com.diqin.cloud.module.pay.service.order.PayOrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class PayOrderApiImpl implements PayOrderApi {

    @Resource
    private PayOrderService payOrderService;

    @Override
    public CommonResult<Long> createOrder(PayOrderCreateReqDTO reqDTO) {
        return success(payOrderService.createOrder(reqDTO));
    }

    @Override
    public CommonResult<PayOrderRespDTO> getOrder(Long id) {
        PayOrderDO order = payOrderService.getOrder(id);
        return success(PayOrderConvert.INSTANCE.convert2(order));
    }

    @Override
    public CommonResult<Boolean> updatePayOrderPrice(Long id, Integer payPrice) {
        payOrderService.updatePayOrderPrice(id, payPrice);
        return success(true);
    }

}
