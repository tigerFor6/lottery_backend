package com.linglong.lottery_backend.user.account.controller;

import com.linglong.lottery_backend.user.account.entity.TblUserBalance;
import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.lottery.match.common.Result;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;


@RestController
@RequestMapping(value = "/api/account/balance")
public class AccountBalanceController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    TblUserBalanceService userBalanceService;

    @Autowired
    private TransactionRecordService transactionRecordService;


    @PutMapping("recharge")
    public Result recharge(@RequestBody HashMap<String, BigDecimal> m, HttpSession session) {
        try {
            BigDecimal amount = m.get("amount");
            if (null != amount && amount.compareTo(BigDecimal.ZERO) == 1) {
                String userId = session.getAttribute("userId").toString();
                userBalanceService.recharge(userId, amount);
                return ResultGenerator.genSuccessResult();
            }
        } catch (Exception e) {
            logger.error("出错了:", e);
        }
        return ResultGenerator.genFailResult("服务器开小差儿了");
    }

    @GetMapping("getUserBalance")
    public Result getUserBalance(@SessionAttribute("userId") String userId) {
        TblUserBalance tblUserBalance = userBalanceService.findByUserId(userId);

        return ResultGenerator.genSuccessResult(tblUserBalance);
    }

    /**
     * @description: 返回对应用户的账户明细
     * @author: ZhiYao.Zhang
     * @param:
     * @return:
     * @create: 2019-04-16
     **/

    @GetMapping("getTransactionRecords")
    public Result getTransactionRecords(@RequestParam(value = "page") Integer page,@RequestParam("size") Integer size,@SessionAttribute("userId") String userId) {
        Page<TransactionRecord> transactionRecords = transactionRecordService.findListByParam(Long.valueOf(userId),page,size);
        return ResultGenerator.genSuccessResult(transactionRecords);

    }

}
