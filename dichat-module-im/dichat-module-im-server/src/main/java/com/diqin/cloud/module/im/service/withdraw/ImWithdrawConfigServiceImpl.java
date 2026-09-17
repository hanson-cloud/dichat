package com.diqin.cloud.module.im.service.withdraw;

import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawConfigRespVO;
import com.diqin.cloud.module.im.controller.admin.withdraw.vo.ImWithdrawConfigSetReqVO;
import com.diqin.cloud.module.im.dal.dataobject.withdraw.ImWithdrawConfigDO;
import com.diqin.cloud.module.im.dal.mysql.withdraw.ImWithdrawConfigMapper;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * IM 提现开关配置 Service 实现
 * <p>
 * 全局单行配置（固定 {@code id = 1}）：默认开启；关闭时必须提供 {@code closeMessage}。
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
public class ImWithdrawConfigServiceImpl implements ImWithdrawConfigService {

    /**
     * 全局单行配置固定主键
     */
    private static final Long CONFIG_ID = 1L;

    @Resource
    private ImWithdrawConfigMapper configMapper;

    @Override
    public ImWithdrawConfigRespVO getConfig() {
        ImWithdrawConfigDO config = configMapper.selectById(CONFIG_ID);
        if (config == null) {
            // 默认开启：未配置即视为开放提现
            return new ImWithdrawConfigRespVO().setEnabled(Boolean.TRUE);
        }
        return BeanUtils.toBean(config, ImWithdrawConfigRespVO.class);
    }

    @Override
    public void setConfig(Long operatorId, ImWithdrawConfigSetReqVO reqVO) {
        if (reqVO.getEnabled() == null) {
            throw exception(ErrorCodeConstants.WITHDRAW_CONFIG_ENABLED_NULL);
        }
        // 关闭提现必须填写通知用户的关闭消息
        if (!reqVO.getEnabled() && StrUtil.isBlank(reqVO.getCloseMessage())) {
            throw exception(ErrorCodeConstants.WITHDRAW_CONFIG_MESSAGE_REQUIRED);
        }

        ImWithdrawConfigDO config = configMapper.selectById(CONFIG_ID);
        boolean isNew = (config == null);
        if (isNew) {
            config = new ImWithdrawConfigDO().setId(CONFIG_ID);
        }
        config.setEnabled(reqVO.getEnabled());
        // 开启时清空关闭消息；关闭时保存通知文案
        config.setCloseMessage(reqVO.getEnabled() ? null : reqVO.getCloseMessage());
        config.setOperatorId(operatorId);

        if (isNew) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
    }

}
