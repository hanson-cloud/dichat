package com.diqin.cloud.module.im.service.payment;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.payment.ImPaymentQrDO;
import com.diqin.cloud.module.im.dal.mysql.payment.ImPaymentQrMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Validated
public class ImPaymentQrServiceImpl implements ImPaymentQrService {

    /** 收款码默认有效期（分钟） */
    private static final int QR_EXPIRE_MINUTES = 5;

    @Resource
    private ImPaymentQrMapper paymentQrMapper;

    @Override
    public ImPaymentQrDO createReceiveQr(Long userId, Long amount, String remark) {
        ImPaymentQrDO entity = ImPaymentQrDO.builder()
                .userId(userId)
                .code(genCode())
                .type(1)
                .amount(amount)
                .remark(remark)
                .status(0)
                .expireTime(LocalDateTime.now().plusMinutes(QR_EXPIRE_MINUTES))
                .build();
        paymentQrMapper.insert(entity);
        return entity;
    }

    @Override
    public ImPaymentQrDO getValidByCode(String code) {
        ImPaymentQrDO qr = paymentQrMapper.selectByCode(code);
        if (qr == null || qr.getStatus() == null || qr.getStatus() != 0) {
            return null;
        }
        if (qr.getExpireTime() != null && qr.getExpireTime().isBefore(LocalDateTime.now())) {
            return null;
        }
        return qr;
    }

    @Override
    public PageResult<ImPaymentQrManagerRespVO> getManagerPage(ImPaymentQrManagerPageReqVO pageReqVO) {
        LambdaQueryWrapperX<ImPaymentQrDO> query = new LambdaQueryWrapperX<ImPaymentQrDO>()
                .eqIfPresent(ImPaymentQrDO::getUserId, pageReqVO.getUserId())
                .eqIfPresent(ImPaymentQrDO::getStatus, pageReqVO.getStatus())
                .likeIfPresent(ImPaymentQrDO::getCode, pageReqVO.getCode())
                .orderByDesc(ImPaymentQrDO::getId);
        PageResult<ImPaymentQrDO> page = paymentQrMapper.selectPage(pageReqVO, query);
        return BeanUtils.toBean(page, ImPaymentQrManagerRespVO.class);
    }

    @Override
    public void deleteManager(Long id) {
        paymentQrMapper.deleteById(id);
    }

    private String genCode() {
        return "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
