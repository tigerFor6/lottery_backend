package com.linglong.lottery_backend.user.account.service.impl;

import com.google.common.collect.Lists;
import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TblUserBalanceRepository;
import com.linglong.lottery_backend.user.account.repository.TransactionRecordRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.user.cashier.entity.SubstituteBo;
import com.linglong.lottery_backend.user.cashier.entity.SubstituteRecord;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.common.error.CashierException;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import com.linglong.lottery_backend.order.model.StatusCode;
import com.linglong.lottery_backend.user.cashier.entity.TblChargeOrder;
import com.linglong.lottery_backend.user.cashier.repository.TblChargeOrderRepository;
import com.linglong.lottery_backend.utils.ArithUtil;
import com.linglong.lottery_backend.utils.IdWorker;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;


@Service
public class TransactionRecordServiceImpl implements TransactionRecordService {
    private static final Logger logger = LoggerFactory.getLogger(TblUserBalanceServiceImpl.class);

//    @Autowired
//    private RecordListRepository recordListRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private TblUserBalanceRepository tblUserBalanceRepository;

    @Autowired
    private TblChargeOrderRepository tblChargeOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdWorker idWorker;

    private final String[] status= {"2","3","4","8","15","10","6","16","17","22"};

    @Override
    public Result record(TransactionRecord transactionRecord, String resType) {
        Date data = new Date();

        if (Code.Trade.RECHARGE.equals(resType) || Code.Trade.RETURN_AWARD.equals(resType)) {
            TransactionRecord rep = transactionRecordRepository.findByRecordNo(transactionRecord.getRecordNo());
            if (null == rep) {
                logger.info("开始插入充值订单记录"+"orderId:"+transactionRecord.getOrderId());
                transactionRecord.setRecordCreateTime(data).setRecordFinishTime(data);
                transactionRecordRepository.saveAndFlush(transactionRecord);
            } else {
                logger.info("订单记录已存在，改变订单状态"+"recordNo:"+transactionRecord.getRecordNo());
                transactionRecordRepository.updateDraw(transactionRecord.getRecordStatus(), transactionRecord.getRecordNo());
            }
        } else {
            TransactionRecord rep = transactionRecordRepository.findByOrderId(transactionRecord.getOrderId());
            if (null == rep) {
                logger.info("开始插入扣减订单记录"+"orderId:"+transactionRecord.getOrderId());
                transactionRecord.setRecordCreateTime(data).setRecordFinishTime(data);
                transactionRecordRepository.saveAndFlush(transactionRecord);
            } else {
                logger.info("订单记录已存在，改变订单状态"+"orderId:"+transactionRecord.getOrderId());
                transactionRecordRepository.updateOrder(transactionRecord.getRecordStatus(), transactionRecord.getOrderId());
            }
        }

        return ResultGenerator.genSuccessResult(StatusCode.OK.getCode());
    }

    @Override
    public Map<String, Object> getrecordByUserIdPage(Long userId, Integer page_end) {
        Map<String, Object> resmap = new HashMap<>();

        //算出需要返回的起始数据条数
        Integer start = (page_end - 1) * Code.SetPageSize.PAGESIZE;

        //查询记录数据
        List<TransactionRecord> res = transactionRecordRepository.findRecordByUserID(userId,start,Code.SetPageSize.PAGESIZE);
        //查询总记录数据
        List<TransactionRecord> num = transactionRecordRepository.countTransactionRecordsByUserId(userId.toString());

//算出一共几页数据
        Integer page = Integer.valueOf(num.size()) / Code.SetPageSize.PAGESIZE;
        Integer ls = Integer.valueOf(num.size()) % Code.SetPageSize.PAGESIZE;
        if (ls > 0) {
            page++;
        }

        resmap.put("totalpage", page);
        resmap.put("res", res);
        return resmap;
    }

//    @Override
//    public Map<String, Object> getrecordByUserIdPage(Long userId, Integer page_end) {
//        ArrayList<Object[]> res = new ArrayList<>();
//        Map<String, Object> resmap = new HashMap<>();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//
//        try {
//            Date dBegin = sdf.parse("2019-03");
//            Date dEnd = sdf.parse(sdf.format(new Date()));
//            List<String> datas = dateTool.findDates(dBegin, dEnd);
//
//
//            //查询近一个时间段内的数据
//            ArrayList<Object[]> res1 = recordListRepository.findRecordByUserID(datas, userId);
//
//            //算出一共几页数据
//            Integer page = res1.size() / Code.SetPageSize.PAGESIZE;
//            Integer ls = res1.size() % Code.SetPageSize.PAGESIZE;
//            if (ls > 0) {
//                page++;
//            }
//
//            //算出需要返回的数据段
//            Integer start = (page_end - 1) * Code.SetPageSize.PAGESIZE;
//            Integer end = page_end * Code.SetPageSize.PAGESIZE;
//            for (int a = start; a < end; a++) {
//                if (a > res1.size() - 1) {
//                    break;
//                }
//                res.add(res1.get(a));
//            }
//
//            resmap.put("totalpage", page);
//            resmap.put("res", res);
//        } catch (Exception e) {
//
//        }
//        return resmap;
//    }

