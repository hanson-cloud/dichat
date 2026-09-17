package com.diqin.cloud.module.pay.api.wallet;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.pay.api.wallet.dto.*;
import com.diqin.cloud.module.pay.controller.app.wallet.vo.recharge.AppPayWalletRechargeCreateReqVO;
import com.diqin.cloud.module.pay.controller.app.wallet.vo.transaction.AppPayWalletTransactionPageReqVO;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletDO;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletRechargeDO;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletTransactionDO;
import com.diqin.cloud.module.pay.enums.wallet.PayWalletBizTypeEnum;
import com.diqin.cloud.module.pay.service.wallet.PayWalletRechargeService;
import com.diqin.cloud.module.pay.service.wallet.PayWalletService;
import com.diqin.cloud.module.pay.service.wallet.PayWalletTransactionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;
import static com.diqin.cloud.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 钱包 API 实现类
 *
 * @author hanson
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class PayWalletApiImpl implements PayWalletApi {

    @Resource
    private PayWalletService payWalletService;
    @Resource
    private PayWalletRechargeService walletRechargeService;
    @Resource
    private PayWalletTransactionService payWalletTransactionService;

    @Override
    public CommonResult<Boolean> addWalletBalance(PayWalletAddBalanceReqDTO reqDTO) {
        // 创建或获取钱包
        PayWalletDO wallet = payWalletService.getOrCreateWallet(reqDTO.getUserId(), reqDTO.getUserType());
        Assert.notNull(wallet, "钱包({}/{})不存在", reqDTO.getUserId(), reqDTO.getUserType());

        // 增加余额
        PayWalletBizTypeEnum bizType = PayWalletBizTypeEnum.valueOf(reqDTO.getBizType());
        payWalletService.addWalletBalance(wallet.getId(), reqDTO.getBizId(), bizType, reqDTO.getPrice());
        return success(true);
    }

    @Override
    public CommonResult<PayWalletRespDTO> getOrCreateWallet(Long userId, Integer userType) {
        PayWalletDO wallet = payWalletService.getOrCreateWallet(userId, userType);
        return success(BeanUtils.toBean(wallet, PayWalletRespDTO.class));
    }

    @Override
    public CommonResult<PayWalletRechargeCreateRespDTO> createRecharge(PayWalletRechargeCreateReqDTO reqDTO) {
        AppPayWalletRechargeCreateReqVO reqVO = new AppPayWalletRechargeCreateReqVO()
                .setPayPrice(reqDTO.getPayPrice())
                .setPackageId(reqDTO.getPackageId());
        PayWalletRechargeDO recharge = walletRechargeService.createWalletRecharge(
                reqDTO.getUserId(), reqDTO.getUserType(), reqDTO.getUserIp(), reqVO);
        return success(new PayWalletRechargeCreateRespDTO()
                .setId(recharge.getId()).setPayOrderId(recharge.getPayOrderId()));
    }

    @Override
    public CommonResult<PageResult<PayWalletTransactionRespDTO>> getWalletTransactionPage(Long userId,
                                                                                          Integer userType,
                                                                                          PayWalletTransactionPageReqDTO reqDTO) {
        PageResult<PayWalletTransactionDO> pageResult = payWalletTransactionService.getWalletTransactionPage(
                userId, userType, BeanUtils.toBean(reqDTO, AppPayWalletTransactionPageReqVO.class));
        return success(BeanUtils.toBean(pageResult, PayWalletTransactionRespDTO.class));
    }

    @Override
    public CommonResult<PageResult<PayWalletRechargeRespDTO>> getRechargePage(PayWalletRechargePageReqDTO reqDTO) {
        PageResult<PayWalletRechargeDO> pageResult = walletRechargeService.getWalletRechargeAdminPage(
                reqDTO, reqDTO.getPayStatus(), reqDTO.getCreateTime());
        PageResult<PayWalletRechargeRespDTO> result = BeanUtils.toBean(pageResult, PayWalletRechargeRespDTO.class);
        if (!CollUtil.isEmpty(result.getList())) {
            Map<Long, Long> walletUserIdMap = payWalletService.getWalletUserIdMapByIds(
                    convertList(result.getList(), PayWalletRechargeRespDTO::getWalletId));
            result.getList().forEach(r -> r.setUserId(walletUserIdMap.get(r.getWalletId())));
        }
        return success(result);
    }

}
