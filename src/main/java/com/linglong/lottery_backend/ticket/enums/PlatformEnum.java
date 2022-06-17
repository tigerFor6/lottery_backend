package com.linglong.lottery_backend.ticket.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.IOrderService;
import com.linglong.lottery_backend.prize.entity.BettingTicketDigital;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.prize.repository.TblBettingTicketDetailsRepository;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Ticket;
import com.linglong.lottery_backend.ticket.cache.PlatformCache;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.remote.aoli.Message;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketDigitalRepository;
import com.linglong.lottery_backend.ticket.service.TblBettingTicketService;
import com.linglong.lottery_backend.utils.IdWorker;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ynght on 2019-04-26
 */
public enum PlatformEnum {

    BJGONGCAI("bjGongCai") {
        @Override
        public String getDoBetUrl() {
            String platformUrl = getPlatform().getPlatformUrl();
            return platformUrl;
        }

        @Override
        public List<TblBettingTicketDetails> sendTicket2Prize(List<Ticket> tickets,
                                                              TblBettingTicketService bettingTicketService,
                                                              OrderDetailsRepository orderDetailsRepository, IdWorker idWorker) {
            if (null == tickets || tickets.isEmpty()) {
                return Lists.newArrayList();
            }
            List<TblBettingTicketDetails> result = Lists.newArrayList();
            Map<Long, List<OrderDetails>> orderMappingDetails = Maps.newHashMap();
            for (Ticket ticket : tickets) {
                if (2 != ticket.getStatus()) {
                    continue;
                }
                if(StringUtils.isEmpty(ticket.getSp()))
                    continue;

                BettingTicket betting = bettingTicketService.findByTicketId(Long.parseLong(ticket.getTicketId()));
                if (null == betting) {
                    continue;
                }

                String[] sp = ticket.getSp().split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SEMICOLON_SPLIT_STR);
                String[] split = betting.getLotteryNumber().split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
                String[] matches = split[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
                if (matches.length != sp.length) {
                    continue;
                }
                if (orderMappingDetails.get(betting.getOrderId()) == null) {
                    orderMappingDetails.put(betting.getOrderId(), orderDetailsRepository.findByOrderId(betting.getOrderId()));
                }

                List<OrderDetails> matchs = orderMappingDetails.get(betting.getOrderId());
                Map<String, OrderDetails> mapOne = Maps.newHashMap();
                for (int i = 0; i < matchs.size(); i++) {
                    OrderDetails orderDetails = matchs.get(i);
                    mapOne.put(orderDetails.getIssue(), orderDetails);
                }

                for (int i = 0; i < matches.length; i++) {
                    String[] split1 = matches[i].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
                    OrderDetails orderDetails = mapOne.get(split1[0]);
                    String[] s1 = sp[i].split(CommonConstant.COMMA_SPLIT_STR);
                    //String[] s1 = sp[i].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
                    boolean b = split1[1].indexOf(CommonConstant.POUND_SPLIT_STR) >= 0;
                    String playCode = null;
                    String oddsCode = null;
                    if (b) {
                        String[] split2 = split1[1].split(CommonConstant.COMMON_WAVE_STR);
                        String[] split3 = split2[1].split(CommonConstant.POUND_SPLIT_STR);
                        // String[] split4 = s1[1].split(CommonConstant.COMMON_PLUS_STR);
                        //String[] split4 = s1[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
                        for (int j = 0; j < split3.length; j++) {
                            playCode = split2[0];
                            oddsCode = split3[j];
                            String[] split5 =
                                    s1[j].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR)[1].split(CommonConstant.POUND_SPLIT_STR);


                            TblBettingTicketDetails detail = new TblBettingTicketDetails(idWorker.nextId(),
                                    Long.parseLong(ticket.getTicketId()), betting.getOrderId(), orderDetails.getMatchId(), split1[0],
                                    orderDetails.getSn(), playCode, oddsCode, new BigDecimal(split5[0]),betting.getExtra());
                            result.add(detail);
                        }
                    } else {
                        String[] temp = split1[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_WAVE_STR);
                        playCode = temp[0];
                        //可以拿s1走一个映射取
                        oddsCode = temp[1];
                        String[] split2 = s1[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR)[1].split(CommonConstant.POUND_SPLIT_STR);
                        TblBettingTicketDetails detail = new TblBettingTicketDetails(idWorker.nextId(),
                                Long.parseLong(ticket.getTicketId()), betting.getOrderId(), orderDetails.getMatchId(), split1[0],
                                orderDetails.getSn(), playCode, oddsCode, new BigDecimal(split2[0]),betting.getExtra());
                        result.add(detail);
                    }
                }
            }
            return result;
        }

