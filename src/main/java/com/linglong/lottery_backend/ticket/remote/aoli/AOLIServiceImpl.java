package com.linglong.lottery_backend.ticket.remote.aoli;

import com.linglong.lottery_backend.ticket.bean.HttpParamDto;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import com.linglong.lottery_backend.utils.HttpServiceUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author  Yinxiong
 * @DATE 2019年5月20日
 */
@Slf4j
@Service
public class AOLIServiceImpl implements AOLIService{

    @Override
    public List<Message> callTicketStatus(Map<Integer, StringBuffer> ticketsId) {

        String doBetUrl = PlatformEnum.AOLI.getDoBetUrl();

        SAXReader saxReader = new SAXReader();
        Document doc = null;
        List<Message> ticketList = null;
        for(Integer gameType : ticketsId.keySet()) {

            String reqUrl;
            String interfaceName;
            if(gameType.equals(AoliEnum.SHX115.getCode())) {
                interfaceName = "interfaceFront";
                reqUrl = createReqSHX115MessageBody(interfaceName, ticketsId.get(gameType));

            }else {
                interfaceName = "queryTicketsOrderStatusList";
                reqUrl = createReqJCZQLQSSQMessageBody(interfaceName, gameType, ticketsId.get(gameType));

            }

            String resultXML = HttpServiceUtils.sendPostRequest(doBetUrl,
                    reqUrl, HttpParamDto.DEFAULT_CHARSET, HttpParamDto.DEFAULT_CONNECT_TIME_OUT,
                    HttpParamDto.DEFAULT_READ_TIME_OUT, false, null, false);

            try{
                doc = saxReader.read(new ByteArrayInputStream(resultXML.getBytes("UTF-8")));
            }catch (Exception e) {
                log.error("AOLI callTicketStatus String by XML dom4j 异常",e);
            }

            Element elementRoot = doc.getRootElement();
            Element elementBody = elementRoot.element("Body");
            Element elementResponse = elementBody.element(interfaceName+"Response");

            JSONObject result = JSONObject.fromObject(elementResponse.elementText("return"));
            log.warn("AOLI callTicketStatus reslut："+result);

            if(gameType.equals(AoliEnum.SHX115.getCode())) {
                ticketList = resSHX115resultData(gameType, result);

            }else {
                ticketList = resJCZQLQSSQresultData(gameType, result);

            }


        }
        return ticketList;
    }

    /**
     * 封装竞猜足球、篮球、双色球查询请求数据
     * @param gameType
     * @param ticketsId
     * @return
     */
    private String createReqJCZQLQSSQMessageBody(String interfaceName, Integer gameType, StringBuffer ticketsId){
        if(!isTicketIds(ticketsId))
            return null;


        log.warn("AOLI createReqJCZQLQSSQMessageBody");

        JSONObject data = new JSONObject();
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("messageType","ticketResult");
        data.put("lotteryCode", AoliGameEnum.LOTTERY_CODE_MAP.get(gameType));
        data.put("message",ticketsId.deleteCharAt(ticketsId.length()-1).toString());

        StringBuilder encodeStr = new StringBuilder();
        encodeStr.append("cardCode=");
        encodeStr.append(data.getString("cardCode"));
        encodeStr.append("$");

        encodeStr.append("lotteryCode=");
        encodeStr.append(data.getString("lotteryCode"));
        encodeStr.append("$");

        encodeStr.append("message=");
        encodeStr.append(data.getString("message"));
        encodeStr.append("$");

        encodeStr.append("messageType=");
        encodeStr.append(data.getString("messageType"));
        encodeStr.append("$");

        encodeStr.append("publicKey=");
        encodeStr.append(PlatformEnum.AOLI.getPlatformKey());

        String encode = DigestUtils.md5Hex(encodeStr.toString());
        data.put("key",encode);

        return AoliGameEnum.createReqBody(interfaceName, data.toString());
    }

    /**
     * 封装竞猜足球、篮球、双色球响应数据
     * @param gameType
     * @param resResult
     * @return
     */
    public List<Message> resJCZQLQSSQresultData(Integer gameType, JSONObject resResult) {
        List<Message> ticketList = new ArrayList<>();

        JSONArray messageArray = resResult.getJSONArray("message");
        for (int i = 0; i < messageArray.size(); i++) {
            Message message = new Message();
            JSONObject messageArrayData = messageArray.getJSONObject(i);

            try{
                message.setTicketId(messageArrayData.getString("ticketId"));
                message.setResult(messageArrayData.getString("result"));
                message.setGameType(gameType);
                if(message.getResult().equals(AoliEnum.SUC_TICKET.getStatus())) {
                    message.setOdds(messageArrayData.get("odds") == null ? null : messageArrayData.getString("odds"));
                    message.setSuccessTime(messageArrayData.getString("successTime"));
                    message.setOrderNumber(messageArrayData.getString("orderNumber"));

                    if(gameType.equals(AoliEnum.SSQ.getCode()) || gameType.equals(AoliEnum.PL3.getCode())
                        || gameType.equals(AoliEnum.DLT.getCode()) || gameType.equals(AoliEnum.PL5.getCode())) {
                        message.setOdds("{location: true}");
                    }
                }

            }catch (Exception e) {
                log.error("resJCZQLQSSQresultData message Error："+ resResult.toString());
            }


            ticketList.add(message);
        }
        return ticketList;
    }

    /**
     * 封装陕11选5查询请求数据
     * @param interfaceName
     * @param ticketsId
     * @return
     */
    private String createReqSHX115MessageBody(String interfaceName,StringBuffer ticketsId) {
        if(!isTicketIds(ticketsId))
            return null;

        log.warn("AOLI createReqSHX115MessageBody");

        JSONObject data = new JSONObject();
        data.put("timeStamp", new Date().getTime());
        data.put("cardCode",PlatformEnum.AOLI.getPlatformAccount());
        data.put("messageId", "1");
        data.put("messageType","ticketResult");

        JSONObject messageData = new JSONObject();
        messageData.put("lotteryCode", AoliEnum.SHX115.getStatus());
        messageData.put("ticketIdList", ticketsId.deleteCharAt(ticketsId.length()-1).toString());

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

        return AoliGameEnum.createReqBody(interfaceName, data.toString());
    }

    /**
     * 封装陕11选5响应数据
     * @param gameType
     * @param resResult
     * @return
     */
    public List<Message> resSHX115resultData(Integer gameType, JSONObject resResult) {
        List<Message> ticketList = new ArrayList<>();

        JSONArray result = JSONArray.fromObject(resResult.getString("resultData"));
        for (int i = 0; i < result.size(); i++) {
            JSONObject resultData = result.getJSONObject(i);
            Message message = new Message();

            try {
                message.setTicketId(resultData.getString("ticketId"));
                message.setResult(resultData.getString("result"));
                message.setGameType(gameType);
                if(message.getResult().equals(AoliEnum.SUC_TICKET.getStatus())) {
                    message.setOdds(resultData.get("localPrint") == null ? "{localPrint:true}" : resultData.getString("localPrint"));
                    message.setSuccessTime(resultData.getString("successTime"));
                    message.setOrderNumber(resultData.getString("orderNumber"));
                }
            }catch (Exception e) {
                log.error("resSHX115resultData message Error："+ resResult.toString());
            }

            ticketList.add(message);
        }

        return ticketList;
    }

    private boolean isTicketIds(StringBuffer ticketsId) {
        if(ticketsId == null)
            return false;
        if(ticketsId.length() == 0)
            return false;

        return true;
    }
}
