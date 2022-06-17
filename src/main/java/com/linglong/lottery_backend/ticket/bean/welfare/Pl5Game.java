package com.linglong.lottery_backend.ticket.bean.welfare;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.model.order_model.PlayType;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.bean.RankFiveDetails;
import com.linglong.lottery_backend.ticket.bean.AbstractPl5Game;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component(value = "PL5Game")
public class Pl5Game extends AbstractPl5Game {

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    private BigDecimal singePrice = new BigDecimal(2);

    private int size = 5;
    @Override
    public Game getGame() {
        return GameEnum.PL5.getGame();
    }

    @Override
    public List<GameBean> assemblyGameBean(AbstractPl5Game awg, Order order) {
        List<GameBean> result = new LinkedList<>();
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        OrderDetails orderDetails = orderDetailsRepository.findByOrderId(order.getOrderId()).get(0);
        order.setBetTimes(orderDetailsInfo.getMultiple());

        List<RankFiveDetails> rankFiveDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), RankFiveDetails.class);
        Map<String, List<RankFiveDetails>> group = rankFiveDetails.stream().collect(Collectors.groupingBy(RankFiveDetails::getRankFiveType));
        group.forEach((k,v)->{
            //拆分list
            if(k.equals(PlayType.PL5MULTIPLE.getType())){
                size = 1;
            }
            List<List<RankFiveDetails>> parts = Lists.partition(v, size);
            parts.forEach(e->{
                GameBean gameBean = new GameBean();
                gameBean.setExtra(k);
                if (k.equals(PlayType.PL5SINGLE.getType())){
                    gameBean.setPlayType(WordConstant.SINGLE);
                }else if(k.equals(PlayType.PL5MULTIPLE.getType())){
                    gameBean.setPlayType(WordConstant.MULTIPLE);
                }
                int betNumber = e.stream().mapToInt(RankFiveDetails::getIntBetNumber).sum();
                gameBean.setEndTime(order.getEndTime());
                gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));
                gameBean.setPeriod(Integer.parseInt(orderDetails.getIssue()));
                gameBean.setLotteryNumber(Joiner.on(CommonConstant.SPACE_SPLIT_STR).join(e.stream().map(m -> m.getNumber()).collect(Collectors.toList())));
                gameBean.setPrice(singePrice.multiply(BigDecimal.valueOf(betNumber)).multiply(new BigDecimal(orderDetailsInfo.getMultiple())));
                gameBean.setBetNumber(betNumber);
                result.add(gameBean);
            });
        });
        return result;
    }

    @Override
    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {

        List<GameBean> splitGameBean = new ArrayList<>();
        userGameBeanList.forEach(bean -> {
            while ( bean.getBetTimes().divide(new BigDecimal(50)).doubleValue() > 1){
                GameBean gameBean = bean.cloneGameBean();
                gameBean.setBetTimes(new BigDecimal(50));
                bean.setBetTimes(bean.getBetTimes().subtract(new BigDecimal(50)));
                gameBean.setPrice(singePrice.multiply(new BigDecimal(gameBean.getBetNumber()).multiply(gameBean.getBetTimes())));
                bean.setPrice(bean.getPrice().subtract(gameBean.getPrice()));
                splitGameBean.add(gameBean);
            }
            splitGameBean.add(bean);
        });

        List<GameBean> splitFinalGameBean = new ArrayList<>();
        splitGameBean.forEach(bean -> {

            BigDecimal limitPrise = new BigDecimal(20000);

            while(bean.getPrice().doubleValue() > limitPrise.doubleValue()) {
                GameBean gameBean = bean.cloneGameBean();
                BigDecimal betDecimal = new BigDecimal(bean.getBetNumber());
                BigDecimal newBetTimes = limitPrise.divide(singePrice, 0 , BigDecimal.ROUND_DOWN).divide(betDecimal, 0,BigDecimal.ROUND_DOWN);
                if(newBetTimes.doubleValue() <= 0) {
                    splitFinalGameBean.addAll(splitMaxPrice(bean));
                    bean = null;
                    break;
                }
                gameBean.setBetTimes(newBetTimes);
                gameBean.setPrice(newBetTimes.multiply(betDecimal).multiply(singePrice));
                splitFinalGameBean.add(gameBean);

                bean.setBetTimes(bean.getBetTimes().subtract(newBetTimes));
                bean.setPrice(bean.getPrice().subtract(gameBean.getPrice()));
            }
            if(bean != null) {
                splitFinalGameBean.add(bean);
            }
        });

        return splitFinalGameBean;
    }

    private List<GameBean> splitMaxPrice(List<GameBean> splitGameBean) {
        List<GameBean> splitFinalGameBean = new ArrayList<>();
        splitGameBean.forEach(bean -> {

            BigDecimal limitPrise = new BigDecimal(20000);

            while(bean.getPrice().doubleValue() > limitPrise.doubleValue()) {
                GameBean gameBean = bean.cloneGameBean();
                BigDecimal betDecimal = new BigDecimal(bean.getBetNumber());
                BigDecimal newBetTimes = limitPrise.divide(singePrice, 0 , BigDecimal.ROUND_DOWN).divide(betDecimal, 0,BigDecimal.ROUND_DOWN);
                if(newBetTimes.doubleValue() <= 0) {
                    splitFinalGameBean.addAll(splitMaxPrice(bean));
                    bean = null;
                    break;
                }
                gameBean.setBetTimes(newBetTimes);
                gameBean.setPrice(newBetTimes.multiply(betDecimal).multiply(singePrice));
                splitFinalGameBean.add(gameBean);

                bean.setBetTimes(bean.getBetTimes().subtract(newBetTimes));
                bean.setPrice(bean.getPrice().subtract(gameBean.getPrice()));
            }
            if(bean != null) {
                splitFinalGameBean.add(bean);
            }
        });
        return splitFinalGameBean;
    }

    private List<GameBean> splitMaxPrice(GameBean gameBean) {
        String[] lotteryNumber = gameBean.getLotteryNumber().split(CommonConstant.COMMON_AT_STR);

        List<String> balls = new LinkedList<>(Arrays.asList(lotteryNumber[0].split(CommonConstant.SEMICOLON_SPLIT_STR)));

        int maxIndex = 0;
        int lengthMax = 0;
        for (int i = 0; i < balls.size(); i++) {
            int ballLegnth = balls.get(i).split(CommonConstant.COMMA_SPLIT_STR).length;
            if(ballLegnth > lengthMax) {
                lengthMax = ballLegnth;
                maxIndex = i;
            }

        }

        String[] maxBall = balls.remove(maxIndex).split(CommonConstant.COMMA_SPLIT_STR);
        List<GameBean> gameBeans = new ArrayList<>();
        for (int i = 0; i < maxBall.length; i++) {
            GameBean newGameBean = gameBean.cloneGameBean();
            balls.add(maxIndex, maxBall[i]);

            StringBuffer newLotteryNumber = new StringBuffer();
            int betNumber = 1;
            for (int i1 = 0; i1 < balls.size(); i1++) {
                String newBall = balls.get(i1);
                newLotteryNumber.append(newBall)
                        .append(CommonConstant.SEMICOLON_SPLIT_STR);
                betNumber *= newBall.split(CommonConstant.COMMA_SPLIT_STR).length;
            }

            newGameBean.setBetNumber(betNumber);
            newGameBean.setLotteryNumber(newLotteryNumber.deleteCharAt(newLotteryNumber.length()-1).toString());
            newGameBean.setPrice(singePrice.multiply(BigDecimal.valueOf(betNumber)).multiply(newGameBean.getBetTimes()));
            gameBeans.add(newGameBean);
            balls.remove(maxIndex);
        }

        return splitMaxPrice(gameBeans);
    }
}
