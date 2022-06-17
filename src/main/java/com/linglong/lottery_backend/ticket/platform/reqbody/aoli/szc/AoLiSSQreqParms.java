package com.linglong.lottery_backend.ticket.platform.reqbody.aoli.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateSSQreqParms;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AOLI封装数字彩（双色球）请求数据
 */
@Service
public class AoLiSSQreqParms extends AoLiSZCreqNumbers implements CreateSSQreqParms {

    String interfaceName = "pushTicketList";

    @Override
    public StringBuffer createSSQreqBody(List<TicketDto> tickets, Game game) {
        JSONObject data = new JSONObject();

        data.put("betContent", JSONArray.fromObject(createParms(tickets).toString()));
        data.put("betSoruce", PlatformEnum.AOLI.getPlatformCode());
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("lotteryCode", AoliGameEnum.LOTTERY_CODE_MAP.get(game.getGameType()));
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
    public StringBuffer createParms(List<TicketDto> tickets) {
        JSONArray betContent = new JSONArray();

        for (TicketDto ticketDto : tickets) {
            List<TicketNumber> ticketNumbers = ticketDto.getNumbers();
            betContent.add(super.createSZCreqBody(ticketNumbers, ticketDto, this));
        }

        return new StringBuffer(betContent.toString());
    }

    @Override
    protected JSONArray createNumbers(String[] lotteryNumbers, String playType) {

        JSONArray numbers = new JSONArray();
        for (int i1 = 0; i1 < lotteryNumbers.length; i1++) {
            JSONObject numbersDataJSON = new JSONObject();

            String[] colorBallGroup;
            String[] dantuo = null;
            if(playType.equalsIgnoreCase("Dantuo")) {
                String[] dantuoAndBall = lotteryNumbers[i1].split(CommonConstant.COMMON_AND_STR);
                colorBallGroup = dantuoAndBall[1].split(CommonConstant.COMMON_COLON_STR);
                dantuo = dantuoAndBall[0].split(CommonConstant.COMMA_SPLIT_STR);

            }else {
                colorBallGroup = lotteryNumbers[i1].split(CommonConstant.COMMON_COLON_STR);
            }


            JSONArray redBallJSON = JSONArray.fromObject(colorBallGroup[0].split(CommonConstant.COMMA_SPLIT_STR));
            JSONArray blueBallJSON = JSONArray.fromObject(colorBallGroup[1].split(CommonConstant.COMMA_SPLIT_STR));

//                splitService.sqlitMuDandcolorBall(lotteryNumber[i1], lotteryNumbers[0], ticketDto);

            numbersDataJSON.put("blueList",blueBallJSON);
            if(dantuo != null) {
                numbersDataJSON.put("redDanList", JSONArray.fromObject(dantuo));
            }
            numbersDataJSON.put("redList",redBallJSON);

            numbers.add(numbersDataJSON);
        }
        return numbers;
    }

}
