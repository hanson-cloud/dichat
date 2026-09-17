package com.diqin.cloud.module.pay.api.transfer;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.pay.api.transfer.dto.PayTransferCreateReqDTO;
import com.diqin.cloud.module.pay.api.transfer.dto.PayTransferCreateRespDTO;
import com.diqin.cloud.module.pay.api.transfer.dto.PayTransferRespDTO;
import com.diqin.cloud.module.pay.dal.dataobject.channel.PayChannelDO;
import com.diqin.cloud.module.pay.dal.dataobject.transfer.PayTransferDO;
import com.diqin.cloud.module.pay.framework.pay.core.client.impl.weixin.WxPayClientConfig;
import com.diqin.cloud.module.pay.service.channel.PayChannelService;
import com.diqin.cloud.module.pay.service.transfer.PayTransferService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 转账单 API 实现类
 *
 * @author hanson
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class PayTransferApiImpl implements PayTransferApi {

    @Resource
    private PayTransferService payTransferService;
    @Resource
    private PayChannelService payChannelService;

    @Override
    public CommonResult<PayTransferCreateRespDTO> createTransfer(PayTransferCreateReqDTO reqDTO) {
        return success(payTransferService.createTransfer(reqDTO));
    }

    @Override
    public CommonResult<PayTransferRespDTO> getTransfer(Long id) {
        PayTransferDO transfer = payTransferService.getTransfer(id);
        if (transfer == null) {
            return null;
        }
        PayChannelDO channel = payChannelService.getChannel(transfer.getChannelId());
        String mchId = null;
        if (channel != null && channel.getConfig() instanceof WxPayClientConfig) {
            mchId = ((WxPayClientConfig) channel.getConfig()).getMchId();
        }
        return success(BeanUtils.toBean(transfer, PayTransferRespDTO.class).setChannelMchId(mchId));
    }

}
