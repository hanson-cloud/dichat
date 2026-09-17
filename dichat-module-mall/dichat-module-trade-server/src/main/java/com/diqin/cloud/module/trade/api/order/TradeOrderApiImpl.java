package com.diqin.cloud.module.trade.api.order;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.trade.api.order.dto.TradeOrderRespDTO;
import com.diqin.cloud.module.trade.convert.order.TradeOrderConvert;
import com.diqin.cloud.module.trade.service.order.TradeOrderQueryService;
import com.diqin.cloud.module.trade.service.order.TradeOrderUpdateService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 订单 API 接口实现类
 *
 * @author hanson
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class TradeOrderApiImpl implements TradeOrderApi {

    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;
    @Resource
    private TradeOrderQueryService tradeOrderQueryService;

    @Override
    public CommonResult<List<TradeOrderRespDTO>> getOrderList(Collection<Long> ids) {
        return success(TradeOrderConvert.INSTANCE.convertList04(tradeOrderQueryService.getOrderList(ids)));
    }

    @Override
    public CommonResult<TradeOrderRespDTO> getOrder(Long id) {
        return success(TradeOrderConvert.INSTANCE.convert(tradeOrderQueryService.getOrder(id)));
    }

    @Override
    public CommonResult<Boolean> cancelPaidOrder(Long userId, Long orderId, Integer cancelType) {
        tradeOrderUpdateService.cancelPaidOrder(userId, orderId, cancelType);
        return success(true);
    }

}
