package com.diqin.cloud.module.im.service.withdraw;

import com.diqin.cloud.framework.common.pojo.PageParam;
import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawManagerPageReqVO;
import com.diqin.cloud.module.im.controller.app.withdraw.vo.AppImWithdrawCreateReqVO;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawDO;

/**
 * IM 提现 Service 接口
 *
 * @author dichat
 */
public interface ImWithdrawService {

    /**
     * 发起余额提现（冻结钱包余额，状态：待审核）
     */
    ImWithdrawDO createWithdraw(Long userId, AppImWithdrawCreateReqVO reqVO);

    /**
     * 按编号查询
     */
    ImWithdrawDO getWithdraw(Long id);

    /**
     * 我的提现分页
     */
    PageResult<ImWithdrawDO> getWithdrawPageMy(Long userId, PageParam pageReqVO);

    /**
     * 管理后台分页
     */
    PageResult<ImWithdrawDO> getWithdrawPage(ImWithdrawManagerPageReqVO reqVO);

    /**
     * 审核提现（通过则完成，拒绝则退回冻结余额）
     */
    void auditWithdraw(Long adminId, Long id, boolean approve, String reason);

    /**
     * 撤销提现（仅待审核；退回已扣减的钱包余额）
     */
    void cancelWithdraw(Long adminId, Long id, String reason);

}
