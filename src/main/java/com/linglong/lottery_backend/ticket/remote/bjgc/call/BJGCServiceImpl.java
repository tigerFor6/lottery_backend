package com.linglong.lottery_backend.ticket.remote.bjgc.call;

import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.Message;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.PrizeResponse;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.Response;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.util.SignUtil;
import com.linglong.lottery_backend.ticket.util.XStreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BJGCServiceImpl implements BJGCService {

    @Value("${bjgc.jc.url}")
    private String jcUrl;

    @Value("${bjgc.szc.url}")
    private String lottUrl;

    @Resource
    RestTemplate restTemplate;
    static String declaration = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    @Override
    public Response callTicketStatus(Message message, Integer gameType) {
        try{
            String url = createUrl(message, gameType);
            log.info("callTicketStatus req:{}",url);
            String result = restTemplate.postForObject(url, null,String.class);
            log.info("callTicketStatus: {}",result);
            Matcher m = assemblyTicketResponse(result);
            while (m.find()) {
                return XStreamUtil.toBean(m.group(), Response.class);
            }
        }catch (Exception e){
            log.error("callTicketStatus: {}",e);
        }
        return null;
    }

//    public static void main(String[] args) {
//        String xml = "<message><response code=\"0000\"><tickets><ticket ticketid=\"1124972596018941952\" status=\"2\" sp=\"3@12.0;1@12.0\" srno=\"200243-945712-189922-67\" ckcode=\"3F30BE04\" pname=\"娄星区静安街百弘学府城B2栋109号\" pcode=\"M13504\" ptime=\"19/05/05 17:36:15\" /><ticket ticketid=\"1124982261654097920\" status=\"2\" sp=\"3@12.0;0@12.0\" srno=\"200243-945712-189922-67\" ckcode=\"6D81E4B2\" pname=\"娄星区静安街百弘学府城B2栋109号\" pcode=\"M13504\" ptime=\"19/05/05 18:23:53\" /><ticket ticketid=\"1124988020920029184\" status=\"2\" sp=\"3@12.0;0@12.0\" srno=\"200243-945712-189922-67\" ckcode=\"EF64AE5A\" pname=\"娄星区静安街百弘学府城B2栋109号\" pcode=\"M13504\" ptime=\"19/05/05 18:40:16\" /></tickets></response></message>";
//        Pattern pattern = Pattern.compile("(?<=<message>).*?(?=</message>)");// 匹配的模式
//        Matcher m = pattern.matcher(xml);
//        while (m.find()) {
//            System.out.println(m.group());
//            Response response = XStreamUtil.toBean(m.group(), Response.class);
//            System.out.println(JSON.toJSONString(response));
//        }
//    }
    private String createUrl(Message message, Integer gameType) {
//        Platform platform = PlatformEnum.BJGONGCAI.getPlatform();
        StringBuffer url = new StringBuffer();
        String xml = XStreamUtil.toXml(message);
//        url.append(platform.getPlatformUrl());
        if(gameType.equals(Integer.valueOf(1)) || gameType.equals(Integer.valueOf(2))) {
            url.append(jcUrl);
        }else {
            url.append(lottUrl);
        }
        url.append("?")
            .append("msg=")
            .append(declaration)
            .append(SignUtil.convertFromXml(xml))
            .append("&cmd=")
            .append(message.getHeader().getCmd());
        return url.toString();
    }

    @Override
    public PrizeResponse callPrizeTicketStatus(Message message) {
        try{
            String url = createUrl(message, 1);
            String result = restTemplate.postForObject(url, null,String.class);
            System.out.println(result);
            Matcher m = assemblyTicketResponse(result);
            while (m.find()) {
                return XStreamUtil.toBean(m.group(), PrizeResponse.class);
            }
        }catch (Exception e){
            log.error("callPrizeTicketStatus: {}",e);
        }
        return null;
    }

    private Matcher assemblyTicketResponse(String data) {
        Pattern pattern = Pattern.compile("(?<=<message>).*?(?=</message>)");// 匹配的模式
        Matcher m = pattern.matcher(data);
        return m;
    }
}
