package com.linglong.lottery_backend.ticket.platform.reqbody.aoli.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateDLTreqParms;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreatePL3reqParms;
import com.linglong.lottery_backend.utils.DateUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AoLiPL3reqParms extends AoLiSZCreqNumbers implements CreatePL3reqParms {

    String interfaceName = "pushTicketList";

    @Override
    public StringBuffer createPL3reqBody(List<TicketDto> tickets, Game game) {
        JSONObject data = new JSONObject();

        JSONArray betContent = JSONArray.fromObject(createParms(tickets).toString());

        data.put("betContent",betContent);
        data.put("betSoruce",PlatformEnum.AOLI.getPlatformCode());
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("lotteryCode",AoliGameEnum.LOTTERY_CODE_MAP.get(game.getGameType()));
        data.put("messageType","pushTicketList");
        data.put("pwd",PlatformEnum.AOLI.getPlatformPwd());

        StringBuffer encodeStr = new StringBuffer();
        encodeStr.append("cardCode=");
        encodeStr.append(data.getInt("cardCode"));
        encodeStr.append("$");

        encodeStr.append("lotteryCode=");
        encodeStr.append(data.getString("lotteryCode"));
        encodeStr.append("$");

        encodeStr.append("pwd=");
        encodeStr.append(data.getString("pwd"));
        encodeStr.append("$");

        encodeStr.append("betSoruce=");
        encodeStr.append(data.getString("betSoruce"));
        encodeStr.append("$");

        encodeStr.append("betContent=");
        encodeStr.append(data.getString("betContent"));
        encodeStr.append("$");

        encodeStr.append("publicKey=");
        encodeStr.append(PlatformEnum.AOLI.getPlatformKey());

        String encode = DigestUtils.md5Hex(encodeStr.toString());
        data.put("key",encode);

        return new StringBuffer(AoliGameEnum.createReqBody(interfaceName, data.toString()));
    }

    @Override
    protected JSONObject createSZCreqBody(List<TicketNumber> ticketNumbers, TicketDto ticketDto, AoLiSZCreqNumbers aoLiSZCreqNumbers) {
        JSONObject betContentData = new JSONObject();

        ticketNumbers.forEach(ticketNumber -> {
            String[] lotteryNumbers = ticketNumber.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR+CommonConstant.COMMON_VERTICAL_STR+CommonConstant.COMMON_AT_STR);
            String[] lotteryNumber = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

            betContentData.put("betType", AoliGameEnum.DOUBLE_COLOR_PLAY_TYPE.get(ticketNumber.getPlayType()));
            betContentData.put("issueNumber", lotteryNumbers[0]);
            betContentData.put("multiple", ticketNumber.getTimes());
            betContentData.put("number", createNumbers(lotteryNumber, ticketNumber.getExtra()));
            betContentData.put("orderStatus", "ING_ENTRUST");
            betContentData.put("orderTime", DateUtil.formatDate(ticketDto.getCreateTime(), DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS));
            betContentData.put("playType", AoliGameEnum.PL3_PLAY_TYPE.get(ticketNumber.getExtra()));
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
        JSONArray numbers = new JSONArray();
        for (int i = 0; i < lotteryNumbers.length; i++) {
            JSONObject numbersData = new JSONObject();
            String[] balls = lotteryNumbers[i].split(CommonConstant.SEMICOLON_SPLIT_STR);

            if(playType.equals(AoliEnum.P3DIRECT.getStatus())) {
                numbersData.put("area3List",JSONArray.fromObject(balls[0].split(CommonConstant.COMMA_SPLIT_STR)));
                numbersData.put("area4List",JSONArray.fromObject(balls[1].split(CommonConstant.COMMA_SPLIT_STR)));
                numbersData.put("area5List",JSONArray.fromObject(balls[2].split(CommonConstant.COMMA_SPLIT_STR)));
            }else {
                numbersData.put("groupList", JSONArray.fromObject(balls[0].split(CommonConstant.COMMA_SPLIT_STR)));

            }

            numbers.add(numbersData);
        }
        return numbers;
    }

}
