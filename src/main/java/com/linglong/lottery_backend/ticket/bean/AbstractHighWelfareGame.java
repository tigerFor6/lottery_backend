package com.linglong.lottery_backend.ticket.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.bean.ElevenChoosefiveDetail;
import com.linglong.lottery_backend.order.service.bean.Shx115Details;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 高频福彩
 * SHX115
 */
public abstract class AbstractHighWelfareGame extends AbstractGame{

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    //玩法拆分
    public List<GameBean> assemblyHighWelfareGameBean(AbstractHighWelfareGame awg, Order order) {
        List<GameBean> result = new LinkedList<>();
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        OrderDetails orderDetails = orderDetailsRepository.findByOrderId(order.getOrderId()).get(0);

        List<ElevenChoosefiveDetail> elevenChoosefiveDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), ElevenChoosefiveDetail.class);

        order.setBetTimes(orderDetailsInfo.getMultiple());
        int detailsSize = elevenChoosefiveDetails.size();
        List<ElevenChoosefiveDetail> remove = new ArrayList<>();
        for(int i = 0 ; i < detailsSize; i ++){
            ElevenChoosefiveDetail detail = elevenChoosefiveDetails.get(i);
            String lotteryType = detail.getElevenChoosefiveType();

            String[] lotteryTypes = lotteryType.split(CommonConstant.COMMON_DASH_STR);

            int playType = Integer.valueOf(lotteryTypes[1]).intValue();
            int numbersCount = Integer.valueOf(lotteryTypes[2]).intValue();
            int gameType = Integer.valueOf(lotteryTypes[3]).intValue();

            //胆拖情况
            if(playType != 2) {
                String[] detailNumbers = detail.getNumber().split(CommonConstant.COMMA_SPLIT_STR + "|" + CommonConstant.SEMICOLON_SPLIT_STR);
                int count = detailNumbers.length;
                if(count > numbersCount) {
                    //前二、前三直选为定位复式，前一不是
                    if(gameType == 1) {
                        if(numbersCount != 1) {
                            lotteryTypes[1] = "4";

                        }else {
                            //直选1，复式拆票
                            lotteryTypes[1] = "1";
                            for (int i1 = 0; i1 < detailNumbers.length; i1++) {
                                ElevenChoosefiveDetail doubleDetail = new ElevenChoosefiveDetail();
                                doubleDetail.setElevenChoosefiveType(Joiner
                                        .on(CommonConstant.COMMON_DASH_STR)
                                        .join(lotteryTypes));
                                doubleDetail.setNumber(detailNumbers[i1]);
                                doubleDetail.setBetNumber("1");
                                doubleDetail.setOrderFee(2);
                                elevenChoosefiveDetails.add(doubleDetail);
                            }
                            remove.add(detail);
                        }

                    }else {
                        //任选8复式
                        if(lotteryTypes[2].equals("8") && lotteryTypes[3].equals("3")) {
                            String[] loopNumbers = FcSsqGame.loop(detail.getNumber().split(CommonConstant.COMMA_SPLIT_STR), 8);
                            for (int i1 = 0; i1 < loopNumbers.length; i1++) {
                                ElevenChoosefiveDetail doubleDetail = new ElevenChoosefiveDetail();
                                doubleDetail.setElevenChoosefiveType(Joiner
                                        .on(CommonConstant.COMMON_DASH_STR)
                                        .join(lotteryTypes));
                                doubleDetail.setNumber(loopNumbers[i1]);
                                doubleDetail.setBetNumber("1");
                                doubleDetail.setOrderFee(2);

                                elevenChoosefiveDetails.add(doubleDetail);
                            }
                            remove.add(detail);
                        }else {
                            lotteryTypes[1] = "3";
                        }

                    }

                }

            }

            if(!remove.isEmpty()){
                elevenChoosefiveDetails.removeAll(remove);
                remove.clear();
            }else {
                detail.setElevenChoosefiveType(Joiner
                        .on(CommonConstant.COMMON_DASH_STR)
                        .join(lotteryTypes));

                elevenChoosefiveDetails.set(i,detail);
            }

        }

        Map<String, List<ElevenChoosefiveDetail>> group =
                elevenChoosefiveDetails.stream().collect(Collectors.groupingBy(ElevenChoosefiveDetail::getElevenChoosefiveType));

        int splitNumberCount = 5;
        for(String typeGroup : group.keySet()){
            List<ElevenChoosefiveDetail> detailList = group.get(typeGroup);
            //4-1-1-1
            String[] typeGroups = typeGroup.split(CommonConstant.COMMON_DASH_STR);

            int type = Integer.valueOf(typeGroups[1]).intValue();
            int numbersCount = Integer.valueOf(typeGroups[2]).intValue();
            if(numbersCount > 5) {
                splitNumberCount = 3;
            }

            //单式
            if(type == 1) {
                List<ElevenChoosefiveDetail> limitDetails;
                int limit = 1;
                do{
                    try{
                        limitDetails = detailList.subList((limit -1) * splitNumberCount, limit * splitNumberCount);
                    }catch (Exception e) {
                        limitDetails = detailList.subList((limit -1) * splitNumberCount, detailList.size());
                    }
                    if(limitDetails.isEmpty())
                        break;

                    StringBuffer lotteryNumber = new StringBuffer();
                    limitDetails.forEach(limitDetail -> {
                        lotteryNumber.append(limitDetail.getNumber())
                                .append(CommonConstant.SPACE_SPLIT_STR);
                    });

                    int betNumber = 0;
                    for (int i = 0; i < limitDetails.size(); i++) {
                        betNumber += Integer.valueOf(limitDetails.get(i).getBetNumber()).intValue();
                    }

                    GameBean gameBean = new GameBean(
                            betNumber, WordConstant.SINGLE,
                            Joiner.on(CommonConstant.PERCENT_SPLIT_STR)
                                    .join(orderDetails.getIssue(),lotteryNumber.deleteCharAt(lotteryNumber.length()-1).toString()),
                            PRICE_PER_STAKE.multiply(BigDecimal.valueOf(limitDetails.size())),
                            (numbersCount + "-" + typeGroups[3]),
                            order.getEndTime(),Integer.valueOf(orderDetails.getIssue()));
                    gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));

                    result.add(gameBean);

                    limit ++;
                    if(limitDetails.size() != splitNumberCount)
                        break;
                }while (true);

            }else {

                String playType = null;
                if(type == 2) {
                    //胆拖玩法
                    playType = WordConstant.DANTUO;

                }else if(type == 3) {
                    //复式玩法
                    playType = WordConstant.MULTIPLE;

                }else if(type == 4) {
                    //定位复式玩法
                    playType = WordConstant.DIRECT;
                }

                for (int i = 0; i < detailList.size(); i++) {
                    ElevenChoosefiveDetail elevenChoosefiveDetail = detailList.get(i);
                    GameBean gameBean = new GameBean(
                            Integer.valueOf(elevenChoosefiveDetail.getBetNumber()), playType,
                            Joiner.on(CommonConstant.PERCENT_SPLIT_STR)
                                    .join(orderDetails.getIssue(),elevenChoosefiveDetail.getNumber()),
                            PRICE_PER_STAKE,
                            (numbersCount + "-" + typeGroups[3]),
                            order.getEndTime(),Integer.valueOf(orderDetails.getIssue()));
                    gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));

                    result.add(gameBean);
                }

            }


        }

        return result;
    }

}
