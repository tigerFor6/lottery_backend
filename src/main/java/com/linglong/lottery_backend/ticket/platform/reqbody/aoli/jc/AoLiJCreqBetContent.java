package com.linglong.lottery_backend.ticket.platform.reqbody.aoli.jc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.List;

/**
 * AOLI 投注（竞猜）类型彩种 封装投注项
 */
public abstract class AoLiJCreqBetContent {

    //彩种类型-竞彩
    Integer TYPE = 1;

    protected JSONObject createJCreqBody(List<TicketNumber> ticketNumbers, TicketDto ticketDto, AoLiJCreqBetContent aoliJCreqBetContent) {
        JSONObject betContentData = new JSONObject();

        ticketNumbers.forEach(ticketNumber -> {
            String lotteryNumber = ticketNumber.getLotteryNumber().split(CommonConstant.COMMON_AT_STR)[0];
            Integer units = ticketNumber.getBetNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.SPACE_SPLIT_STR);
            JSONArray matchContent = createJCreqMatchContent(lotteryNumbers, aoliJCreqBetContent);
            String payType = (String)matchContent.remove(matchContent.size() - 1);

            betContentData.put("matchContent", matchContent);
            betContentData.put("multiple",ticketDto.getTimes());
            betContentData.put("orderStatus","ING_ENTRUST");
            betContentData.put("orderTime", DateFormatUtils.format(ticketDto.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            betContentData.put("passMode",matchContent.size() > 1 ? "PASS" : "SINGLE");
            betContentData.put("passType","P"+(ticketDto.getExtra().equals("1_1") ? "1" : ticketDto.getExtra()));
            betContentData.put("playType",payType);
            betContentData.put("schemeCost",ticketDto.getTicketAmount());
            betContentData.put("ticketId",ticketDto.getTicketId());
            betContentData.put("units",Integer.valueOf(units));
        });
        return betContentData;
    }

    private JSONArray createJCreqMatchContent(String[] lotteryNumbers, AoLiJCreqBetContent aoliJCreqBetContent) {
        JSONArray matchContent = new JSONArray();
        String payType = null;
        for (int i1 = 0; i1 < lotteryNumbers.length; i1++) {
            JSONObject matchContentData = new JSONObject();

            String[] ticketNumberDetails = lotteryNumbers[i1].split(":|~");

            matchContentData.put("matchNumber",ticketNumberDetails[0]);

            String ticketPayType = ticketNumberDetails[1].toUpperCase().equals("BIFEN") ? "BF" : ticketNumberDetails[1].toUpperCase();
            if(payType == null) {
                payType = ticketPayType;
            }
            if(!payType.equals(ticketPayType)) {
                payType = "HHGG";
            }

            String[] details = ticketNumberDetails[2].split("#");
            matchContent.add(aoliJCreqBetContent.createMatchContentData(matchContentData, details, ticketNumberDetails[1], ticketPayType));
        }
        matchContent.add(payType);
        return matchContent;
    }

    protected JSONObject createMatchContentData(JSONObject matchContentData, String[] details,
                                                String ticketNumberDetails, String ticketPayType) {
        return null;
    }
}
