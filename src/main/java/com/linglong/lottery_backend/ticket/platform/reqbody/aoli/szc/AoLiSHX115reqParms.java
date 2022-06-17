package com.linglong.lottery_backend.ticket.platform.reqbody.aoli.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateSHX115reqParms;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AoLiSHX115reqParms extends AoLiSZCreqNumbers implements CreateSHX115reqParms {

    String interfaceName = "interfaceFront";

    @Override
    public StringBuffer createSHX115reqBody(List<TicketDto> tickets, Game game) {
        JSONObject data = new JSONObject();
        data.put("timeStamp", new Date().getTime());
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("messageId", "1");
        data.put("messageType","pushTicket");

        JSONArray betContent = JSONArray.fromObject(createParms(tickets).toString());
        JSONObject messageData = new JSONObject();
        messageData.put("betContent", betContent);
        messageData.put("lotteryCode", "SHX115");
        data.put("messageData", messageData);

        StringBuilder encodeStr = new StringBuilder();
        encodeStr.append(data.getInt("cardCode"));
        encodeStr.append("$");

        encodeStr.append(data.getString("messageData"));
        encodeStr.append("$");

        encodeStr.append(data.getString("messageId"));
        encodeStr.append("$");

        encodeStr.append(data.getString("messageType"));
        encodeStr.append("$");

        encodeStr.append(data.getString("timeStamp"));
        encodeStr.append("$");

        encodeStr.append(PlatformEnum.AOLI.getPlatformKey());

        String encode = DigestUtils.md5Hex(encodeStr.toString());
        data.put("md5",encode);

        return new StringBuffer(AoliGameEnum.createReqBody(interfaceName, data.toString()));
    }

    @Override
    protected JSONObject createSZCreqBody(List<TicketNumber> ticketNumbers, TicketDto ticketDto, AoLiSZCreqNumbers aoLiSZCreqNumbers) {
        JSONObject betContentData = new JSONObject();

        ticketNumbers.forEach(ticketNumber -> {
            String[] lotteryNumbers = ticketNumber.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR+CommonConstant.COMMON_VERTICAL_STR+CommonConstant.COMMON_AT_STR);
            String[] lotteryNumber = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

            betContentData.put("betType", AoliGameEnum.SHX115_BET_TYPE_CODE_MAP.get(ticketNumber.getPlayType()));
            betContentData.put("issueNumber", ticketNumber.getPeriod());
            betContentData.put("multiple", ticketNumber.getTimes());
            betContentData.put("number", createNumbers(lotteryNumber, ticketNumber.getPlayType()).get(0));
            betContentData.put("orderTime", DateFormatUtils.format(ticketDto.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            betContentData.put("playType", AoliGameEnum.SHX115_PLAY_TYPE_CODE_MAP.get(ticketNumber.getExtra()));
            betContentData.put("schemeCost", ticketDto.getTicketAmount());
            betContentData.put("ticketId", ticketDto.getTicketId());
            betContentData.put("units", ticketNumber.getBetNumber());

        });

        return betContentData;

    }

    @Override
    public StringBuffer createParms(List<TicketDto> tickets) {
        JSONArray betContent = new JSONArray();

        for (TicketDto ticketDto : tickets) {
            List<TicketNumber> ticketNumbers = ticketDto.getNumbers();
            JSONObject betContentData = this.createSZCreqBody(ticketNumbers, ticketDto, this);
            betContent.add(betContentData);
        }

        return new StringBuffer(betContent.toString());
    }

    @Override
    protected JSONArray createNumbers(String[] lotteryNumbers, String playType) {
        JSONArray numberArray = new JSONArray();
        StringBuffer numbers = new StringBuffer();
        for (int i1 = 0; i1 < lotteryNumbers.length; i1++) {
            numbers.append(lotteryNumbers[i1].replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.POUND_SPLIT_STR));
            numbers.append(CommonConstant.SEMICOLON_SPLIT_STR);
        }
        numberArray.add(numbers.deleteCharAt(numbers.length()-1).toString());
        return numberArray;
    }

}
