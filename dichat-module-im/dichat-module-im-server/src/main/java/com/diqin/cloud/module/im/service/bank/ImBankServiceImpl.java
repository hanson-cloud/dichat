package com.diqin.cloud.module.im.service.bank;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankPageReqVO;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankSaveReqVO;
import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;
import com.diqin.cloud.module.im.dal.mysql.bank.ImBankMapper;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.diqin.cloud.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * IM 支持的银行 Service 实现
 *
 * @author dichat
 */
@Service
@Validated
@Slf4j
public class ImBankServiceImpl implements ImBankService {

    @Resource
    private ImBankMapper bankMapper;

    @Override
    public List<ImBankDO> getEnabledBanks() {
        return bankMapper.selectList(new LambdaQueryWrapperX<ImBankDO>()
                .eq(ImBankDO::getStatus, 1)
                .orderByAsc(ImBankDO::getSort)
                .orderByDesc(ImBankDO::getId));
    }

    @Override
    public PageResult<ImBankDO> getBankPage(ImBankPageReqVO reqVO) {
        return bankMapper.selectPage(reqVO, new LambdaQueryWrapperX<ImBankDO>()
                .likeIfPresent(ImBankDO::getBankCode, reqVO.getBankCode())
                .likeIfPresent(ImBankDO::getBankName, reqVO.getBankName())
                .eqIfPresent(ImBankDO::getStatus, reqVO.getStatus())
                .orderByAsc(ImBankDO::getSort)
                .orderByDesc(ImBankDO::getId));
    }

    @Override
    public Long createBank(ImBankSaveReqVO reqVO) {
        ImBankDO bank = BeanUtils.toBean(reqVO, ImBankDO.class);
        bankMapper.insert(bank);
        return bank.getId();
    }

    @Override
    public void updateBank(ImBankSaveReqVO reqVO) {
        ImBankDO bank = validateBankExists(reqVO.getId());
        BeanUtils.copyProperties(reqVO, bank);
        bankMapper.updateById(bank);
    }

    @Override
    public void deleteBank(Long id) {
        validateBankExists(id);
        bankMapper.deleteById(id);
    }

    private ImBankDO validateBankExists(Long id) {
        ImBankDO bank = bankMapper.selectById(id);
        if (bank == null) {
            throw exception(ErrorCodeConstants.BANK_NOT_EXISTS);
        }
        return bank;
    }

}
