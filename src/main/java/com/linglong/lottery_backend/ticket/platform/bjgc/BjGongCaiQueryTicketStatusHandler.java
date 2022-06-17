package com.linglong.lottery_backend.ticket.platform.bjgc;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.bean.HttpParamDto;
import com.linglong.lottery_backend.ticket.bean.ResendUpdateUnit;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.TicketStatus;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.AbstractPlatformHandler;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.HttpServiceUtils;
import com.linglong.lottery_backend.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ynght on 2019-04-28
 */
@Component
@Slf4j
public class BjGongCaiQueryTicketStatusHandler extends AbstractPlatformHandler {

    @Value("${bjgc.jc.url}")
    private String jcUrl;

    @Autowired
    TblBettingTicketRepository tblBettingTicketRepository;


    @Override
    protected String createRequestMessage(Integer gameType, Map<String, Object> paramMap) {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        Timestamp current = DateUtil.getCurrentTimestamp();
        String time = DateUtil.formatTime(current, DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
        StringBuffer buffer = new StringBuffer(BjGongCaiInterfaceEnum.getXmlPre(gameType, time));
        StringBuffer bodyBuffer = new StringBuffer();
        List<Long> ticketIds = (List<Long>) paramMap.get("ticketIds");
        bodyBuffer.append("<body><tickets>");
        for (Long ticketId : ticketIds) {
            bodyBuffer.append("<ticket ticketid=\"").append(ticketId).append("\"/>");
        }
        buffer.append("</tickets>").append(BjGongCaiInterfaceEnum.BJ_GONG_CAI_REQUEST_PART4);
        Platform platform = PlatformEnum.BJGONGCAI.getPlatform();
        String s = platform.getPlatformAccount() + time + platform.getPlatformKey() + bodyBuffer.toString();
        String sign = MD5Util.getMD5String(s);
        bodyBuffer.append("</tickets></body>");
        buffer.append(sign).append("</digest><cmd>").append(BjGongCaiInterfaceEnum.TICKET_STATUS_QUERY.getCmd()).append(
                "</cmd></header><body>").append(bodyBuffer.toString()).append("</body></message>");
        buffer.append(bodyBuffer);
        JSONObject json = new JSONObject(Boolean.TRUE);
        json.putAll(params);
        String requestMsg = json.toString();
        log.info("[QueryTicket] : message about to be sent ==> " + requestMsg);
        return requestMsg;
    }

    @Override
    protected String sendMessage(String requestMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        Game game = GameCache.getGame(gameType);
        String doBetUrl = PlatformEnum.BJGONGCAI.getDoBetUrl();
        String url = null;
        if (Objects.equal(Integer.valueOf(1), game.getType())) {
            url = doBetUrl + jcUrl;
        }
        return HttpServiceUtils.sendRequest(url, HttpParamDto.DEFAULT_CONNECT_TIME_OUT, HttpParamDto
                .DEFAULT_READ_TIME_OUT, HttpParamDto.CHARSET_GBK, false, null, HttpParamDto.DEFAULT_CHARSET);
    }

    @Override
    protected String parseResponseMessage(String responseMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        log.info("BjGongCaiQueryTicketStatusHandler return response " + responseMessage);
        try {
            Document document = DocumentHelper.parseText(responseMessage);
            Element root = document.getRootElement();
            Element body = root.element("body");
            Element response = body.element("response");
            String code = response.attributeValue("code");
            if (!Strings.isNullOrEmpty(code) && Objects.equal(BjGongCaiInterfaceEnum.DO_BET_RECEIVE_SUCCESS_CODE, code)) {
                Element tickets = response.element("tickets");
                List<Element> ticketIdsElement = tickets.elements("ticket");
                List<BetResult> resultList = Lists.newArrayList();
                for (Element e : ticketIdsElement) {
                    String ticketId = e.attributeValue("ticketid");
                    BetResult betResult = new BetResult(Long.parseLong(ticketId), ticketId, BettingTicket
                            .TICKET_STATUS_INIT);
                    Integer status = Integer.parseInt(e.attributeValue("status"));
                    if (BjGongCaiInterfaceEnum.TICKET_STATUS_QUERY_SUCCESS_CODE == status) {
                        betResult.setTicketStatus(TicketStatus.success.getValue());
                    }
                    resultList.add(betResult);
                }
                Long orderId = (Long) paramMap.get("orderId");
                List<Long> ticketIds = (List<Long>) paramMap.get("ticketIds");
                List<BetResult> checkResultList = checkQueryTicketListSize(ticketIds, resultList, gameType);
                if (null != checkResultList && !checkResultList.isEmpty()) {
                    resultList = checkResultList;
                }
                TICKET_STATUS_QUEUE.add(new ResendUpdateUnit(gameType, PlatformEnum.BJGONGCAI.getPlatformId(),
                        orderId, resultList));
            } else {
                log.error("BjGongCaiQueryTicketStatusHandler parse response is error, responseMessage: " + responseMessage);
            }
        } catch (Exception e) {
            log.error("BjGongCaiQueryTicketStatusHandler parse response is error, responseMessage: " + responseMessage + " , e:" + e, e);
        }finally {
            List<TicketDto> ticketList = (List<TicketDto>)paramMap.get("ticketList");
            List<Long> ids = ticketList.stream().map(ticketDto -> Long.valueOf(ticketDto.getTicketId())).collect(Collectors.toList());

            tblBettingTicketRepository.updateTicketStatusByTicketId(0, ids);
        }
        return null;
    }
}
