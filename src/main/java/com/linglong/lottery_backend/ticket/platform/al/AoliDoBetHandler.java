package com.linglong.lottery_backend.ticket.platform.al;

import com.linglong.lottery_backend.ticket.bean.HttpParamDto;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.AbstractPlatformHandler;
import com.linglong.lottery_backend.ticket.platform.reqbody.aoli.AoliReqBody;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.ticket.service.SplitService;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.HttpServiceUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * hua zeng guang
 */
@Component
@Slf4j
public class AoliDoBetHandler extends AbstractPlatformHandler {

    @Autowired
    private AoliReqBody aoliReqBody;

    @Autowired
    private TblBettingTicketRepository tblBettingTicketRepository;

    @Override
    protected String createRequestMessage(Integer gameType, Map<String, Object> paramMap) {
        log.warn("AoliDoBetHandler createRequestMessage");
        List<TicketDto> tickets = (List<TicketDto>) paramMap.get("ticketList");

        String bodyData = aoliReqBody.getReqMessageBody(gameType, tickets).toString();
        log.warn("AoliDoBetHandler sendBodyData: {}",bodyData);

        return bodyData;
    }

    /**
     * 封装竞猜足球、篮球、双色球请求数据
     * @return
     */
    private String createJCZQ_JCLQ_SSQ_DLT_PL3reqBody(List<TicketDto> ticket, Integer gameType) {
        JSONObject data = new JSONObject();

        JSONArray betContent = createReqParms(ticket, gameType);

        data.put("betContent",betContent);
        data.put("betSoruce",PlatformEnum.AOLI.getPlatformCode());
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("lotteryCode",AoliGameEnum.LOTTERY_CODE_MAP.get(gameType));
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

        return AoliGameEnum.createReqBody("pushTicketList", data.toString());
    }

    /**
     * 封装陕11选5请求数据
     * @return
     */
    private String createSHX115reqBody(List<TicketDto> ticket, Integer gameType) {
        JSONObject data = new JSONObject();
        data.put("timeStamp", new Date().getTime());
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("messageId", "1");
        data.put("messageType","pushTicket");

        JSONArray betContent = createReqParms(ticket, gameType);

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

        return AoliGameEnum.createReqBody("interfaceFront", data.toString());
    }

    private JSONArray createReqParms(List<TicketDto> splitTicket, Integer gameType) {
        JSONArray betContent = new JSONArray();

        for (TicketDto ticketDto : splitTicket) {
            List<TicketNumber> ticketNumbers = ticketDto.getNumbers();
            if(gameType.equals(AoliEnum.JCZQ.getCode()) || gameType.equals(AoliEnum.JCLQ.getCode())){
                betContent.add(createZQLQ(ticketNumbers, ticketDto, gameType));

            }else if(gameType.equals(AoliEnum.SSQ.getCode())) {
                betContent.add(createDoubleColor(ticketNumbers, ticketDto));

            }else if(gameType.equals(AoliEnum.SHX115.getCode())){
                betContent.add(createSHX115(ticketNumbers, ticketDto));

            }else if(gameType.equals(AoliEnum.DLT.getCode())){
                betContent.add(createDlt(ticketNumbers, ticketDto));

            }else if(gameType.equals(AoliEnum.PL3.getCode())){
                betContent.add(createPl3(ticketNumbers, ticketDto));

            }else {
                continue;
            }
        }

        return betContent;
    }

