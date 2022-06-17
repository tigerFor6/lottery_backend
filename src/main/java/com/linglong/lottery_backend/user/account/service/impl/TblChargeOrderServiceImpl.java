package com.linglong.lottery_backend.user.account.service.impl;

import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.service.TblChargeOrderService;
import com.linglong.lottery_backend.user.cashier.entity.TblChargeOrder;
import com.linglong.lottery_backend.user.cashier.entity.TblPaymentConfiguration;
import com.linglong.lottery_backend.user.cashier.repository.TblChargeOrderRepository;
import com.linglong.lottery_backend.utils.ArithUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/26
 */
@Service
public class TblChargeOrderServiceImpl implements TblChargeOrderService {

    @Autowired
    private TblChargeOrderRepository tblChargeOrderRepository;

    public static void main(String[] args) {
        BigDecimal discountMinAmount = new BigDecimal(0);
        BigDecimal discountMaxAmount = new BigDecimal(100);
        Random random = new Random();
        int discountAmount = random.nextInt(discountMaxAmount.intValue())%(discountMaxAmount.intValue()-discountMinAmount.intValue()+1) + discountMinAmount.intValue();
    }
    @Override
    public BigDecimal insertChargeRecord(User user, Long orderNo, String code, String recordStatus, BigDecimal amount, TblPaymentConfiguration configuration) {

        TblChargeOrder chargeOrder = new TblChargeOrder();
        chargeOrder.setUserId(user.getUserId().toString());
        chargeOrder.setRecordNo(orderNo);
        chargeOrder.setCode(code);
        chargeOrder.setType("1");//订单方式为充值
        chargeOrder.setRecordStatus(recordStatus);
        chargeOrder.setRechargeAmount(amount);
        chargeOrder.setChannelNo(user.getChannelNo());
        //随机立减状态
        Integer discountStatus = configuration.getDiscountStatus();
        BigDecimal realityAmount = amount;
        if (discountStatus == 1){
            BigDecimal discountMinAmount = configuration.getDiscountMinAmount();
            BigDecimal discountMaxAmount = configuration.getDiscountMaxAmount();
            Random random = new Random();
            int discountAmount = random.nextInt(discountMaxAmount.intValue())%(discountMaxAmount.intValue()-discountMinAmount.intValue()+1) + discountMinAmount.intValue();
            if (discountAmount > discountMaxAmount.intValue()){
                discountAmount = discountMaxAmount.intValue();
            }
            realityAmount = ArithUtil.sub(amount, new BigDecimal(discountAmount));
            chargeOrder.setDiscountAmount(new BigDecimal(discountAmount));
            chargeOrder.setRealityAmount(realityAmount);
        }
        //插入一条充值订单记录
        tblChargeOrderRepository.save(chargeOrder);
        return realityAmount;
    }

    @Override
    public TblChargeOrder findByRecordNo(Long recordNo) {
        return tblChargeOrderRepository.findByRecordNo(recordNo);
    }

    @Override
    public TblChargeOrder findByThirdPartyRecordNo(String thirdPartyRecordNo) {
        return tblChargeOrderRepository.findByThirdPartyRecordNo(thirdPartyRecordNo);
    }

    @Override
    public void updateChargeRecord(TblChargeOrder record) {
        tblChargeOrderRepository.save(record);
    }
}
