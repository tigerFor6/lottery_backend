package com.linglong.lottery_backend.user.account.service.impl;

import com.linglong.lottery_backend.activity.entity.TblUserCoupon;
import com.linglong.lottery_backend.activity.repository.TblUserCouponRepository;
import com.linglong.lottery_backend.activity.service.TblUserCouponService;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.listener.bean.CommonRecord;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.lottery_backend.order.model.StatusCode;
import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TblUserBalanceRepository;
import com.linglong.lottery_backend.user.account.repository.TransactionRecordRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.utils.ArithUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class TblUserBalanceServiceImpl implements TblUserBalanceService {
    private static final Logger logger = LoggerFactory.getLogger(TblUserBalanceServiceImpl.class);

    @Autowired
    private TblUserBalanceRepository repository;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TblUserCouponRepository tblUserCouponRepository;

    @Autowired
    private TblUserCouponService tblUserCouponService;

    //???????????????????????????????????????????????????
    private static final BigDecimal multiple = new BigDecimal(100);


    /**
     * @description: ???????????????????????????????????????????????????????????????????????????
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-12
     **/

    @Transactional
    @Override
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public Result recharge(String userId, BigDecimal amount) {
        if (amount.doubleValue() <= 0){
            logger.error("???????????????0");
            return ResultGenerator.genFailResult("?????????????????????0");
        }
        BigDecimal amountDouble = ArithUtil.mul(new BigDecimal(100), amount);
        TblUserBalance userBalance = repository.findByUserId(userId);
        User user = userRepository.findByUserId(userId);

        //??????????????????
        TransactionRecord transactionRecord = new TransactionRecord()
                .setUserId(userId)
                .setRecordNo(idWorker.nextId())
                .setPrice(amountDouble)
                .setType(Code.Trade.RECHARGE)
                .setRecordStatus(Code.Trade.RECHARGE_SUCCESS)
                .setChannelNo(user.getChannelNo());

        if (null == userBalance) {
            logger.info("?????????????????????????????????????????????id???" + userId + "???????????????????????????");
            userBalance = new TblUserBalance()
                    .setUserId(userId)
                    .setRechargeAmount(amountDouble)
                    .setTotalBalance(amountDouble);

            transactionRecord.setBalance(amountDouble);
            repository.saveAndFlush(userBalance);
        } else {
            logger.info("????????????id???" + userId + "???????????????" + amount + "???");
            BigDecimal rechargeAmount = userBalance.getRechargeAmount();
            BigDecimal rechargeAfter = ArithUtil.add(amountDouble, rechargeAmount);

            BigDecimal totalBalance = userBalance.getTotalBalance();
            BigDecimal totalAfter = ArithUtil.add(amountDouble, totalBalance);

            transactionRecord.setBalance(totalAfter);
            repository.rechargeUpdate(rechargeAfter, totalAfter, userId);

        }

        logger.info("?????????????????????????????????");
        transactionRecordService.record(transactionRecord, Code.Trade.RECHARGE);
        return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
    }

    //????????????
    @Override
    @Transactional
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void refund(String userId, BigDecimal amount, String voucher_no, String orderId) {
        User user = userRepository.findByUserId(userId);
        BigDecimal amountDouble = ArithUtil.mul(new BigDecimal(100), amount);
        TblUserBalance userBalance = repository.findByUserId(userId);
        BigDecimal rechargeAmount = userBalance.getRechargeAmount();
        BigDecimal rewardAmount = userBalance.getRewardAmount();
        BigDecimal totalBalance = userBalance.getTotalBalance();

        //???????????????????????????
        TransactionRecord transactionRecord = transactionRecordRepository.findFirstByVoucherNo(Long.valueOf(voucher_no));
        //????????????????????????
        TransactionRecord record = transactionRecordRepository.findByOrderId(Long.valueOf(orderId));
        BigDecimal totalAfter = BigDecimal.ZERO;
        if (record != null){
            logger.info("????????????:"+"??????ID:"+userId+"????????????"+orderId+"??????:"+amount);
            BigDecimal rewardDeduct = record.getRewardDeduct();
            BigDecimal rechargeDeduct = record.getRechargeDeduct();
            if (rewardDeduct != null && rewardDeduct.compareTo(new BigDecimal(0)) == 1){
                if (rechargeDeduct.compareTo(amountDouble) == -1) {
                    //???????????????????????????????????????,20-80  30
                    BigDecimal recharge = ArithUtil.sub(amountDouble, rechargeDeduct);
                    BigDecimal rechargeAfter = ArithUtil.add(rechargeAmount,rechargeDeduct);//+20
                    BigDecimal rewardAfter = ArithUtil.add(rewardAmount,recharge);//+10
                    totalAfter = ArithUtil.add(totalBalance,amountDouble);//+30
                    repository.deductionUpdate(rechargeAfter, rewardAfter, totalAfter, userId, userBalance.getVersion());
                    record.setRechargeDeduct(new BigDecimal(0.0));
                    record.setRewardDeduct(ArithUtil.sub(rewardDeduct,recharge));//80-10
                    transactionRecordRepository.saveAndFlush(record);
                }else{
                    //??????????????????????????????,80-20  30
                    BigDecimal rechargeAfter = ArithUtil.add(rechargeAmount,amountDouble);//+30
                    totalAfter = ArithUtil.add(totalBalance,amountDouble);//+30
                    repository.rechargeUpdate(rechargeAfter,totalAfter,userId);
                    if (rechargeDeduct.compareTo(amountDouble) == -1){
                        record.setRechargeDeduct(new BigDecimal(0.0));
                    }else{
                        record.setRechargeDeduct(ArithUtil.sub(rechargeDeduct,amountDouble));//100-30
                    }
                    transactionRecordRepository.saveAndFlush(record);
                }
            }else{
                //??????????????? 100-0  30
                BigDecimal rechargeAfter = ArithUtil.add(rechargeAmount, amountDouble);
                totalAfter = ArithUtil.add(totalBalance, amountDouble);
                repository.rechargeUpdate(rechargeAfter, totalAfter, userId);
                if (rechargeDeduct.compareTo(amountDouble) == -1){
                    record.setRechargeDeduct(new BigDecimal(0.0));
                }else{
                    record.setRechargeDeduct(ArithUtil.sub(rechargeDeduct,amountDouble));//100-30
                }
                transactionRecordRepository.saveAndFlush(record);
            }
        }else{
            totalAfter = ArithUtil.add(totalBalance, amountDouble);
        }

        if (null == transactionRecord) {

            //??????????????????
            transactionRecord = new TransactionRecord()
                    .setUserId(userId)
                    .setVoucherNo(Long.valueOf(voucher_no))
                    .setPrice(amountDouble)
                    .setType(Code.Trade.REFUND)
                    .setRecordStatus(Code.Trade.REFUNDING_SUCCESS)
                    .setRecordCreateTime(new Date())
                    .setRecordFinishTime(new Date())
                    .setChannelNo(user.getChannelNo());

            logger.info("????????????id???" + userId + "???????????????" + amount + "???");
            transactionRecord.setBalance(totalAfter);
            logger.info("?????????????????????????????????");

            transactionRecordRepository.saveAndFlush(transactionRecord);
        }

    }

    /**
     * @description: ???????????????????????????????????????????????????????????????????????????
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-23
     **/
    @Transactional
    @Override
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public Result reward(String userId, Long voucherNo, BigDecimal amount) {
        if (amount.doubleValue() <= 0){
            logger.error("???????????????0");
            return ResultGenerator.genFailResult("?????????????????????0");
        }
        User user = userRepository.findByUserId(userId);
        TblUserBalance userBalance = repository.findByUserId(userId);
        if (null == userBalance) {
            logger.error("???????????????????????? userID {}", userId);
            return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
        }
        TransactionRecord transactionRecord = transactionRecordService.findByVoucherNo(voucherNo);
        if (null != transactionRecord) {
            return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
        }

        transactionRecord = new TransactionRecord()
                .setUserId(userId)
                .setRecordNo(idWorker.nextId())
                .setPrice(amount)
                .setVoucherNo(voucherNo)
                .setType(Code.Trade.RETURN_AWARD)
                .setRecordStatus(Code.Trade.RETURNAWARD_SUCCESS)
                .setChannelNo(user.getChannelNo());
        logger.info("??????????????????:"+"userId:"+userId+"voucherNo:"+voucherNo+"amount:"+amount);
        transactionRecordService.record(transactionRecord, Code.Trade.RETURN_AWARD);

        BigDecimal rewardAmount = userBalance.getRewardAmount();
        rewardAmount = ArithUtil.add(amount, rewardAmount);
        BigDecimal totalBalance = userBalance.getTotalBalance();
        BigDecimal totalAfter = ArithUtil.add(amount, totalBalance);
        transactionRecord.setBalance(totalAfter);
        repository.rewardUpdate(rewardAmount, totalAfter, userId);

        return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
    }

    @Override
    public TblUserBalance findByUserId(String userId) {
        TblUserBalance tblUserBalance = repository.findByUserId(userId);
        if (null != tblUserBalance) {
            BigDecimal totalBalanceFront = ArithUtil.div(tblUserBalance.getTotalBalance(), multiple);
            BigDecimal rewardAmountFront = ArithUtil.div(tblUserBalance.getRewardAmount(), multiple);
            BigDecimal rechargeAmountFront = ArithUtil.div(tblUserBalance.getRechargeAmount(), multiple);
            tblUserBalance.setTotalBalance(totalBalanceFront);
            tblUserBalance.setRewardAmount(rewardAmountFront);
            tblUserBalance.setRechargeAmount(rechargeAmountFront);
        }
        return tblUserBalance;
    }

    /**
     * @description: ??????????????????, ????????????????????????????????????????????????????????????
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-12
     **/
    @Transactional
    @Override
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public Result deduction(String userId, BigDecimal amount, Long order_id, Long userCouponId, Integer gameType) {
        User user = userRepository.findByUserId(userId);
        BigDecimal amountDouble = ArithUtil.mul(multiple, amount);
        TblUserBalance userBalance = repository.findByUserId(userId);
        TransactionRecord record = transactionRecordRepository.findByOrderIdAndRecordStatus(order_id,Code.Trade.THIRD_BET_SUCCESS);
        if (record != null){
            logger.info("?????????????????????");
            return ResultGenerator.genExceptionResult(StatusCode.ERROR.getCode(), "????????????");
        }
        BigDecimal couponAmount;
        if (userCouponId == 0){
            couponAmount = BigDecimal.ZERO;
        }else{
            //??????????????????????????????
            int code = tblUserCouponService.useCoupon(Long.valueOf(userId),userCouponId,order_id,amountDouble,gameType);
            if (code != 100000){
                logger.info("???????????????????????????" + code);
                return ResultGenerator.genExceptionResult(StatusCode.ERROR.getCode(), "??????????????????");
            }
            TblUserCoupon userCoupon = tblUserCouponRepository.findFirstByUserCouponId(userCouponId);
            //????????????
            couponAmount = userCoupon.getCouponAmount();
        }
        amountDouble = ArithUtil.sub(amountDouble, couponAmount);
        if (amountDouble.compareTo(BigDecimal.ZERO) == 0){
            //??????????????????????????????????????????????????????????????????
            return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
        }
        //??????????????????
        logger.info("????????????????????????????????????????????????");
        TransactionRecord transactionRecord = new TransactionRecord()
                .setUserId(userId)
                .setOrderId(order_id)
                .setPrice(amountDouble)
                .setType(Code.Trade.BET)
                .setRecordStatus(Code.Trade.THIRD_BET_SUCCESS)
                .setChannelNo(user.getChannelNo());
        if (null == userBalance) {
            logger.info("?????????????????????id???" + userId + "?????????");
            return ResultGenerator.genExceptionResult(StatusCode.ERROR.getCode(), "?????????????????????");
        } else {
            BigDecimal totalBalance = userBalance.getTotalBalance();
            if (totalBalance.compareTo(amountDouble) == -1) {
                logger.info("?????????????????????????????????");
                return ResultGenerator.genExceptionResult(StatusCode.ERROR.getCode(), "????????????");
            } else {
                //????????????
                BigDecimal rechargeAmount = userBalance.getRechargeAmount();
                //???????????????
                BigDecimal totalAfter = ArithUtil.sub(totalBalance, amountDouble);

                if (rechargeAmount.compareTo(amountDouble) == -1) {
                    logger.info("??????????????????????????????????????????????????????");
                    //???????????????0
                    BigDecimal rechargeAfter = new BigDecimal(0.0);

                    //??????????????????
                    BigDecimal sub = ArithUtil.sub(amountDouble, rechargeAmount);
                    BigDecimal rewardAmount = userBalance.getRewardAmount();
                    BigDecimal rewardAfter = ArithUtil.sub(rewardAmount, sub);

                    //????????????
                    repository.deductionUpdate(rechargeAfter, rewardAfter, totalAfter, userId, userBalance.getVersion());
                    //??????????????????????????????
                    transactionRecord.setBalance(totalAfter);
                    //???????????????????????????????????????????????????
                    transactionRecord.setRechargeDeduct(rechargeAmount);
                    //?????????????????????????????????????????????????????????
                    transactionRecord.setRewardDeduct(sub);
                } else {
                    logger.info("?????????????????????????????????????????????");
                    //?????????????????????
                    BigDecimal rechargeAfter = ArithUtil.sub(rechargeAmount, amountDouble);

                    //????????????
                    repository.rechargeUpdate(rechargeAfter, totalAfter, userId);
                    transactionRecord.setBalance(totalAfter);
                    //???????????????????????????????????????????????????
                    transactionRecord.setRechargeDeduct(amountDouble);
                    //?????????????????????????????????????????????????????????
                    transactionRecord.setRewardDeduct(new BigDecimal(0.0));
                }

                logger.info("?????????????????????????????????");
                transactionRecordService.record(transactionRecord, "??????");
            }
        }
        return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
    }

    @Override
    public void batchReward(List<CommonRecord> commonRecords) {

    }

    @Override
    @Transactional
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.REENTRANT_LOCK, waitTime = 30000)
    public void withdrawalReturn(String userId, BigDecimal amount,long relateRecordId) {
        User user = userRepository.findByUserId(userId);
        TblUserBalance userBalance = repository.findByUserId(userId);

        //??????????????????
        TransactionRecord transactionRecord = new TransactionRecord()
                .setUserId(userId)
                .setVoucherNo(Long.valueOf(idWorker.nextId()))
                .setPrice(amount)
                .setType(Code.Trade.REFUND)
                .setRecordStatus(Code.Trade.WITHDRAWAL_REFUNDING_SUCCESS)
                .setRecordCreateTime(new Date())
                .setRecordFinishTime(new Date())
                .setChannelNo(user.getChannelNo());
        transactionRecord.setRelateRecordId(relateRecordId);
        logger.info("????????????id???" + userId + "???????????????" + amount + "???");
        BigDecimal rewardAmount = userBalance.getRewardAmount();
        rewardAmount=rewardAmount.add(amount);
        BigDecimal totalBalance = userBalance.getTotalBalance();
        totalBalance=totalBalance.add(amount);
        transactionRecord.setBalance(totalBalance);
        userBalance.setTotalBalance(totalBalance);
        userBalance.setRewardAmount(rewardAmount);
        repository.save(userBalance);
        logger.info("?????????????????????????????????");
        transactionRecordRepository.saveAndFlush(transactionRecord);
    }

//    @Override
//    @Transactional
//    public Result exceptionOrderRefound(String userId, BigDecimal amount, String orderId) {
//        try {
//            //??????????????????
//            Order order = iOrderService.findByOrderId(Long.valueOf(orderId));
//            //??????????????????????????????
//            BigDecimal couponAmount = null == order.getCouponAmount()?BigDecimal.ZERO:order.getCouponAmount();
//
//            //?????????????????????,???????????????????????????????????????
//            if (couponAmount.compareTo(BigDecimal.ZERO) == 1) {
//                tblUserCouponService.compensateCoupon(order.getUserCouponId(), couponAmount, Long.valueOf(orderId));
//            }
//
//            //????????????????????????????????????
//            refund(userId, amount, orderId, orderId);
//
//            //??????????????????order_details????????????BillStatus????????????3???????????????
//            iOrderService.updatePayRefundedStatus(Long.valueOf(orderId), ArithUtil.mul(amount, new BigDecimal("100")), 3);
//
//            iOrderService.updateStatus(orderId);
//        } catch (Exception e) {
//            return ResultGenerator.genFailResult(e.getMessage());
//        }
//        return ResultGenerator.genSuccessResult();
//    }

}
