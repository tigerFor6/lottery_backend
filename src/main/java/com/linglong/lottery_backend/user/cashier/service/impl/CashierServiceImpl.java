package com.linglong.lottery_backend.user.cashier.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.linglong.lottery_backend.common.error.CashierException;
import com.linglong.lottery_backend.lottery.match.common.Code;
import com.linglong.lottery_backend.user.account.entity.TransactionRecord;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TransactionRecordRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.TblChargeOrderService;
import com.linglong.lottery_backend.user.account.service.TransactionRecordService;
import com.linglong.lottery_backend.user.cashier.entity.*;
import com.linglong.lottery_backend.user.cashier.repository.TblPayApiConfigurationRepository;
import com.linglong.lottery_backend.user.cashier.repository.TblPaymentConfigurationRepository;
import com.linglong.lottery_backend.user.cashier.service.CashierService;
import com.linglong.lottery_backend.user.cashier.utils.OkHttpClientUtils;
import com.linglong.lottery_backend.user.transaction.service.GlobalConfigurationService;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CashierServiceImpl implements CashierService {

    private static final Logger logger = LoggerFactory.getLogger(CashierServiceImpl.class);


    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private GlobalConfigurationService globalConfigurationService;

    @Autowired
    private TblPayApiConfigurationRepository tblPayApiConfigurationRepository;

    @Autowired
    private TblPaymentConfigurationRepository tblPaymentConfigurationRepository;

    @Autowired
    private TblChargeOrderService tblChargeOrderService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OkHttpClientUtils okHttpClientUtils;

//    @Autowired
//    private OrderRepository orderRepository;

    @Override
    public String cashierSign(String hrefUrl, String userId) throws UnsupportedEncodingException {
        StringBuffer url = new StringBuffer();

        GlobalConfiguration globalConfiguration=globalConfigurationService.findByCode("cashier");
        String configParam=globalConfiguration.getConfig();
        Gson gson=new Gson();
        OrderConfigEntity orderConfigEntity=gson.fromJson(configParam, OrderConfigEntity.class);

        StringBuffer signStr = new StringBuffer();
        signStr.append("appId=").append(orderConfigEntity.getAppId())
                .append("&hrefUrl=").append(hrefUrl)
                .append("&notifyUrl=").append(orderConfigEntity.getNotifyUrl())
                .append("&key=").append(orderConfigEntity.getSecret());
        logger.info("?????????-->" + signStr.toString());
        logger.info("??????????????????-->" + Hashing.md5().hashBytes(signStr.toString().getBytes()).toString().toUpperCase());

        User user = userRepository.findByUserId(userId);
        if (null == user) {
            throw new CashierException("???????????????");
        }
        String realname = user.getRealname();
        if (Strings.isBlank(user.getRealname())) {
            realname = user.getPhone().substring(7,11);
//            throw new CashierException("????????????????????????");
        }

        Long orderNo = transactionRecordService.insertRecord(user, Code.Trade.RECHARGE, Code.Trade.RECHARGEING);

        url.append(orderConfigEntity.getRequestUrl())
                .append("?").append("appId=")
                .append(orderConfigEntity.getAppId())
                .append("&")
                .append("orderId=").append(orderNo)
                .append("&")
                .append("notifyUrl=").append(orderConfigEntity.getNotifyUrl())
                .append("&")
                .append("hrefUrl=").append(hrefUrl)
                .append("&")
                .append("userId=").append(Long.parseLong(userId))
                .append("&")
                .append("userName=").append(URLEncoder.encode(realname, "utf-8"))
                .append("&")
                .append("sign=").append(Hashing.md5().hashBytes(signStr.toString().getBytes()).toString().toUpperCase());
        System.out.println(url.toString());
        return url.toString();
    }

    @Override
    public void updateOrderStatus(CashierBody body) {
        logger.info("receive thirdParty msg:" + body.toString());
        String orderNo = body.getOutTradeNo();
        String amount = body.getAmount();
        String code = body.getCode();
        String orderId = body.getOrderId();
        if (Strings.isBlank(orderNo) || Strings.isBlank(amount) || Strings.isBlank(code) || Strings.isBlank(orderId)) {
            executeErrorMsg("??????????????????", body);
        }
        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(orderNo));
        if (null == record) {
            executeErrorMsg("??????????????????", body);
        }

        BigDecimal actualPrice = new BigDecimal(amount);

        if (record.getRecordStatus().equals("2")) {
            executeErrorMsg("??????????????????????????????????????????", body);
        }

        if (code.equals("0")) {
            record.setRecordStatus(Code.Trade.RECHARGE_SUCCESS);
        } else if (code.equals("1")) {
            record.setRecordStatus(Code.Trade.RECHARGEING);
        } else {
            record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
        }
        logger.info("orderNo is :" + orderId);
        transactionRecordService.updateRecharge(record.getUserId(), actualPrice, orderNo, record.getRecordStatus(),orderId);
        logger.info("????????????");
    }

    @Override
    public Map<String,Object> payOrder(String returnUrl, BigDecimal amount, String code, String userId) throws UnsupportedEncodingException {
        Map<String,Object> map = new HashMap<String, Object>();
        User user = userRepository.findByUserId(userId);
        if (null == user) {
            throw new CashierException("???????????????");
        }
        String s = redisTemplate.opsForValue().get(Code.Redis.TBL_PAYMENT_CONFIGURATION_KEY);
        TblPaymentConfiguration configuration = new TblPaymentConfiguration();
        if (StringUtils.isEmpty(s)){
            configuration = tblPaymentConfigurationRepository.findByCode(code);

        }else{
            List<TblPaymentConfiguration> tblPaymentConfigurationList = JSON.parseArray(s, TblPaymentConfiguration.class);
            tblPaymentConfigurationList = tblPaymentConfigurationList.stream().filter(e -> e.getCode().equals(code)).collect(Collectors.toList());
            if (!tblPaymentConfigurationList.isEmpty()){
                configuration = tblPaymentConfigurationList.get(0);
            }
        }
        if (amount.compareTo(configuration.getMinAmount()) == -1 || amount.compareTo(configuration.getMaxAmount()) == 1){
            throw new CashierException("?????????????????????????????????");
        }
        String realname = user.getRealname();
        if (Strings.isBlank(user.getRealname())) {
            realname = user.getPhone().substring(7,11);
        }
        List<TblPayApiConfiguration> configurations = new ArrayList<TblPayApiConfiguration>();
        String payapi = redisTemplate.opsForValue().get(Code.Redis.TBL_PAYAPI_CONFIGURATION_KEY);
        if (StringUtils.isEmpty(payapi)){
            configurations = tblPayApiConfigurationRepository.findAll();
            redisTemplate.opsForValue().set(Code.Redis.TBL_PAYAPI_CONFIGURATION_KEY,JSON.toJSONString(configurations));
        }else{
            configurations = JSON.parseArray(payapi,TblPayApiConfiguration.class);
        }
        //????????????????????????????????????????????????????????????
        Optional<TblPayApiConfiguration> max = configurations.stream().filter(e -> e.getCode().equals(code)).max(Comparator.comparing(TblPayApiConfiguration::getWeight));
        if (max.isPresent()){
            TblPayApiConfiguration tblPayApiConfiguration = max.get();
            String requestUrl = tblPayApiConfiguration.getRequestUrl();
            String appId = tblPayApiConfiguration.getAppId();
            Long productId = tblPayApiConfiguration.getProductId();
            String notifyUrl = tblPayApiConfiguration.getNotifyUrl();
            //????????????ip
            notifyUrl = returnUrl+notifyUrl;
            String key = tblPayApiConfiguration.getKey();

            //?????????????????????????????????
            Long orderNo = transactionRecordService.insertRecord(Long.valueOf(userId), amount,Code.Trade.RECHARGE, Code.Trade.RECHARGEING);
            //??????????????????
            BigDecimal realityAmount = tblChargeOrderService.insertChargeRecord(user, orderNo, code, Code.Trade.RECHARGE, amount, configuration);
            TransactionRecord record = transactionRecordService.findByRecordNo(orderNo);
            TblChargeOrder tblChargeOrder = tblChargeOrderService.findByRecordNo(orderNo);
            if (null == record || null == tblChargeOrder){
                return null;
            }
            StringBuffer signStr = new StringBuffer();
            signStr.append("amount=").append(String.valueOf(realityAmount))
                    .append("&appId=").append(appId)
                    .append("&notifyUrl=").append(notifyUrl)
                    .append("&orderId=").append(orderNo.toString())
                    .append("&productId=").append(productId)
                    .append("&userId=").append(userId)
                    .append("&key=").append(key);
            logger.info("orderNo-->"+orderNo);
            logger.info("?????????-->" + signStr.toString());
            logger.info("??????????????????-->" + Hashing.md5().hashBytes(signStr.toString().getBytes()).toString().toUpperCase());

            RequestBody body = new FormBody.Builder()
                    .add("amount", String.valueOf(realityAmount))
                    .add("appId", appId)
                    .add("orderId", orderNo.toString())
                    .add("productId", String.valueOf(productId))
                    .add("notifyUrl", notifyUrl)
                    .add("userId", userId)
                    .add("returnUrl",returnUrl)
                    .add("userName", URLEncoder.encode(realname, "utf-8"))
                    .add("sign", Hashing.md5().hashBytes(signStr.toString().getBytes()).toString().toUpperCase())
                    .build();

            try (Response response = okHttpClientUtils.requestData(requestUrl, body)) {
                String result = response.body().string();
                logger.info("receive rechargeMsg:" + result);
                RechargeEntity rechargeEntity = JSON.parseObject(result,RechargeEntity.class);
                if (rechargeEntity.getCode().equals(0)){
                    record.setThirdPartyRecordNo(rechargeEntity.getOrderId());
                    tblChargeOrder.setThirdPartyRecordNo(rechargeEntity.getOrderId());
                    tblChargeOrder.setOpeningBank(rechargeEntity.getBankName() == null ? "" : rechargeEntity.getBankName());
                    tblChargeOrder.setAccountNo(rechargeEntity.getBankNo() == null ? "" : rechargeEntity.getBankNo());
                    map.put("url",rechargeEntity.getQrCode());
                    map.put("recordNo",String.valueOf(tblChargeOrder.getRecordNo()));
                    map.put("price",realityAmount);
                    map.put("bankName",rechargeEntity.getBankName());
                    map.put("BankNo",rechargeEntity.getBankNo());
                    map.put("bankOwner",rechargeEntity.getBankOwner());
                }else{
                    record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                    tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                }
            } catch (IOException e) {
                record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                logger.error("?????????????????????????????????");
                logger.error(e.getMessage());
                throw new CashierException("????????????????????????");
            }finally {
                transactionRecordService.updateRecord(String.valueOf(orderNo),record);
                tblChargeOrderService.updateChargeRecord(tblChargeOrder);
            }

        }
        return map;

    }

    @Override
    public String queryOrder(Long recordNo) throws UnsupportedEncodingException {
        String msg = "";
        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(recordNo));
        TblChargeOrder tblChargeOrder = tblChargeOrderService.findByRecordNo(recordNo);
        if (null == tblChargeOrder) {
            executeErrorMsg("??????????????????", null);
            return "??????????????????";
        }
        if (tblChargeOrder.getRecordStatus().equals("2")) {
            msg = "?????????????????????";
            logger.info("?????????????????????"+tblChargeOrder.getRecordNo());
            return msg;
        }
        List<TblPayApiConfiguration> configurations = new ArrayList<TblPayApiConfiguration>();
        String payapi = redisTemplate.opsForValue().get(Code.Redis.TBL_PAYAPI_CONFIGURATION_KEY);
        if (StringUtils.isEmpty(payapi)){
            configurations = tblPayApiConfigurationRepository.findAll();
            redisTemplate.opsForValue().set(Code.Redis.TBL_PAYAPI_CONFIGURATION_KEY,JSON.toJSONString(configurations));
        }else{
            configurations = JSON.parseArray(payapi,TblPayApiConfiguration.class);
        }
        Optional<TblPayApiConfiguration> max = configurations.stream().filter(e -> e.getCode().equals(tblChargeOrder.getCode())).max(Comparator.comparing(TblPayApiConfiguration::getWeight));
        if (max.isPresent()) {
            TblPayApiConfiguration tblPayApiConfiguration = max.get();
            String queryUrl = tblPayApiConfiguration.getQueryUrl();
            String appId = tblPayApiConfiguration.getAppId();
            String key = tblPayApiConfiguration.getKey();

            StringBuffer signStr = new StringBuffer();
            signStr.append("appId=").append(appId)
                    .append("&orderId=").append(tblChargeOrder.getThirdPartyRecordNo())
                    .append("&key=").append(key);
            logger.info("?????????-->" + signStr.toString());
            logger.info("??????????????????-->" + Hashing.md5().hashBytes(signStr.toString().getBytes()).toString().toUpperCase());

            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBilder = HttpUrl.parse(queryUrl).newBuilder();
            urlBilder.addQueryParameter("appId",appId);
            urlBilder.addQueryParameter("orderId",tblChargeOrder.getThirdPartyRecordNo());
            urlBilder.addQueryParameter("sign",Hashing.md5().hashBytes(signStr.toString().getBytes()).toString().toUpperCase());

            Request request = new Request.Builder().url(urlBilder.build()).build();
            client.newBuilder().connectTimeout(30,TimeUnit.SECONDS);
            try (Response response = client.newCall(request).execute()) {
                String result = response.body().string();
                logger.info("receive rechargeMsg:" + result);
                PaychargeEntity paychargeEntity = JSON.parseObject(result,PaychargeEntity.class);
                if (Strings.isBlank(paychargeEntity.getMchOrderNo()) || Strings.isBlank(paychargeEntity.getAmount()) || Strings.isBlank(paychargeEntity.getCode()) || Strings.isBlank(paychargeEntity.getOrderId())) {
                    executeErrorMsg("??????????????????", result);
                }
                String code = paychargeEntity.getCode();
                String status = paychargeEntity.getStatus();
                String remark = paychargeEntity.getRemark();
                if (code.equals("0")) {
                    if (status.equals("1")){
                        msg = "??????????????????????????????????????????????????????????????????";
                    }if (status.equals("3")){
                        record.setRecordStatus(Code.Trade.RECHARGE_SUCCESS);
                        tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_SUCCESS);
                        msg = "????????????";
                    }else if (status.equals("5")){
                        //??????
                        record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                        tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                        msg = "??????????????????????????????????????????????????????????????????";
                    }
                } else {
                    record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                    tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                    msg = remark;
                }
                logger.info("orderNo is :" + recordNo);
            } catch (IOException e) {
                record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_FAIL);
                logger.error("?????????????????????????????????");
                logger.error(e.getMessage());
                throw new CashierException("????????????????????????");
            }finally {
                transactionRecordService.updateRechargeOrder(tblChargeOrder.getUserId(), record, tblChargeOrder);
            }
        }
        return msg;
    }

    @Override
    public void updateChargeOrderStatus(CashierNewBody body) {
        logger.info("receive thirdParty msg:" + body.toString());
        String orderNo = body.getOutTradeNo();
        String amount = body.getAmount();
        String code = body.getCode();
        String orderId = body.getOrderId();
        if (Strings.isBlank(orderNo) || Strings.isBlank(amount) || Strings.isBlank(code) || Strings.isBlank(orderId)) {
            executeErrorMsg("??????????????????", body);
        }
        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(orderNo));
        TblChargeOrder tblChargeOrder = tblChargeOrderService.findByRecordNo(Long.valueOf(orderNo));
        if (null == tblChargeOrder) {
            executeErrorMsg("??????????????????", body);
        }

        if (tblChargeOrder.getRecordStatus().equals("2")) {
            logger.info("?????????????????????");
            logger.info("response msg is:" + body.toString());
            return;
        }

        if (code.equals("0")) {
            if (tblChargeOrder.getRecordStatus().equals("7")){
                logger.info("?????????????????????????????????orderNo???"+orderNo);
                tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_AFTER_SUCCESS);
                record.setRecordStatus(Code.Trade.RECHARGE_AFTER_SUCCESS);
            }else{
                tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_SUCCESS);
                record.setRecordStatus(Code.Trade.RECHARGE_SUCCESS);
            }
        } else if (code.equals("1")) {
            record.setRecordStatus(Code.Trade.RECHARGEING);
            tblChargeOrder.setRecordStatus(Code.Trade.RECHARGEING);
        } else {
            record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
            tblChargeOrder.setRecordStatus(Code.Trade.RECHARGE_FAIL);
        }
        logger.info("orderNo is :" + orderId);
        transactionRecordService.updateRechargeOrder(tblChargeOrder.getUserId(), record, tblChargeOrder);
        logger.info("????????????");
    }

