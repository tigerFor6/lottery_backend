package com.linglong.lottery_backend.ticket.platform.reqbody.aoli.jc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateJCLQreqParms;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AOLI封装竞猜（篮球）请求数据
 */
@Service
public class AoLiLQreqParms extends AoLiJCreqBetContent implements CreateJCLQreqParms {

    String interfaceName = "pushTicketList";

    @Override
    public StringBuffer createJCLQreqBody(List<TicketDto> tickets, Game game) {
        JSONObject data = new JSONObject();

        data.put("betContent", JSONArray.fromObject(createParms(tickets).toString()));
        data.put("betSoruce", PlatformEnum.AOLI.getPlatformCode());
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
    public StringBuffer createParms(List<TicketDto> tickets) {
        JSONArray betContent = new JSONArray();

        for (TicketDto ticketDto : tickets) {
            List<TicketNumber> ticketNumbers = ticketDto.getNumbers();
            betContent.add(super.createJCreqBody(ticketNumbers, ticketDto, this));
        }

        return new StringBuffer(betContent.toString());
    }

    @Override
    protected JSONObject createMatchContentData(JSONObject matchContentData, String[] details, String ticketNumberDetails,String ticketPayType) {
        JSONArray matchContentDataOption = new JSONArray();
        for (int i2 = 0; i2 < details.length; i2++) {
            matchContentDataOption.add(AoliGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.
                    get(ticketNumberDetails).
                    get(details[i2]));
        }
        matchContentData.put("options",matchContentDataOption);
        matchContentData.put("playType", Integer.valueOf(AoliGameEnum.PAY_TYPE_BASKETBALL_CODE_MAP.get(ticketPayType)));
        return matchContentData;
    }

}
