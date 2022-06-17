package com.linglong.lottery_backend.user.cashier;

import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.user.cashier.entity.SubstituteRecord;
import com.linglong.lottery_backend.user.cashier.entity.WithdrawalEntity;
import com.linglong.lottery_backend.user.cashier.service.SubstitutePayService;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/api/withDrawal")
public class SubstitutePayController {

    private static final Logger logger = LoggerFactory.getLogger(SubstitutePayController.class);

    @Autowired
    private SubstitutePayService substitutePayService;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @GetMapping("/operate")
    public Result withDrawalReward(@RequestParam long accountId, @RequestParam BigDecimal amount, @SessionAttribute String userId) {
        checkArgument(accountId != 0l && null != amount, "参数不能为空");
        amount = amount.multiply(new BigDecimal(100));
        substitutePayService.substituteToAccount(accountId, userId, amount);
        return new Result(StatusCode.OK.getCode(), "提现完成", "");
    }

    @GetMapping("/verified")
    @CrossOrigin
    public Result verifiedRequest(@RequestParam long orderNo, @RequestParam String verifiedResult) {
        checkArgument(orderNo != 0l && null != verifiedResult, "参数不能为空");
        substitutePayService.verifiedRequest(orderNo, verifiedResult);
        return new Result(StatusCode.OK.getCode(), "审核完成", "");
    }

    @PostMapping("/receive")
    public String getResponseMsg(@ModelAttribute WithdrawalEntity entity) {
        logger.info("receive withDrawalNotifyMsg:" + entity.toString());
        substitutePayService.getResponse(entity);
        return "success";
    }

    @GetMapping("/getRecord")
    @CrossOrigin
    public Result getRecord() {
        List<SubstituteRecord> list = transactionRecordService.getSubstituteRecord();
        return new Result(StatusCode.OK.getCode(), "查询记录", list);
    }
}
