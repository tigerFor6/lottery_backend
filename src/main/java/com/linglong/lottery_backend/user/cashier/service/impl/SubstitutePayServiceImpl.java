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
            throw new CashierException("????????????????????????0");
        }
        if (Strings.isBlank(realName)) {
            throw new CashierException("??????????????????????????????????????????");
        }
        AccountInfo accountInfo = iAccountInfoService.getAccountInfoById(accountId);
        if (null == accountInfo) {
            throw new CashierException("??????????????????????????????????????????");
        }
        if (!accountInfo.getUserId().equals(userId)) {
            throw new CashierException("????????????????????????");
        }
        Long orderNo = transactionRecordService.insertRecord(user, Code.Trade.CASH_WITHDRAWAL, Code.Trade.CASH_WITHDRAWALING);
        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(orderNo));
        transactionRecordService.updateBalance(userId, record, amount, "minus");
        //???????????????????????????????????????ID?????????????????????????????????????????????
        record.setExtendInfo(String.valueOf(accountId));

        //???redis????????????????????????????????????????????????
        String cashNum = redisTemplate.opsForValue().get(Code.Redis.CASH_NUM_KEY);
        if (null == cashNum || "".equals(cashNum)) {
            //????????????????????????????????????5???
            logger.info("????????????????????????????????????????????????5???");
            redisTemplate.opsForValue().set(Code.Redis.CASH_NUM_KEY, "5");
            cashNum = "5";
        }

        String userCashNum = redisTemplate.opsForValue().get(Code.Redis.USER_CASH_NUM_KEY + userId);
        if (null == userCashNum || "".equals(userCashNum)) {
            logger.info("??????????????????" + userId + "??????????????????????????????????????????????????????0,????????????1???");
            redisTemplate.opsForValue().set(Code.Redis.USER_CASH_NUM_KEY + userId, "0", 1L, TimeUnit.DAYS);
            userCashNum = "0";
        }
        //????????????????????????100????????????????????????????????????5?????????????????????????????????????????????????????????????????????????????????
        if (amount.compareTo(new BigDecimal("10000")) < 1) {

            if (Integer.valueOf(userCashNum) > Integer.valueOf(cashNum)) {
                transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
            } else {
                record.setRecordStatus(Code.Trade.CASH_WITHDRAWAL_CHECK);
                transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
                verifiedRequest(orderNo, "1");
                //?????????????????????????????? ???????????? +1
                redisTemplate.opsForValue().set(Code.Redis.USER_CASH_NUM_KEY + userId, String.valueOf(Integer.valueOf(userCashNum) + 1));
                logger.info("?????????id???" + userId + "???????????????????????????+1");
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
            throw new RuntimeException("????????????");
        }
        TransactionRecord record = transactionRecordService.findByRecordNoAndThirdPartyRecordNo(entity.getOutTradeNo(), entity.getOrderId());
        if (null == record) {
            logger.error("withdrawalNotify failed,tbl_transaction_record not exist,entity is :" + entity.toString());
            status = Code.Trade.CASH_WITHDRAWAL_FAIL;
            throwException(status, record);
        }
        if (!record.getRecordStatus().equals(Code.Trade.CASH_WITHDRAWAL_CHECK)) {
            logger.error("withdrawalNotify failed,entity is :" + entity.toString());
            throw new RuntimeException("????????????????????????????????????");
        }
        BigDecimal amount = new BigDecimal(entity.getAmount());
        if (record.getPrice().compareTo(amount) != 0) {
            logger.error("withdrawalNotify failed,amount compare failed,entity is:" + entity.toString());
            status = Code.Trade.CASH_WITHDRAWAL_FAIL;
            throwException(status, record);
        }

        if (!entity.getCode().equals("0")) {
            tblUserBalanceService.withdrawalReturn(record.getUserId(), record.getPrice(), record.getId());
            logger.error("????????????????????????????????????????????????");
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
            logger.error("????????????????????????????????????????????????");
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
                throw new CashierException("????????????????????????");
            }
            transactionRecordRepository.save(record);
        } catch (IOException e) {
            logger.error("?????????????????????????????????");
            logger.error(e.getMessage());
            throw new CashierException("????????????????????????");
        }
    }


    public void throwException(String status, TransactionRecord record) {
        record.setRecordStatus(status);
        transactionRecordService.updateRecord(String.valueOf(record.getRecordNo()), record);
        throw new RuntimeException("????????????????????????");
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
