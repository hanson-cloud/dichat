package com.diqin.cloud.module.pay.dal.mysql.wallet;


import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.framework.mybatis.core.mapper.BaseMapperX;
import com.diqin.cloud.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.diqin.cloud.module.pay.controller.admin.wallet.vo.wallet.PayWalletPageReqVO;
import com.diqin.cloud.module.pay.dal.dataobject.wallet.PayWalletDO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayWalletMapper extends BaseMapperX<PayWalletDO> {

    default PayWalletDO selectByUserIdAndType(Long userId, Integer userType) {
        return selectOne(PayWalletDO::getUserId, userId,
                PayWalletDO::getUserType, userType);
    }

    default PageResult<PayWalletDO> selectPage(PayWalletPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PayWalletDO>()
                .eqIfPresent(PayWalletDO::getUserId, reqVO.getUserId())
                .eqIfPresent(PayWalletDO::getUserType, reqVO.getUserType())
                .betweenIfPresent(PayWalletDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PayWalletDO::getId));
    }

    /**
     * 当消费退款时候， 更新钱包
     *
     * @param id    钱包 id
     * @param price 消费金额
     */
    default void updateWhenConsumptionRefund(Long id, Integer price) {
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
                .setSql(" balance = balance + " + price
                        + ", total_expense = total_expense - " + price)
                .eq(PayWalletDO::getId, id);
        update(null, lambdaUpdateWrapper);
    }

    /**
     * 当消费时候， 更新钱包
     *
     * @param price 消费金额
     * @param id 钱包 id
     */
    default int updateWhenConsumption(Long id, Integer price){
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
                .setSql(" balance = balance - " + price
                        + ", total_expense = total_expense + " + price)
                .eq(PayWalletDO::getId, id)
                .ge(PayWalletDO::getBalance, price); // cas 逻辑
        return update(null, lambdaUpdateWrapper);
    }

    /**
     * 当充值的时候，更新钱包
     *
     * @param id    钱包 id
     * @param price 钱包金额
     */
    default void updateWhenRecharge(Long id, Integer price){
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
                .setSql(" balance = balance + " + price
                        + ", total_recharge = total_recharge + " + price)
                .eq(PayWalletDO::getId, id);
        update(null, lambdaUpdateWrapper);
    }

    /**
     * 增加余额的时候，更新钱包
     *
     * @param id 钱包 id
     * @param price 钱包金额（正数入账 / 负数扣款）
     * @return 影响行数（扣款时若余额不足则为 0，用于 CAS 原子校验）
     */
    default int updateWhenAdd(Long id, Integer price) {
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
             .setSql(" balance = balance + " + price)
             .eq(PayWalletDO::getId, id)
             .ge(PayWalletDO::getBalance, -price); // cas 逻辑：扣款时确保余额 >= |price|，防止余额变负
        return update(null, lambdaUpdateWrapper);
    }

    /**
     * 冻结钱包部分余额
     *
     * @param id 钱包 id
     * @param price 冻结金额
     */
    default int freezePrice(Long id, Integer price){
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
                .setSql(" balance = balance - " + price
                        + ", freeze_price = freeze_price + " + price)
                .eq(PayWalletDO::getId, id)
                .ge(PayWalletDO::getBalance, price); // cas 逻辑
        return update(null, lambdaUpdateWrapper);
    }

    /**
     * 解冻钱包余额
     *
     * @param id 钱包 id
     * @param price 解冻金额
     */
    default int unFreezePrice(Long id, Integer price){
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
                .setSql(" balance = balance + " + price
                        + ", freeze_price = freeze_price - " + price)
                .eq(PayWalletDO::getId, id)
                .ge(PayWalletDO::getFreezePrice, price); // cas 逻辑
        return update(null, lambdaUpdateWrapper);
    }

    /**
     * 当充值退款时, 更新钱包
     *
     * @param id 钱包 id
     * @param price 退款金额
     */
    default  int updateWhenRechargeRefund(Long id, Integer price){
        LambdaUpdateWrapper<PayWalletDO> lambdaUpdateWrapper = new LambdaUpdateWrapper<PayWalletDO>()
                .setSql(" freeze_price = freeze_price - " + price
                        + ", total_recharge = total_recharge - " + price)
                .eq(PayWalletDO::getId, id)
                .ge(PayWalletDO::getFreezePrice, price)
                .ge(PayWalletDO::getTotalRecharge, price);// cas 逻辑
        return update(null, lambdaUpdateWrapper);
    }

}




