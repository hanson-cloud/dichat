package com.diqin.cloud.module.im.service.transfer;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.transfer.vo.ImTransferManagerPageReqVO;
import com.diqin.cloud.module.im.dal.dataobject.transfer.ImTransferDO;
import com.diqin.cloud.module.im.dto.transfer.ImTransferReqDTO;

/**
 * IM 转账 Service 接口
 *
 * @author dichat
 */
public interface ImTransferService {

    /**
     * 发起 P2P 转账（委托钱包模块变动余额）
     */
    ImTransferDO createTransfer(Long userId, ImTransferReqDTO reqDTO);

    /**
     * 按编号查询
     */
    ImTransferDO getTransfer(Long id);

    /**
     * 我的转账分页（发送或接收）
     */
    PageResult<ImTransferDO> getTransferPageMy(Long userId, PageParam pageReqVO);

    /**
     * 管理后台分页
     */
    PageResult<ImTransferDO> getTransferPage(ImTransferManagerPageReqVO reqVO);

}
