package com.diqin.cloud.module.im.service.bank;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankPageReqVO;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankSaveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;

import java.util.List;

/**
 * IM 支持的银行 Service
 *
 * @author dichat
 */
public interface ImBankService {

    /**
     * 获得所有启用的银行（APP 端绑卡选择用）
     */
    List<ImBankDO> getEnabledBanks();

    /**
     * 获得银行分页（管理后台）
     */
    PageResult<ImBankDO> getBankPage(ImBankPageReqVO reqVO);

    /**
     * 创建银行
     */
    Long createBank(ImBankSaveReqVO reqVO);

    /**
     * 更新银行
     */
    void updateBank(ImBankSaveReqVO reqVO);

    /**
     * 删除银行
     */
    void deleteBank(Long id);
    }
