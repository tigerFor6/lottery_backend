package com.linglong.lottery_backend.user.cashier.service.impl;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.linglong.lottery_backend.common.error.CashierException;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TransactionRecordRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.TblUserBalanceService;
import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.user.bankcard.pojo.AccountInfo;
import com.linglong.lottery_backend.user.bankcard.service.IAccountInfoService;
import com.linglong.lottery_backend.user.cashier.entity.GlobalConfiguration;
import com.linglong.lottery_backend.user.cashier.entity.OrderConfigEntity;
import com.linglong.lottery_backend.user.cashier.entity.WithdrawalEntity;
import com.linglong.lottery_backend.user.cashier.service.SubstitutePayService;
import com.linglong.lottery_backend.user.transaction.service.GlobalConfigurationService;
import okhttp3.*;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class SubstitutePayServiceImpl implements SubstitutePayService {

    private static final Logger logger = LoggerFactory.getLogger(SubstitutePayServiceImpl.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IAccountInfoService iAccountInfoService;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private GlobalConfigurationService globalConfigurationService;

    @Autowired
    private TblUserBalanceService tblUserBalanceService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void substituteToAccount(long accountId, String userId, BigDecimal amount) {

        User user = userRepository.findByUserId(userId);
        String realName = user.getRealname();
        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            throw new CashierException("提现金额需要大于0");
        }
        if (Strings.isBlank(realName)) {
            throw new CashierException("未进行实名认证，不能进行交易");
        }
        AccountInfo accountInfo = iAccountInfoService.getAccountInfoById(accountId);
        if (null == accountInfo) {
            throw new CashierException("账户不存在，请联系管理员解决");
        }
        if (!accountInfo.getUserId().equals(userId)) {
            throw new CashierException("用户与账户不匹配");
        }
        Long orderNo = transactionRecordService.insertRecord(user, Code.Trade.CASH_WITHDRAWAL, Code.Trade.CASH_WITHDRAWALING);
        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(orderNo));
        transactionRecordService.updateBalance(userId, record, amount, "minus");
        //交易记录表中记录体现的账户ID，后期方便查询到对应的银行账号
        record.setExtendInfo(String.valueOf(accountId));

        //从redis中读取平台限制的用户每日提现次数
        String cashNum = redisTemplate.opsForValue().get(Code.Redis.CASH_NUM_KEY);
        if (null == cashNum || "".equals(cashNum)) {
            //如果没读取到，则初始化为5次
            logger.info("初始化平台限制用户每日提现次数为5次");
            redisTemplate.opsForValue().set(Code.Redis.CASH_NUM_KEY, "5");
            cashNum = "5";
        }

        String userCashNum = redisTemplate.opsForValue().get(Code.Redis.USER_CASH_NUM_KEY + userId);
        if (null == userCashNum || "".equals(userCashNum)) {
            logger.info("未查询到用户" + userId + "的提现次数，将该用户提现次数初始化为0,有效期为1天");
            redisTemplate.opsForValue().set(Code.Redis.USER_CASH_NUM_KEY + userId, "0", 1L, TimeUnit.DAYS);
            userCashNum = "0";
        }
        //提现金额小于等于100元并且当日提现次数不超过5次时，直接请求第三方提现接口。其他情况都需要走审核流程
        if (amount.compareTo(new BigDecimal("10000")) < 1) {

            if (Integer.valueOf(userCashNum) > Integer.valueOf(cashNum)) {
                transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
            } else {
                record.setRecordStatus(Code.Trade.CASH_WITHDRAWAL_CHECK);
                transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
                verifiedRequest(orderNo, "1");
                //该笔提现需要给该用户 当日提现 +1
                redisTemplate.opsForValue().set(Code.Redis.USER_CASH_NUM_KEY + userId, String.valueOf(Integer.valueOf(userCashNum) + 1));
                logger.info("给用户id为" + userId + "的用户当日提现次数+1");
            }

        } else {
            transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
        }
    }

    @Override
    public void getResponse(WithdrawalEntity entity) {

        String status = Code.Trade.CASH_WITHDRAWAL_SUCCESS;
        if (Strings.isBlank(entity.getOrderId()) || Strings.isBlank(entity.getOutTradeNo()) || Strings.isBlank(entity.getAmount())) {
            logger.error("withdrawalNotify failed,entity is :" + entity.toString());
            throw new RuntimeException("参数不全");
        }
        TransactionRecord record = transactionRecordService.findByRecordNoAndThirdPartyRecordNo(entity.getOutTradeNo(), entity.getOrderId());
        if (null == record) {
            logger.error("withdrawalNotify failed,tbl_transaction_record not exist,entity is :" + entity.toString());
            status = Code.Trade.CASH_WITHDRAWAL_FAIL;
            throwException(status, record);
        }
        if (!record.getRecordStatus().equals(Code.Trade.CASH_WITHDRAWAL_CHECK)) {
            logger.error("withdrawalNotify failed,entity is :" + entity.toString());
            throw new RuntimeException("同一笔提现不允许多次修改");
        }
        BigDecimal amount = new BigDecimal(entity.getAmount());
        if (record.getPrice().compareTo(amount) != 0) {
            logger.error("withdrawalNotify failed,amount compare failed,entity is:" + entity.toString());
            status = Code.Trade.CASH_WITHDRAWAL_FAIL;
            throwException(status, record);
        }

        if (!entity.getCode().equals("0")) {
            tblUserBalanceService.withdrawalReturn(record.getUserId(), record.getPrice(), record.getId());
            logger.error("提现失败，将余额恢复到用户账户中");
            logger.error("Withdrawal failed:" + entity.toString());
            status = Code.Trade.CASH_WITHDRAWAL_FAIL;
        }

        record.setRecordStatus(status);
        transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
    }

    @Override
    public void verifiedRequest(Long orderNo, String verifiedResult) {
        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(orderNo));
        User user = userRepository.findByUserId(record.getUserId());
        long accountId = Long.valueOf(record.getExtendInfo());
        AccountInfo accountInfo = iAccountInfoService.getAccountInfoById(accountId);
        if (verifiedResult.equals("0")) {
            tblUserBalanceService.withdrawalReturn(record.getUserId(), record.getPrice(), record.getId());
            logger.error("审核失败，将余额恢复到用户账户中");
            logger.error("recordNo is:" + record.getRecordNo());
            record.setRecordStatus(Code.Trade.CASH_WITHDRAWAL_FAIL);
            transactionRecordRepository.save(record);
            return;
        }

        GlobalConfiguration globalConfiguration = globalConfigurationService.findByCode("substitute");
        String configParam = globalConfiguration.getConfig();
        Gson gson = new Gson();
        OrderConfigEntity orderConfigEntity = gson.fromJson(configParam, OrderConfigEntity.class);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = getParam(accountInfo, String.valueOf(orderNo), user, record.getPrice(), orderConfigEntity);
        Request request = new Request.Builder()
                .url(orderConfigEntity.getRequestUrl())
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String result = response.body().string();
            logger.info("receive withDrawalMsg:" + result);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(result).getAsJsonObject();
            String code = jsonObject.get("code").getAsString();
            if (code.equals("0")) {
                String thirdPartyOrderId = jsonObject.get("orderId").getAsString();
                record.setThirdPartyRecordNo(thirdPartyOrderId);
                record.setRecordStatus(Code.Trade.CASH_WITHDRAWAL_CHECK);
            } else {
                record.setRecordStatus(Code.Trade.CASH_WITHDRAWAL_FAIL);
                throw new CashierException("提现失败，请重试");
            }
            transactionRecordRepository.save(record);
        } catch (IOException e) {
            logger.error("请求第三方代付接口失败");
            logger.error(e.getMessage());
            throw new CashierException("提现失败，请重试");
        }
    }


    public void throwException(String status, TransactionRecord record) {
        record.setRecordStatus(status);
        transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
        throw new RuntimeException("更新提现记录失败");
    }

    public RequestBody getParam(AccountInfo accountInfo, String orderId, User user, BigDecimal amount, OrderConfigEntity configuration) {

        long amountVal = amount.longValue();
        StringBuilder sign = new StringBuilder();
        sign.append("amount=").append(amountVal)
                .append("&").append("appId=").append(configuration.getAppId())
                .append("&").append("bankNo=").append(accountInfo.getBankCardNum())
                .append("&").append("notifyUrl=").append(configuration.getNotifyUrl())
                .append("&").append("orderId=").append(orderId)
                .append("&").append("ownerName=").append(user.getRealname())
                .append("&").append("key=").append(configuration.getSecret())
        ;
        String result = Hashing.md5().hashBytes(sign.toString().getBytes()).toString().toUpperCase();
        logger.info("request Sign is :" + sign.toString());
        logger.info("encrypted Str is :" + result);

        RequestBody formBody = new FormBody.Builder()
                .add("amount", String.valueOf(amountVal))
                .add("appId", configuration.getAppId())
                .add("bankName", accountInfo.getAffiliatedBank())
                .add("bankNo", accountInfo.getBankCardNum())
                .add("notifyUrl", configuration.getNotifyUrl())
                .add("orderId", orderId)
                .add("ownerName", user.getRealname())
                .add("orderType", configuration.getOrderType())
                .add("payType", configuration.getPayType())
                .add("sign", result)
                .add("phoneNo", user.getPhone())
                .add("idNo", user.getIdCard())
                .build();
        return formBody;
    }
}
