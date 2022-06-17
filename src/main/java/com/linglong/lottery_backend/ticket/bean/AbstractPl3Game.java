package com.linglong.lottery_backend.ticket.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.linglong.lottery_backend.order.entity.Order;
import com.linglong.lottery_backend.order.entity.OrderDetails;
import com.linglong.lottery_backend.order.entity.OrderDetailsInfo;
import com.linglong.lottery_backend.order.repository.OrderDetailsInfoRepository;
import com.linglong.lottery_backend.order.repository.OrderDetailsRepository;
import com.linglong.lottery_backend.order.service.bean.RankThreeDetails;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 排列三
 */
public abstract class AbstractPl3Game extends AbstractGame{

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderDetailsInfoRepository orderDetailsInfoRepository;

    public List<GameBean> assemblyPl3GameBean(AbstractPl3Game awg, Order order) {
        List<GameBean> result = new LinkedList<>();
        OrderDetailsInfo orderDetailsInfo = orderDetailsInfoRepository.findByOrderId(order.getOrderId());
        OrderDetails orderDetails = orderDetailsRepository.findByOrderId(order.getOrderId()).get(0);
        List<RankThreeDetails> rankThreeDetails = JSONArray.parseArray(orderDetails.getPlaysDetails(), RankThreeDetails.class);

        order.setBetTimes(orderDetailsInfo.getMultiple());

        rankThreeDetails.forEach(rankThree -> {
            String number = rankThree.getNumber();

            String playType = "-1";
            if(rankThree.getRankThreeType().equals(AoliEnum.P3DIRECT.getStatus())) {

                String[] numbers = number.split(CommonConstant.SEMICOLON_SPLIT_STR);
                for (int i = 0; i < numbers.length; i++) {
                    if(numbers[i].split(CommonConstant.COMMA_SPLIT_STR).length > 1) {
                        playType = "-2";
                        break;
                    }
                }
            }else {
                String[] numbers = number.split(CommonConstant.COMMA_SPLIT_STR);
                if (rankThree.getRankThreeType().equals(AoliEnum.GROUP3.getStatus())) {
                    playType = "-2";

                }else if(rankThree.getRankThreeType().equals(AoliEnum.GROUP6.getStatus())) {
                    if(numbers.length > 3) {
                        playType = "-2";
                    }

                }
            }

            rankThree.setRankThreeType(rankThree.getRankThreeType()+playType);
        });

        Map<String, List<RankThreeDetails>> group = rankThreeDetails.stream()
                                                                                            .collect(Collectors.groupingBy(RankThreeDetails::getRankThreeType));

        group.forEach((betType, details) -> {

            String playType = null;
            String[] playTypes = betType.split(CommonConstant.COMMON_DASH_STR);

            if(playTypes[2].equals("1")) {

                List<List<RankThreeDetails>> rank3Details = new ArrayList<>();
                if(details.size() <= 5) {
                    rank3Details.add(details);
                }else {
                    int limit = 0;
                    do {
                        limit += 5;
                        rank3Details.add(details.subList(limit - 5,  limit > details.size() ? details.size() : limit));
                    }while (limit < details.size());
                }

                rank3Details.forEach(rank3 -> {
                    int betNumber = 0;
                    for (int i = 0; i < rank3.size(); i++) {
                        betNumber += Integer.valueOf(rank3.get(i).getBetNumber()).intValue();
                    }

                    StringBuffer ballNumber = new StringBuffer();
                    rank3.forEach(e -> {
                        ballNumber.append(e.getNumber());
                        ballNumber.append(CommonConstant.SPACE_SPLIT_STR);
                    });

                    String lotteryNumber =  ballNumber.deleteCharAt(ballNumber.length() - 1).toString();

                    GameBean gameBean = new GameBean(
                            betNumber, WordConstant.SINGLE,
                            Joiner.on(CommonConstant.PERCENT_SPLIT_STR)
                                    .join(orderDetails.getIssue(),lotteryNumber),
                            PRICE_PER_STAKE.multiply(BigDecimal.valueOf(betNumber)).multiply(BigDecimal.valueOf(orderDetailsInfo.getMultiple())),
                            playTypes[0] + "-" +playTypes[1],
                            order.getEndTime(),Integer.valueOf(orderDetails.getIssue()));
                    gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));

                    result.add(gameBean);

                });

            }else if(playTypes[2].equals("2")) {

                details.forEach(e -> {
                    GameBean gameBean = new GameBean(
                            Integer.valueOf(e.getBetNumber()), WordConstant.MULTIPLE,
                            Joiner.on(CommonConstant.PERCENT_SPLIT_STR)
                                    .join(orderDetails.getIssue(),e.getNumber()),
                            PRICE_PER_STAKE.multiply(BigDecimal.valueOf(Integer.valueOf(e.getBetNumber()))).multiply(new BigDecimal(orderDetailsInfo.getMultiple())),
                            playTypes[0] + "-" +playTypes[1],
                            order.getEndTime(),Integer.valueOf(orderDetails.getIssue()));
                    gameBean.setBetTimes(new BigDecimal(orderDetailsInfo.getMultiple()));
                    result.add(gameBean);
                });
            }
        });

        return result;
    }

    @Override
    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {
        return userGameBeanList;
    }
}