    @Override
    public TransactionRecord findByVoucherNo(Long voucherNo) {
        return transactionRecordRepository.findFirstByVoucherNo(voucherNo);
    }

    @Override
    public Long insertRecord(User user, String type, String status) {
        Date data = new Date();
        long recordNo = idWorker.nextId();

        TransactionRecord transactionRecord = new TransactionRecord()
                .setUserId(user.getUserId())
                .setRecordNo(recordNo)
                .setPrice(new BigDecimal("0.0"))
                .setVoucherNo(idWorker.nextId())
                .setType(type)
                .setRecordStatus(status)
                .setRecordCreateTime(data)
                .setRecordFinishTime(data)
                .setChannelNo(user.getChannelNo());

        logger.info("开始插入充值订单记录");
        transactionRecordRepository.saveAndFlush(transactionRecord);
        return Long.valueOf(recordNo);
    }

    @Override
    public Long insertRecord(Long userId, BigDecimal amount, String type, String status) {
        User user = userRepository.findByUserId(String.valueOf(userId));
        Date data = new Date();
        long recordNo = idWorker.nextId();

        TransactionRecord transactionRecord = new TransactionRecord()
                .setUserId(userId.toString())
                .setRecordNo(recordNo)
                .setPrice(amount)
                .setVoucherNo(idWorker.nextId())
                .setType(type)
                .setRecordStatus(status)
                .setChannelNo(user.getChannelNo())
                .setRecordCreateTime(data)
                .setRecordFinishTime(data);

        logger.info("开始插入充值订单记录");
        transactionRecordRepository.saveAndFlush(transactionRecord);
        return Long.valueOf(recordNo);
    }

