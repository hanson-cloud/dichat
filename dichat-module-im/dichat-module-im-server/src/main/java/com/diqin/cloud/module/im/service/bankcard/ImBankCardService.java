package com.diqin.cloud.module.im.service.bankcard;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.bankcard.vo.ImBankCardManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bankcard.ImBankCardDO;
import com.diqin.cloud.module.im.dto.bankcard.ImBankCardBindReqDTO;

import java.util.List;

/**
 * IM 银行卡 Service 接口
 *
 * @author dichat
 */
public interface ImBankCardService {

    /**
     * 绑定银行卡（待审核）
     */
    ImBankCardDO bind(Long userId, ImBankCardBindReqDTO reqDTO);

    /**
     * 我的银行卡列表
     */
    List<ImBankCardDO> getMyBankCards(Long userId);

    /**
     * 设为默认卡
     */
    void setDefault(Long userId, Long id);

    /**
     * 解绑银行卡
     */
    void deleteBankCard(Long userId, Long id);

    // ==================== 管理后台 ====================

    /**
     * 按编号查询
     */
    ImBankCardDO getBankCard(Long id);

    /**
     * 银行卡分页
     */
    PageResult<ImBankCardDO> getBankCardPage(ImBankCardManagerPageReqVO reqVO);

    /**
     * 审核（通过 / 拒绝）
     */
    void auditBankCard(Long adminId, Long id, boolean approve, String reason);

    /**
     * 管理后台解绑
     */
    void deleteBankCardAdmin(Long id);

}
