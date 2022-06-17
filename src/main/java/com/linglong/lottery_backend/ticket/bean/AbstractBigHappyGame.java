package com.linglong.lottery_backend.ticket.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.bean.SuperLottoDetails;
import com.linglong.lottery_backend.ticket.bean.welfare.FcSsqGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 大乐透
 */
@Slf4j
public abstract class AbstractBigHappyGame extends AbstractGame{

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    BigDecimal singePrice = new BigDecimal(2);

    public List<GameBean> assemblyBigHappyGameBean(AbstractBigHappyGame awg, Order order) {
        List<GameBean> result = new LinkedList<>();
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        OrderDetails orderDetails = orderDetailsRepository.findByOrderId(order.getOrderId()).get(0);
        List<SuperLottoDetails> superLottoDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), SuperLottoDetails.class);

        order.setBetTimes(orderDetailsInfo.getMultiple());

        Map<String, List<SuperLottoDetails>> group = superLottoDetails.stream()
                                                                                            .collect(Collectors.groupingBy(SuperLottoDetails::getSuperLottoType));

        group.forEach((betType, details) -> {
            if(orderDetailsInfo.getChasing().equals(Integer.valueOf(1))) {
                singePrice = singePrice.add(BigDecimal.ONE);
            }
            String playType = null;
            if(betType.equals("5-1")) {

                List<List<SuperLottoDetails>> superDetails = new ArrayList<>();
                if(details.size() <= 5) {
                    superDetails.add(details);
                }else {
                    int limit = 0;
                    do {
                        limit += 5;
                        superDetails.add(details.subList(limit - 5,  limit > details.size() ? details.size() : limit));
                    }while (limit < details.size());
                }

                superDetails.forEach(sdetails -> {

                    int betNumber = 0;
                    for (int i = 0; i < sdetails.size(); i++) {
                        betNumber += Integer.valueOf(sdetails.get(i).getBetNumber()).intValue();
                    }

                    StringBuffer ballNumbers = new StringBuffer();
                    sdetails.forEach(e -> {
                        ballNumbers.append(e.getRedNumber()+CommonConstant.COMMON_VERTICAL_STR+e.getBlueNumber());
                        ballNumbers.append(CommonConstant.SPACE_SPLIT_STR);
                    });
                    String lotteryNumber = ballNumbers.deleteCharAt(ballNumbers.length() - 1).toString();

                    GameBean gameBean = new GameBean(
                            betNumber, WordConstant.SINGLE,
                            Joiner.on(CommonConstant.PERCENT_SPLIT_STR)
                                    .join(orderDetails.getIssue(),lotteryNumber),
                            singePrice.multiply(BigDecimal.valueOf(betNumber)).multiply(new BigDecimal(orderDetailsInfo.getMultiple())),
                            orderDetailsInfo.getChasing().toString(),
                            order.getEndTime(),Integer.valueOf(orderDetails.getIssue()));
                    gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));

                    result.add(gameBean);

                });

            }else {
                if (betType.equals("5-2")) {
                    playType = WordConstant.MULTIPLE;

                }else if (betType.equals("5-3")) {
                    playType = WordConstant.DANTUO;

                }

                for (int i = 0; i < details.size(); i++) {
                    SuperLottoDetails detail = details.get(i);
                    int betNumber = Integer.valueOf(detail.getBetNumber()).intValue();
                    String lotteryNumber = detail.getRedNumber()+CommonConstant.COMMON_VERTICAL_STR+detail.getBlueNumber();
                    BigDecimal betMulDecimal = BigDecimal.valueOf(betNumber).multiply(new BigDecimal(orderDetailsInfo.getMultiple()));

                    GameBean gameBean = new GameBean(
                            betNumber, playType,
                            Joiner.on(CommonConstant.PERCENT_SPLIT_STR)
                                    .join(orderDetails.getIssue(),lotteryNumber),
                            betMulDecimal.multiply(singePrice),
                            orderDetailsInfo.getChasing().toString(),
                            order.getEndTime(),Integer.valueOf(orderDetails.getIssue()));
                    gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));

                    result.add(gameBean);
                }

            }

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
                if(bean.getExtra().equals("0")) {
                    BigDecimal singlePrise = new BigDecimal(2);
                    gameBean.setPrice(singlePrise.multiply(new BigDecimal(gameBean.getBetNumber()).multiply(gameBean.getBetTimes())));
                }else {
                    BigDecimal singlePrise = new BigDecimal(3);
                    gameBean.setPrice(singlePrise.multiply(new BigDecimal(gameBean.getBetNumber()).multiply(gameBean.getBetTimes())));
                }
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
//        return splitGameBean;
    }


    /**
     * 大乐透拆分单倍超过2W金额的票
     * @param gameBean
     * @return
     */
    private List<GameBean> splitMaxPrice(GameBean gameBean) {
        String[] lotteryNumber = gameBean.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
        List<String> danBalls;
        String[] balls = lotteryNumber[1].split("\\|");

        String[] redBalls = balls[0].split(CommonConstant.SEMICOLON_SPLIT_STR);
        String[] redBall;
        if(redBalls.length > 1) {
            redBall = redBalls[1].split(CommonConstant.COMMA_SPLIT_STR);
            danBalls = new LinkedList<>(Arrays.asList(redBall[0].split(CommonConstant.COMMA_SPLIT_STR)));
        }else {
            redBall = redBalls[0].split(CommonConstant.COMMA_SPLIT_STR);
            danBalls = new LinkedList<>();
        }

        List<String> redBallList = new LinkedList<>(Arrays.asList(redBall));
        String[] blueBall = balls[1].split(CommonConstant.COMMA_SPLIT_STR);

        return split(redBallList, blueBall,danBalls, gameBean);
    }


    private List<GameBean> split(List<String> redBall, String[] blueBall, List<String> danBalls, GameBean gameBean) {

        String[] loopStr = FcSsqGame.loop(redBall.toArray(new String[redBall.size()]), 5);
        if(singePrice.multiply(new BigDecimal(loopStr.length)).doubleValue() < 20000) {
            List<String> mergeList = new ArrayList<>();
            mergeList.addAll(danBalls);
            mergeList.addAll(redBall);

            List<GameBean> gameBeans = new ArrayList<>();
            if(danBalls.isEmpty()) {
                String[] newRedBall = mergeList.toArray(new String[mergeList.size()]);
                int betNumber = FcSsqGame.loop(newRedBall, 5).length;

                String[] newBlueBalls = FcSsqGame.loop(blueBall, 2);

                for (int i2 = 0; i2 < newBlueBalls.length; i2++) {
                    GameBean newGameBean = gameBean.cloneGameBean();
                    StringBuffer lottery = new StringBuffer(gameBean.getPeriod() +"");
                    lottery.append(CommonConstant.PERCENT_SPLIT_STR)
                            .append(mergeList.toString().replaceAll(" |\\[|\\]", ""))
                            .append(CommonConstant.COMMON_VERTICAL_STR)
                            .append(newBlueBalls[i2])
                            .append(CommonConstant.COMMON_AT_STR)
                            .append(betNumber);
                    newGameBean.setLotteryNumber(lottery.toString());
                    newGameBean.setBetNumber(betNumber);
                    newGameBean.setPrice(singePrice.multiply(new BigDecimal(betNumber).multiply(gameBean.getBetTimes())));
                    newGameBean.setPlayType(WordConstant.MULTIPLE);
                    gameBeans.add(newGameBean);

                }

            }else {
                String[] newBlueBalls = FcSsqGame.loop(blueBall, 2);

                for (int i = 0; i < danBalls.size(); i++) {
                    mergeList.remove(danBalls.get(i));
                    String[] newRedBall = mergeList.toArray(new String[mergeList.size()]);
                    int betNumber = FcSsqGame.loop(newRedBall, 4).length;

                    for (int i2 = 0; i2 < newBlueBalls.length; i2++) {
                        GameBean newGameBean = gameBean.cloneGameBean();

                        StringBuffer lottery = new StringBuffer(gameBean.getPeriod() +"");
                        lottery.append(CommonConstant.PERCENT_SPLIT_STR)
                                .append(danBalls.get(i))
                                .append(CommonConstant.SEMICOLON_SPLIT_STR)
                                .append(mergeList.toString().replaceAll(" |\\[|\\]", ""))
                                .append(CommonConstant.COMMON_VERTICAL_STR)
                                .append(newBlueBalls[i2])
                                .append(CommonConstant.COMMON_AT_STR)
                                .append(betNumber);
                        newGameBean.setLotteryNumber(lottery.toString());
                        newGameBean.setBetNumber(betNumber);
                        newGameBean.setPrice(singePrice.multiply(new BigDecimal(betNumber).multiply(gameBean.getBetTimes())));
                        newGameBean.setPlayType(WordConstant.DANTUO);
                        gameBeans.add(newGameBean);

                    }

                }

                for (int i2 = 0; i2 < newBlueBalls.length; i2++) {
                    GameBean newMultipleGameBean = gameBean.cloneGameBean();
                    StringBuffer lotteryMultiple = new StringBuffer(gameBean.getPeriod() +"");
                    lotteryMultiple.append(CommonConstant.PERCENT_SPLIT_STR)
                            .append(redBall.toString().replaceAll(" |\\[|\\]", ""))
                            .append(CommonConstant.COMMON_VERTICAL_STR)
                            .append(newBlueBalls[i2])
                            .append(CommonConstant.COMMON_AT_STR)
                            .append(loopStr.length);
                    newMultipleGameBean.setLotteryNumber(lotteryMultiple.toString());
                    newMultipleGameBean.setBetNumber(loopStr.length);
                    newMultipleGameBean.setPrice(singePrice.multiply(new BigDecimal(loopStr.length).multiply(gameBean.getBetTimes())));
                    newMultipleGameBean.setPlayType(WordConstant.MULTIPLE);
                    gameBeans.add(newMultipleGameBean);
                }

            }

            return gameBeans;
        }else {
            danBalls.add(redBall.remove(0));
        }

        return split(redBall, blueBall, danBalls, gameBean);
    }
}