    /**
     * AOLI足球篮球封装出票请求数据
     * @return
     */
    private JSONObject createZQLQ(List<TicketNumber> ticketNumbers, TicketDto ticketDto, Integer gameType){
        JSONObject betContentData = new JSONObject();
        JSONArray matchContent = new JSONArray();

        Integer units = 1;
        String payType = null;
        for (int i = 0; i < ticketNumbers.size(); i++) {

            String lotteryNumber = ticketNumbers.get(i).getLotteryNumber().split("@")[0];
            units = ticketNumbers.get(i).getBetNumber();
            String[] lotteryNumbers = lotteryNumber.split(" ");
            //20190520004:bqc~#13#3331#30#00#11#10#03#01 20190520005:spf~3@9
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
                StringBuffer detailsBuff = new StringBuffer();
                //1足球、2篮球
                if(gameType.equals(AoliEnum.JCZQ.getCode())) {
                    JSONObject matchContentDataValue = new JSONObject();
                    for (int i2 = 0; i2 < details.length; i2++) {
                        detailsBuff.append(AoliGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP.
                                get(ticketNumberDetails[1]).
                                get(details[i2]))
                                .append(",");
                    }
                    matchContentDataValue.put(ticketPayType,detailsBuff.deleteCharAt(detailsBuff.length()-1).toString());
                    matchContentData.put("value",matchContentDataValue);
                }else if(gameType.equals(AoliEnum.JCLQ.getCode())){
                    JSONArray matchContentDataOption = new JSONArray();
                    for (int i2 = 0; i2 < details.length; i2++) {
                        matchContentDataOption.add(AoliGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP.
                                get(ticketNumberDetails[1]).
                                get(details[i2]));
                    }
                    matchContentData.put("options",matchContentDataOption);
                    matchContentData.put("playType", Integer.valueOf(AoliGameEnum.PAY_TYPE_BASKETBALL_CODE_MAP.get(ticketPayType)));
                }else {
                    continue;
                }
                matchContent.add(matchContentData);
            }
        }
        betContentData.put("matchContent",matchContent);
        betContentData.put("multiple",ticketDto.getTimes());
        betContentData.put("orderStatus","ING_ENTRUST");
        betContentData.put("orderTime", DateFormatUtils.format(ticketDto.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
        betContentData.put("passMode",matchContent.size() > 1 ? "PASS" : "SINGLE");
        betContentData.put("passType","P"+(ticketDto.getExtra().equals("1_1") ? "1" : ticketDto.getExtra()));
        betContentData.put("playType",payType);
        betContentData.put("schemeCost",ticketDto.getTicketAmount());
        betContentData.put("ticketId",ticketDto.getTicketId());
        betContentData.put("units",Integer.valueOf(units));
        return betContentData;
    }


    /**
     * 封装双色球出票请求数据
     * @return
     */
    private JSONObject createDoubleColor(List<TicketNumber> ticketNumbers, TicketDto ticketDto) {
        JSONObject betContentData = new JSONObject();

        for (int i = 0; i < ticketNumbers.size(); i++) {
            TicketNumber ticketNumber = ticketNumbers.get(i);
            String[] lotteryNumbers = ticketNumber.getLotteryNumber().split("@|%");
            String[] lotteryNumber = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
            //2019060%08,17,18,28,29,32:02 03,08,10,16,28,30:15 09,14,19,24,28,30:10 01,05,07,14,15,22:07 07,10,18,21,23,24:13@5

            String playType = AoliGameEnum.DOUBLE_COLOR_PLAY_TYPE.get(ticketDto.getPlayType());

            betContentData.put("betType", playType);
            betContentData.put("issueNumber", lotteryNumbers[0]);
            betContentData.put("multiple", ticketDto.getTimes());

            JSONArray numbers = new JSONArray();
            for (int i1 = 0; i1 < lotteryNumber.length; i1++) {
                JSONObject numbersDataJSON = new JSONObject();

                String[] colorBallGroup;
                String[] dantuo = null;
                if(playType.equalsIgnoreCase("Dantuo")) {
                    String[] dantuoAndBall = lotteryNumber[i1].split(CommonConstant.COMMON_AND_STR);
                    colorBallGroup = dantuoAndBall[1].split(CommonConstant.COMMON_COLON_STR);
                    dantuo = dantuoAndBall[0].split(CommonConstant.COMMA_SPLIT_STR);

                }else {
                    colorBallGroup = lotteryNumber[i1].split(CommonConstant.COMMON_COLON_STR);
                }


                JSONArray redBallJSON = JSONArray.fromObject(colorBallGroup[0].split(","));
                JSONArray blueBallJSON = JSONArray.fromObject(colorBallGroup[1].split(","));

//                splitService.sqlitMuDandcolorBall(lotteryNumber[i1], lotteryNumbers[0], ticketDto);

                numbersDataJSON.put("blueList",blueBallJSON);
                if(dantuo != null) {
                    numbersDataJSON.put("redDanList", JSONArray.fromObject(dantuo));
                }
                numbersDataJSON.put("redList",redBallJSON);

                numbers.add(numbersDataJSON);
            }
            betContentData.put("number", numbers);
            betContentData.put("orderStatus", "ING_ENTRUST");
            betContentData.put("orderTime", DateFormatUtils.format(ticketDto.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            betContentData.put("playType", "General");
            betContentData.put("schemeCost", ticketDto.getTicketAmount());
            betContentData.put("ticketId",ticketDto.getTicketId());
            betContentData.put("units",ticketNumbers.get(i).getBetNumber());
        }
        return betContentData;
    }

    /**
     * 封装陕11选5出票请求数据
     * @return
     */
    private JSONObject createSHX115(List<TicketNumber> ticketNumbers, TicketDto ticketDto) {
        JSONObject betContentData = new JSONObject();

        for (int i = 0; i < ticketNumbers.size(); i++) {
            TicketNumber ticketNumber = ticketNumbers.get(i);

            String lotteryNumber = ticketNumber.getLotteryNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.COMMON_AT_STR + "|" + CommonConstant.PERCENT_SPLIT_STR);

            String[] ticketsNumbers = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

            StringBuffer numbers = new StringBuffer();

            for (int i1 = 0; i1 < ticketsNumbers.length; i1++) {

                numbers.append(ticketsNumbers[i1].replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.POUND_SPLIT_STR));
                numbers.append(CommonConstant.SEMICOLON_SPLIT_STR);
            }

            betContentData.put("betType", AoliGameEnum.SHX115_BET_TYPE_CODE_MAP.get(ticketNumber.getPlayType()));
            betContentData.put("issueNumber", ticketNumber.getPeriod());
            betContentData.put("multiple", ticketNumber.getTimes());
            betContentData.put("number", numbers.deleteCharAt(numbers.length()-1).toString());
            betContentData.put("orderTime", DateFormatUtils.format(ticketDto.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
            betContentData.put("playType", AoliGameEnum.SHX115_PLAY_TYPE_CODE_MAP.get(ticketNumber.getExtra()));
            betContentData.put("schemeCost", ticketDto.getTicketAmount());
            betContentData.put("ticketId", ticketDto.getTicketId());
            betContentData.put("units", ticketNumber.getBetNumber());
        }
        return betContentData;
    }

    /**
     * 封装大乐透出票数据
     * @param ticketNumbers
     * @param ticketDto
     * @return
     */
    private JSONObject createDlt(List<TicketNumber> ticketNumbers, TicketDto ticketDto) {
        JSONObject betContentData = new JSONObject();

        ticketNumbers.forEach(ticketNumber -> {
            String lotteryNumber = ticketNumber.getLotteryNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.COMMON_AT_STR + "|" + CommonConstant.PERCENT_SPLIT_STR);

            String[] ticketsNumbers = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);

            StringBuffer numbers = new StringBuffer();
            for (int i = 0; i < ticketsNumbers.length; i++) {
                String[] balls = ticketsNumbers[i].split("\\|");

                String redBalls = balls[0].replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.POUND_SPLIT_STR);
                String blueBalls = balls[1].replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.POUND_SPLIT_STR);

                numbers.append(redBalls);
                numbers.append(CommonConstant.COMMON_ADD_STR);
                numbers.append(blueBalls);
                numbers.append(CommonConstant.SEMICOLON_SPLIT_STR);
            }

            betContentData.put("betType", AoliGameEnum.DLT_PLAY_TYPE.get(ticketNumber.getPlayType()));
            betContentData.put("issueNumber", lotteryNumbers[0]);
            betContentData.put("multiple", ticketNumber.getTimes());
            betContentData.put("number", numbers.deleteCharAt(numbers.length() - 1).toString());
            betContentData.put("orderStatus", "ING_ENTRUST");
            betContentData.put("orderTime", DateUtil.formatDate(ticketDto.getCreateTime(), DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS));
            betContentData.put("playType", AoliGameEnum.DLT_PLAY_TYPE_CODE_MAP.get(ticketNumber.getExtra()));
            betContentData.put("schemeCost", ticketDto.getTicketAmount());
            betContentData.put("ticketId", ticketDto.getTicketId());
            betContentData.put("units", ticketNumber.getBetNumber());
        });