        @Override
        public List<TblBettingTicketDetails> sendTicket2PrizeByAOLI(List<Message> tickets, TblBettingTicketService bettingTicketService, TblBettingTicketDigitalRepository tblBettingTicketDigitalRepository,OrderDetailsRepository orderDetailsRepository, IdWorker idWorker) {
            return null;
        }
    },


    AOLI("aoli") {
        @Override
        public String getDoBetUrl() {
            String platformUrl = getPlatform().getPlatformUrl();
            return platformUrl;
        }

        @Override
        public List<TblBettingTicketDetails> sendTicket2Prize(List<Ticket> tickets, TblBettingTicketService bettingTicketService, OrderDetailsRepository orderDetailsRepository, IdWorker idWorker) {
            return null;
        }

        @Override
        public List<TblBettingTicketDetails> sendTicket2PrizeByAOLI(List<Message> tickets,
                                                                    TblBettingTicketService bettingTicketService, TblBettingTicketDigitalRepository tblBettingTicketDigitalRepository,
                                                                    OrderDetailsRepository orderDetailsRepository, IdWorker idWorker) {

            List<TblBettingTicketDetails> result = Lists.newArrayList();
            for (int i = 0; i < tickets.size(); i++) {
                Message message = tickets.get(i);
                if(message.getOdds() == null)
                    continue;

                Integer gameType = message.getGameType();
                JSONObject oddsJSON = JSONObject.parseObject(message.getOdds());
                //1足球，2篮球
                if(gameType.equals(AoliEnum.JCZQ.getCode())){
                    result.addAll(JCZQTicketDetails(oddsJSON,message,bettingTicketService,orderDetailsRepository,idWorker));

                }else if (gameType.equals(AoliEnum.JCLQ.getCode())){
                    result.addAll(JCLQTicketDetails(oddsJSON,message,bettingTicketService,orderDetailsRepository,idWorker));

                }else if (gameType.equals(AoliEnum.SHX115.getCode())) {
                    result.addAll(Shx115TicketDetails(message,bettingTicketService,tblBettingTicketDigitalRepository,idWorker));
                }
            }
            return result;
        }
        /**
         * 竞猜足球
         * @return
         */
        public List<TblBettingTicketDetails> JCZQTicketDetails(JSONObject oddsJSON,Message message,
                                                               TblBettingTicketService bettingTicketService,
                                                               OrderDetailsRepository orderDetailsRepository, IdWorker idWorker){
            JSONObject spMapJSON = oddsJSON.getJSONObject("spMap");
            JSONArray matchNumberArray = spMapJSON.getJSONArray("matchNumber");
            return createTblBettingTicketDetails(matchNumberArray, message, bettingTicketService, orderDetailsRepository, idWorker);
        }

        /**
         * 竞猜篮球
         * @return
         */
        public List<TblBettingTicketDetails> JCLQTicketDetails(JSONObject oddsJSON,Message message,
                                                               TblBettingTicketService bettingTicketService,
                                                               OrderDetailsRepository orderDetailsRepository, IdWorker idWorker){
            JSONArray itemsOddsArray = oddsJSON.getJSONArray("itemsOdds");
            return createTblBettingTicketDetails(itemsOddsArray, message, bettingTicketService, orderDetailsRepository, idWorker);
        }

        public List<TblBettingTicketDetails> createTblBettingTicketDetails(JSONArray matchNumberArray, Message message,
                                                                           TblBettingTicketService bettingTicketService,
                                                                           OrderDetailsRepository orderDetailsRepository, IdWorker idWorker){

            BettingTicket betting = bettingTicketService.findByTicketId(Long.parseLong(message.getTicketId()));
            List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(betting.getOrderId());
//            Order order = orderService.findByOrderId(betting.getOrderId());
//            JSONObject detailsJSON = JSONObject.parseObject(order.getOrderDetails());
//            JSONArray detailsMatchContent = detailsJSON.getJSONArray("matchs");

            List<TblBettingTicketDetails> result = Lists.newArrayList();
            Pattern sqlitLotterNumberRegex =Pattern.compile("(.*?):(.*?)~(.*?)\\s");
            Matcher lotterNumberRegexMatcher = sqlitLotterNumberRegex.matcher(betting.getLotteryNumber()+" ");

            while(lotterNumberRegexMatcher.find()){
                String matchIssue = lotterNumberRegexMatcher.group(1);
                String playCode = lotterNumberRegexMatcher.group(2);

                OrderDetails match = null;
                for (int i = 0; i < orderDetailsList.size(); i++) {
                    if(orderDetailsList.get(i).getIssue().equals(matchIssue)) {
                        match = orderDetailsList.get(i);
                        break;
                    }
                }

                JSONObject matchDataJSON = null;
                for (int i = 0; i < matchNumberArray.size(); i++) {
                    JSONObject jsonObject = matchNumberArray.getJSONObject(i);
                    if(jsonObject.getString("matchNumber").equals(matchIssue)) {
                        matchDataJSON = jsonObject;
                        break;
                    }
                }
                JSONObject matchDataJSONValue = null;
                if(message.getGameType().equals(1)){
                    matchDataJSONValue = matchDataJSON.getJSONObject("value");

                }else if (message.getGameType().equals(2)){
                    matchDataJSONValue = matchDataJSON.getJSONObject("odds");

                }
                List<String> oddCodeList = new ArrayList<>(matchDataJSONValue.keySet());
                for (int i = 0; i < oddCodeList.size(); i++) {
                    String oddCode = oddCodeList.get(i);
                    String odds = matchDataJSONValue.getString(oddCode);

                    TblBettingTicketDetails detail = new TblBettingTicketDetails(idWorker.nextId(),
                            Long.parseLong(message.getTicketId()), betting.getOrderId(), match.getMatchId(), matchIssue,
                            match.getSn(), playCode, oddCode, new BigDecimal(odds),betting.getExtra());
                    result.add(detail);
                }
            }

            return result;
        }

        /**
         * 陕115出票详情
         * @param message
         * @param bettingTicketService
         * @param tblBettingTicketDigitalRepository
         * @param idWorker
         * @return
         */
        public List<TblBettingTicketDetails> Shx115TicketDetails(Message message, TblBettingTicketService bettingTicketService,
                                                                 TblBettingTicketDigitalRepository tblBettingTicketDigitalRepository, IdWorker idWorker){

            List<TblBettingTicketDetails> tblBettingTicketDetails = new ArrayList<>();

            BettingTicket betting = bettingTicketService.findByTicketId(Long.parseLong(message.getTicketId()));


            String[] lotteryNumbers = betting.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
            String[] balls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
            List<BettingTicketDigital> bettingTicketDigitals = tblBettingTicketDigitalRepository.findByBettingTicketId(betting.getTicketId());

            List<BettingTicketDigital> saveDetails = new ArrayList<>();
            balls_for:
            for (int i = 0; i < balls.length; i++) {
                for (int i1 = 0; i1 < bettingTicketDigitals.size(); i1++) {
                    if(bettingTicketDigitals.get(i1).getBettingGroup().equals(balls[i]) &&
                            bettingTicketDigitals.get(i1).getBettingTicketId().equals(betting.getTicketId())) {
                        continue balls_for;
                    }
                }

                BettingTicketDigital bettingTicketDigital = new BettingTicketDigital();
                bettingTicketDigital.setOrderId(betting.getOrderId());
                bettingTicketDigital.setTicketDigitalId(idWorker.nextId());
                bettingTicketDigital.setBettingTicketId(Long.valueOf(message.getTicketId()));
                bettingTicketDigital.setPeriod(lotteryNumbers[0]);
                bettingTicketDigital.setBettingType(AoliEnum.SHX115.getCode());
                bettingTicketDigital.setPlayType(betting.getPlayType());
                bettingTicketDigital.setBettingGroup(balls[i]);
                bettingTicketDigital.setBettingResult(0);
                bettingTicketDigital.setAftTaxBonus(new BigDecimal(AoliEnum.getCodeByStatus(betting.getExtra())));
                bettingTicketDigital.setPreTaxBonus(bettingTicketDigital.getAftTaxBonus());
                bettingTicketDigital.setMultiple(betting.getTimes());

                saveDetails.add(bettingTicketDigital);
            }
            tblBettingTicketDigitalRepository.saveAll(saveDetails);

            return tblBettingTicketDetails;
        }

    };

    private String platformEn;

    PlatformEnum(String platformEn) {
        this.platformEn = platformEn;
    }

    public String getPlatformEn() {
        return platformEn;
    }


    public Platform getPlatform() {
        return PlatformCache.getPlatform(platformEn);
    }

    public Integer getPlatformId() {
        return getPlatform().getPlatformId();
    }

    public String getPlatformCode() {
        return getPlatform().getPlatformCode();
    }

    public String getPlatformAccount() {
        return getPlatform().getPlatformAccount();
    }

    public String getPlatformKey() {
        return getPlatform().getPlatformKey();
    }

    public String getPlatformPwd() {
        return getPlatform().getPlatformPwd();
    }

    public abstract String getDoBetUrl();

    public String getQueryTicketStatusUrl() {
        throw new AbstractMethodError();
    }

    public static  PlatformEnum getPlatformEnum(String platformEn){
        for (PlatformEnum en : PlatformEnum.values()) {
            if (Objects.equals(platformEn, en.platformEn)) {
                return en;
            }
        }
        return null;
    }


    public abstract List<TblBettingTicketDetails> sendTicket2Prize(List<Ticket> tickets,
                                                                   TblBettingTicketService bettingTicketService,
                                                                   OrderDetailsRepository orderDetailsRepository, IdWorker idWorker);

    public abstract List<TblBettingTicketDetails> sendTicket2PrizeByAOLI(List<Message> tickets,
                                                                         TblBettingTicketService bettingTicketService, TblBettingTicketDigitalRepository tblBettingTicketDigitalRepository,
                                                                         OrderDetailsRepository orderDetailsRepository, IdWorker idWorker);

}