//    @Override
//    public void updateRecordStatus(WithdrawalEntity body) {
//        logger.info("receive thirdParty msg:" + body.toString());
//        String orderNo = body.getOutTradeNo();
//        String amount = body.getAmount();
//        String code = body.getCode();
//        String orderId = body.getOrderId();
//        if (Strings.isBlank(orderNo) || Strings.isBlank(amount) || Strings.isBlank(code) || Strings.isBlank(orderId)) {
//            executeErrorMsg("??????????????????", body);
//        }
//        TransactionRecord record = transactionRecordRepository.findByRecordNo(Long.valueOf(orderNo));
//        if (null == record) {
//            executeErrorMsg("??????????????????", body);
//        }
//        BigDecimal actualPrice = new BigDecimal(amount);
//
//        if (record.getRecordStatus().equals("2")) {
//            executeErrorMsg("??????????????????????????????????????????", body);
//        }
//
//        if (code.equals("0")) {
//            record.setRecordStatus(Code.Trade.RECHARGE_SUCCESS);
//        } else if (code.equals("1")) {
//            record.setRecordStatus(Code.Trade.RECHARGEING);
//        } else {
//            record.setRecordStatus(Code.Trade.RECHARGE_FAIL);
//        }
//        logger.info("orderNo is :" + orderId);
//        transactionRecordService.updateRecharge(record.getUserId(), actualPrice, orderNo, record.getRecordStatus(),orderId);
//        logger.info("????????????");
//    }

    public void executeErrorMsg(String message, Object body) {
        logger.error(message);
        logger.error("response msg is:" + body.toString());
        throw new CashierException(message);
    }
}
