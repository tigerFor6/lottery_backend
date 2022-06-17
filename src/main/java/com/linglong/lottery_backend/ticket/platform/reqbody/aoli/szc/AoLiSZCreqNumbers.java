package com.linglong.lottery_backend.ticket.platform.reqbody.aoli.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.List;

public abstract class AoLiSZCreqNumbers {

    //彩种类型-数字彩
    Integer TYPE = 2;

    protected JSONObject createSZCreqBody(List<TicketNumber> ticketNumbers, TicketDto ticketDto, AoLiSZCreqNumbers aoLiSZCreqNumbers) {
        JSONObject betContentData = new JSONObject();

        ticketNumbers.forEach(ticketNumber -> {
            String[] lotteryNumbers = ticketNumber.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR+CommonConstant.COMMON_VERTICAL_STR+CommonConstant.COMMON_AT_STR);
            String[] lotteryNumber = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
            String playType = AoliGameEnum.DOUBLE_COLOR_PLAY_TYPE.get(ticketDto.getPlayType());

            betContentData.put("betType", playType);
            betContentData.put("issueNumber", lotteryNumbers[0]);
            betContentData.put("multiple", ticketDto.getTimes());
            betContentData.put("number", createSZCreqNumbers(lotteryNumber, playType, aoLiSZCreqNumbers));
            betContentData.put("orderStatus", "ING_ENTRUST");
            betContentData.put("orderTime", DateFormatUtils.format(ticketDto.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            betContentData.put("playType", "General");
            betContentData.put("schemeCost", ticketDto.getTicketAmount());
            betContentData.put("ticketId",ticketDto.getTicketId());
            betContentData.put("units",ticketNumber.getBetNumber());

        });

        return betContentData;
    }


    private JSONArray createSZCreqNumbers(String[] lotteryNumbers, String playType,  AoLiSZCreqNumbers aoLiSZCreqNumbers) {
        return aoLiSZCreqNumbers.createNumbers(lotteryNumbers, playType);
    }

    protected JSONArray createNumbers(String[] lotteryNumbers, String playType) {
        return null;
    }
}
