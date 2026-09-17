package com.diqin.cloud.module.im.service.payment;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerPageReqVO;
import com.diqin.cloud.module.im.controller.admin.payment.vo.ImPaymentQrManagerRespVO;
import com.diqin.cloud.module.im.dal.dataobject.payment.ImPaymentQrDO;

public interface ImPaymentQrService {

    /**
     * 创建收款码（可选金额/备注）
     *
     * @return 收款码记录
     */
    ImPaymentQrDO createReceiveQr(Long userId, Long amount, String remark);

    /**
     * 根据 code 获取有效收款码（未过期且状态为有效）
     *
     * @return 无效或已过期时返回 null
     */
    ImPaymentQrDO getValidByCode(String code);
    /**
     * 获取收款码分页（管理后台）
     */
    PageResult<ImPaymentQrManagerRespVO> getManagerPage(ImPaymentQrManagerPageReqVO pageReqVO);

    /**
     * 管理后台删除收款码
     */
    void deleteManager(Long id);
}
