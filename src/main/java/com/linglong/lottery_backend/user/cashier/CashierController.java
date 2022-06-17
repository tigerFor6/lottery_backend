package com.linglong.lottery_backend.user.cashier;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.order.model.Result;
import com.linglong.lottery_backend.order.model.StatusCode;
import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.user.cashier.entity.CashierBody;
import com.linglong.lottery_backend.user.cashier.entity.CashierNewBody;
import com.linglong.lottery_backend.user.cashier.entity.TblCashierAmount;
import com.linglong.lottery_backend.user.cashier.entity.TblPaymentConfiguration;
import com.linglong.lottery_backend.user.cashier.repository.TblCashierAmountRepository;
import com.linglong.lottery_backend.user.cashier.repository.TblPaymentConfigurationRepository;
import com.linglong.lottery_backend.user.cashier.service.CashierService;
import com.linglong.lottery_backend.user.transaction.service.GlobalConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/api/cashier")
@Slf4j
public class CashierController {

    @Autowired
    CashierService cashierService;

    @Autowired
    GlobalConfigurationService globalConfigurationService;

    @Autowired
    TransactionRecordService transactionRecordService;

    @Autowired
    private TblCashierAmountRepository tblCashierAmountRepository;

    @Autowired
    private TblPaymentConfigurationRepository tblPaymentConfigurationRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final BigDecimal minAmount = new BigDecimal("1000");
    private final BigDecimal maxAmount = new BigDecimal("1000000");

    @GetMapping("/sign")
    public Result cashierSign(@RequestParam String hrefUrl, @SessionAttribute String userId) throws UnsupportedEncodingException {
        checkArgument(Strings.isNotBlank(userId), "请先登陆，然后充值");
        //根据订单号查询订单信息
        String url = cashierService.cashierSign(hrefUrl, userId);
        return new Result(StatusCode.OK.getCode(), "", url);
    }

    @PostMapping("/payOrder")
    public Result payOrder(@RequestParam String returnUrl, @RequestParam("amount") BigDecimal amount, @RequestParam("code") String code, @SessionAttribute String userId) throws UnsupportedEncodingException {
        checkArgument(Strings.isNotBlank(userId), "请先登陆，然后充值");
        //根据订单号查询订单信息
        if (amount.intValue() == 0){
            return new Result(StatusCode.ERROR.getCode(), "充值金额不能为0");
        }
        Map<String,Object> map = cashierService.payOrder(returnUrl,amount, code, userId);
        if (map.isEmpty()) {
            return new Result(StatusCode.ERROR.getCode(), "失败");
        }
        return new Result(StatusCode.OK.getCode(), "成功", map);
    }

    @GetMapping("/queryOrder")
    public Result queryOrder(@RequestParam("recordNo") Long recordNo) throws UnsupportedEncodingException {
        String msg = cashierService.queryOrder(recordNo);
        return new Result(StatusCode.OK.getCode(), msg, null);
    }

    /**
     * 接收Cashier充值成功请求
     *
     * @param body
     * @return
     */
    @PostMapping("/receive")
    public String cashierSign(@ModelAttribute CashierBody body) {
        checkArgument(null != body, "response is null");
        cashierService.updateOrderStatus(body);
        return "success";
    }

    /**
     * 接收Cashier充值成功请求(新)
     *
     * @param
     * @return
     */
    @PostMapping("/cashierReceive")
    public String cashierReceive(@ModelAttribute CashierNewBody body) {
        log.info("充值回调"+body.getOutTradeNo());
        checkArgument(null != body, "response is null");
        cashierService.updateChargeOrderStatus(body);
        return "success";
    }

    /**
     * 支付金额选择展示
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/cashierAmount")
    public Result cashierAmount(){
        String s = redisTemplate.opsForValue().get(Code.Redis.TBL_CASHIER_AMOUNT_KEY);
        List<TblCashierAmount> tblCashierAmountList = new ArrayList<TblCashierAmount>();
        if (StringUtils.isEmpty(s)){
            tblCashierAmountList = tblCashierAmountRepository.findAllByStatus(1);
            tblCashierAmountList = tblCashierAmountList.stream().sorted(Comparator.comparing(TblCashierAmount::getAmount)).collect(Collectors.toList());
            redisTemplate.opsForValue().set(Code.Redis.TBL_CASHIER_AMOUNT_KEY, JSON.toJSONString(tblCashierAmountList));
        }else{
            tblCashierAmountList = JSON.parseArray(s,TblCashierAmount.class);
        }
        List<BigDecimal> list = new ArrayList<BigDecimal>();
        if (!tblCashierAmountList.isEmpty()){
            for (TblCashierAmount tblCashierAmount : tblCashierAmountList){
                BigDecimal amount = tblCashierAmount.getAmount();
                list.add(amount);
            }
        }
        return new Result(StatusCode.OK.getCode(), "", list);
    }

    /**
     * 支付方式查询
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/cashierWays")
    public Result cashierWays(){
        String s = redisTemplate.opsForValue().get(Code.Redis.TBL_PAYMENT_CONFIGURATION_KEY);
        List<TblPaymentConfiguration> tblPaymentConfigurationList = new ArrayList<TblPaymentConfiguration>();
        if (null == s){
            tblPaymentConfigurationList = tblPaymentConfigurationRepository.findAll();
            tblPaymentConfigurationList = tblPaymentConfigurationList.stream().filter(e -> e.getWeight() != -1).collect(Collectors.toList());
            redisTemplate.opsForValue().set(Code.Redis.TBL_PAYMENT_CONFIGURATION_KEY, JSON.toJSONString(tblPaymentConfigurationList));
        }else{
            tblPaymentConfigurationList = JSON.parseArray(s,TblPaymentConfiguration.class);
        }
        List<Map<String, Object>> results = new ArrayList<>();
        if (null != tblPaymentConfigurationList){
            for (TblPaymentConfiguration payment : tblPaymentConfigurationList){
                Map<String, Object> map = new HashMap<>();
                String code = payment.getCode();
                String name = payment.getName();
                BigDecimal minAmount = payment.getMinAmount();
                BigDecimal maxAmount = payment.getMaxAmount();
                String description = payment.getDescription();
                String picture = payment.getPicture();
                Long weight = payment.getWeight();
                map.put("code",code);
                map.put("name",name);
                map.put("weight",weight);
                map.put("minAmount",minAmount);
                map.put("maxAmount",maxAmount);
                map.put("description",description);
                map.put("picture",picture);
                results.add(map);
            }
        }
        return new Result(StatusCode.OK.getCode(), "", results);
    }

//    /**
//     *
//     * @param entity
//     * @return
//     */
//    @PostMapping("/charge/receive")
//    public String cashierReceive(@ModelAttribute WithdrawalEntity entity) {
//        checkArgument(null != entity, "response is null");
//        //log.info("");
//        cashierService.updateRecordStatus(entity);
//        return "success";
//    }
}
