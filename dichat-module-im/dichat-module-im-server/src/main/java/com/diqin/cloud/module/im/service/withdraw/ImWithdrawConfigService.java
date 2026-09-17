package com.diqin.cloud.module.im.service.withdraw;

import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawConfigRespVO;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawConfigSetReqVO;

/**
 * IM 提现开关配置 Service 接口
 *
 * @author dichat
 */
public interface ImWithdrawConfigService {

    /**
     * 获取提现开关配置（全局单行；不存在时返回默认「开启」配置）
     */
    ImWithdrawConfigRespVO getConfig();

    /**
     * 设置提现开关配置
     *
     * @param operatorId 操作管理员编号
     * @param reqVO      开关状态与关闭消息
     */
    void setConfig(Long operatorId, ImWithdrawConfigSetReqVO reqVO);

}