        return betContentData;
    }

    /**
     * 封装排列三出票数据
     * @param ticketNumbers
     * @param ticketDto
     * @return
     */
    private JSONObject createPl3(List<TicketNumber> ticketNumbers, TicketDto ticketDto) {
        JSONObject betContentData = new JSONObject();

        ticketNumbers.forEach(ticketNumber -> {
            String lotteryNumber = ticketNumber.getLotteryNumber();
            String[] lotteryNumbers = lotteryNumber.split(CommonConstant.PERCENT_SPLIT_STR);

            String[] ticketsNumbers = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
            JSONArray numbers = new JSONArray();
            for (int i = 0; i < ticketsNumbers.length; i++) {
                JSONObject numbersData = new JSONObject();
                String[] balls = ticketsNumbers[i].split(CommonConstant.SEMICOLON_SPLIT_STR);

                if(ticketNumber.getExtra().equals(AoliEnum.P3DIRECT.getStatus())) {
                    numbersData.put("area3List",JSONArray.fromObject(balls[0].split(CommonConstant.COMMA_SPLIT_STR)));
                    numbersData.put("area4List",JSONArray.fromObject(balls[1].split(CommonConstant.COMMA_SPLIT_STR)));
                    numbersData.put("area5List",JSONArray.fromObject(balls[2].split(CommonConstant.COMMA_SPLIT_STR)));
                }else {
                    numbersData.put("groupList", JSONArray.fromObject(balls[0].split(CommonConstant.COMMA_SPLIT_STR)));

                }

                numbers.add(numbersData);
            }

            betContentData.put("betType", AoliGameEnum.DOUBLE_COLOR_PLAY_TYPE.get(ticketNumber.getPlayType()));
            betContentData.put("issueNumber", ticketNumber.getPeriod());
            betContentData.put("multiple", ticketNumber.getTimes());
            betContentData.put("number", numbers);
            betContentData.put("orderStatus", "ING_ENTRUST");
            betContentData.put("orderTime", DateUtil.formatDate(ticketDto.getCreateTime(), DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS));
            betContentData.put("playType", AoliGameEnum.PL3_PLAY_TYPE.get(ticketNumber.getExtra()));
            betContentData.put("schemeCost", ticketDto.getTicketAmount());
            betContentData.put("ticketId", ticketDto.getTicketId());
            betContentData.put("units", ticketNumber.getBetNumber());

        });

        return betContentData;
    }

    /**
     * 陕11选5请求返奖结果
     * @return
     */
    public JSONObject reqLotteryResult(String issueNumber) throws Exception{

        JSONObject data = new JSONObject();
        data.put("lotteryCode", "SHX115");
        data.put("issueNumber",issueNumber);
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("messageId", "1");
        data.put("messageType","queryX115OpenResult ");

        StringBuilder encodeStr = new StringBuilder();
        encodeStr.append("cardCode=");
        encodeStr.append(data.getInt("cardCode"));
        encodeStr.append("$");

        encodeStr.append("lotteryCode=");
        encodeStr.append(data.getString("lotteryCode"));
        encodeStr.append("$");

        encodeStr.append("issueNumber=");
        encodeStr.append(data.getString("issueNumber"));
        encodeStr.append("$");

        encodeStr.append("messageId=");
        encodeStr.append(data.getString("messageId"));
        encodeStr.append("$");

        encodeStr.append("messageType=");
        encodeStr.append(data.getString(    "messageType"));
        encodeStr.append("$");

        encodeStr.append("publicKey=");
        encodeStr.append(PlatformEnum.AOLI.getPlatformKey());

        String encode = DigestUtils.md5Hex(encodeStr.toString());
        data.put("md5",encode);

        String reqBody = AoliGameEnum.createReqBody("queryX115OpenResult", data.toString());

        String result = HttpServiceUtils.sendPostRequest(PlatformEnum.AOLI.getDoBetUrl(),
                                    reqBody, HttpParamDto.DEFAULT_CHARSET, HttpParamDto.DEFAULT_CONNECT_TIME_OUT,
                                    HttpParamDto.DEFAULT_READ_TIME_OUT, false, null, false);

        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new ByteArrayInputStream(result.getBytes("UTF-8")));
        Element elementRoot = document.getRootElement();
        Element elementBody = elementRoot.element("Body");
        Element elementResponse = elementBody.element("queryX115OpenResultResponse");
        return JSONObject.fromObject(elementResponse.elementText("return"));
    }

    @Override
    protected String sendMessage(String requestMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        String doBetUrl = PlatformEnum.AOLI.getDoBetUrl();
        log.warn("AoliDoBetHandler sendMessage: {}",doBetUrl);

        return HttpServiceUtils.sendPostRequest(doBetUrl,
                requestMessage, HttpParamDto.DEFAULT_CHARSET, HttpParamDto.DEFAULT_CONNECT_TIME_OUT,
                HttpParamDto.DEFAULT_READ_TIME_OUT, false, null, false);
    }

    @Override
    protected String parseResponseMessage(String responseMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        List<TicketDto> ticketList = (List<TicketDto>)paramMap.get("ticketList");

        List<Long> ids = ticketList.stream().map(ticketDto -> Long.valueOf(ticketDto.getTicketId())).collect(Collectors.toList());
        tblBettingTicketRepository.updateTicketStatusByTicketId(0, ids);
        return null;
    }

    /**
     * ORDER_MAXSIZE_ERROR 投注订单超长20个
     * 分割投注
     * @param tickets
     * @return
     */
    private List<List<TicketDto>> splitTicket(List<TicketDto> tickets){
        List<List<TicketDto>> pageTicketList = new ArrayList<>();

        List<TicketDto> tmpTickets = new ArrayList<>();
        for (int i = 0; i < tickets.size(); i++) {
            if(i%20 == 0){
                pageTicketList.add(tmpTickets);
                tmpTickets.clear();
            }
            tmpTickets.add(tickets.get(i));
        }

        return pageTicketList;
    }
}