    @Override
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.WRITE_LOCK, waitTime = 30000)
    public Boolean updateRecharge(String userId, BigDecimal amount, String recordNo, String recordStatus, String thirdPartyOrderId) {

        TransactionRecord rep = transactionRecordRepository.findByRecordNo(Long.valueOf(recordNo));
        if (null == rep) {
            throw new CashierException("没有查询到对应记录");
        }

        //查询余额表
        TblUserBalance userBalance = tblUserBalanceRepository.findByUserId(userId);

        logger.info("记录状态为'成功',修改余额表并拉取余额放入充值记录中");
        if (null == userBalance) {
            logger.info("没有从用户余额表中查出对应用户id为" + userId + "的数据");
            throw new CashierException("没有从用户余额表中查出对应用户id为" + userId + "的数据");
        } else {
            logger.info("将给用户id为" + userId + "的用户充值" + amount + "分");
            BigDecimal rechargeAmount = userBalance.getRechargeAmount();
            BigDecimal totalBalance = userBalance.getTotalBalance();
            if (recordStatus.equals(Code.Trade.RECHARGE_SUCCESS)) {
                rechargeAmount = ArithUtil.add(amount, rechargeAmount);
                totalBalance = ArithUtil.add(amount, totalBalance);

            }
            //tblUserBalanceRepository.rechargeUpdate(rechargeAmount, totalBalance, userId);
            userBalance.setRechargeAmount(rechargeAmount)
                    .setTotalBalance(totalBalance);
            tblUserBalanceRepository.save(userBalance);
            logger.info("拉取余额表中的余额放入充值记录中");
            //transactionRecordRepository.updateRecharge(amount, totalBalance, recordNo, recordStatus, thirdPartyOrderId);
            rep.setPrice(amount)
                    .setBalance(totalBalance)
                    .setRecordStatus(recordStatus)
                    .setThirdPartyRecordNo(thirdPartyOrderId);
            transactionRecordRepository.save(rep);
            return true;
        }
    }

    @Override
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.WRITE_LOCK, waitTime = 30000)
    public void updateBalance(String userId, TransactionRecord record, BigDecimal amount, String operation) {
        TblUserBalance userBalance = tblUserBalanceRepository.findByUserId(record.getUserId());
        BigDecimal rewardAmount = userBalance.getRewardAmount();
        BigDecimal totalBalance = userBalance.getTotalBalance();

        if (operation.equals("plus")) {
            rewardAmount = rewardAmount.add(amount);
            totalBalance = totalBalance.add(amount);
        } else if (operation.equals("minus")) {
            if (rewardAmount.compareTo(amount) < 0) {
                record.setPrice(amount);
                record.setBalance(totalBalance);
                record.setRecordStatus(Code.Trade.CASH_WITHDRAWAL_FAIL);
                transactionRecordRepository.save(record);
                return;
            }
            rewardAmount = rewardAmount.subtract(amount);
            totalBalance = totalBalance.subtract(amount);
        }
        record.setPrice(amount);
        record.setBalance(totalBalance);
        userBalance.setTotalBalance(totalBalance);
        userBalance.setRewardAmount(rewardAmount);
        tblUserBalanceRepository.save(userBalance);
        transactionRecordRepository.save(record);
    }

    @Override
    public TransactionRecord findByRecordNoAndThirdPartyRecordNo(String recordNo, String thirdPartyRecordNo) {
        return transactionRecordRepository.findByRecordNoAndThirdPartyRecordNo(Long.valueOf(recordNo), thirdPartyRecordNo);
    }

    @Override
    @LockAction(key = LockKey.lockOrder, value = "#recordNo", lockType = LockType.WRITE_LOCK, waitTime = 30000)
    public void updateRecord(String recordNo, TransactionRecord record) {
        transactionRecordRepository.save(record);
    }

    @Override
    public List<SubstituteRecord> getSubstituteRecord() {
        List<SubstituteBo> list=transactionRecordRepository.getSubstituteRecord();
        List<SubstituteRecord> recordList= Lists.newArrayList();
        for(SubstituteBo instance:list)
        {
            SubstituteRecord record=adapat(instance);
            recordList.add(record);
        }
        return recordList;
    }

    @Override
    public TransactionRecord findByRecordNo(Long orderNo) {
        return transactionRecordRepository.findByRecordNo(orderNo);
    }

    @Override
    public Page<TransactionRecord> findListByParam(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.DESC, "updatedTime"));
        return transactionRecordRepository.findAll(new Specification<TransactionRecord>(){
            @Override
            public Predicate toPredicate(Root<TransactionRecord> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new LinkedList<>();
                list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), userId));
                Path<Object> path = root.get("recordStatus");
                CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                for (int i = 0; i < status.length; i++) {
                    in.value(status[i]);
                }
                list.add(in);
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        },pageable);
    }


    @Override
    @LockAction(key = LockKey.lockUserAccount, value = "#userId", lockType = LockType.WRITE_LOCK, waitTime = 30000)
    public Boolean updateRechargeOrder(String userId, TransactionRecord record, TblChargeOrder chargeOrder) {
        //查询余额表
        TblUserBalance userBalance = tblUserBalanceRepository.findByUserId(userId);

        logger.info("记录状态为'成功',修改余额表并拉取余额放入充值记录中");
        if (null == userBalance) {
            logger.info("没有从用户余额表中查出对应用户id为" + userId + "的数据");
            throw new CashierException("没有从用户余额表中查出对应用户id为" + userId + "的数据");
        } else {
            BigDecimal rechargeAmount = userBalance.getRechargeAmount();
            BigDecimal totalBalance = userBalance.getTotalBalance();
            if (chargeOrder.getRecordStatus().equals(Code.Trade.RECHARGE_SUCCESS) || chargeOrder.getRecordStatus().equals(Code.Trade.RECHARGE_AFTER_SUCCESS)) {
                logger.info("将给用户id为" + userId + "的用户充值" + chargeOrder.getRechargeAmount() + "分");
                rechargeAmount = ArithUtil.add(chargeOrder.getRechargeAmount(), rechargeAmount);
                totalBalance = ArithUtil.add(chargeOrder.getRechargeAmount(), totalBalance);

            }
            userBalance.setRechargeAmount(rechargeAmount)
                    .setTotalBalance(totalBalance);
            tblUserBalanceRepository.save(userBalance);
            logger.info("拉取余额表中的余额放入充值记录中");
            record.setPrice(chargeOrder.getRechargeAmount())
                    .setBalance(totalBalance);

            transactionRecordRepository.save(record);
            tblChargeOrderRepository.save(chargeOrder);
            return true;
        }
    }

    public SubstituteRecord adapat(SubstituteBo substituteBo){
        String phoneNumber=substituteBo.getPhoneNumber();
        phoneNumber=phoneNumber.substring(0,4)+"****"+phoneNumber.substring(phoneNumber.length()-4);
        SubstituteRecord record=new SubstituteRecord(substituteBo.getRecordNo(),substituteBo.getAmount(),substituteBo.getUserName(),substituteBo.getCreatedTime(),phoneNumber);
        record.setUserId(substituteBo.getUserId());
        return record;
    }
}
