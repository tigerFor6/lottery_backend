package com.linglong.lottery_backend.ticket.platform.bjgc;

import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.bean.HttpParamDto;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.AbstractPlatformHandler;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.BjGongCaiReqBody;
import com.linglong.lottery_backend.ticket.repository.TblBettingTicketRepository;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.lottery_backend.utils.HttpServiceUtils;
import com.linglong.lottery_backend.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ynght on 2019-04-26
 */
@Component
@Slf4j
public class BjGongCaiDoBetHandler extends AbstractPlatformHandler {

    @Autowired
    BjGongCaiReqBody bjGongCaiReqBody;

    @Autowired
    TblBettingTicketRepository tblBettingTicketRepository;

    @Override
    protected String createRequestMessage(Integer gameType, Map<String, Object> paramMap) {
        List<TicketDto> tickets = (List<TicketDto>) paramMap.get("ticketList");
        Timestamp current = DateUtil.getCurrentTimestamp();
        String time = DateUtil.formatTime(current, DateUtil.DATE_FORMAT_YYYYMMDDHHMMSS);
        StringBuffer buffer = new StringBuffer(BjGongCaiInterfaceEnum.getXmlPre(gameType, time));

//        Game game = GameCache.getGame(gameType);
//        BjGongCaiGameEnum gameEnum = BjGongCaiGameEnum.getByGameEn(game.getGameEn());
                        //play=" .append(gameEnum.getPlay()) "

        StringBuffer bodyBuffer = bjGongCaiReqBody.getReqMessageBody(gameType, tickets);

//        StringBuffer bodyBuffer = createReqParms(gameType, gameEnum, tickets);

        Platform platform = PlatformEnum.BJGONGCAI.getPlatform();
        String s = platform.getPlatformAccount() + time + platform.getPlatformKey() + bodyBuffer.toString();
        String sign = MD5Util.getMD5String(s);
        buffer.append(sign).append("</digest><cmd>").append(BjGongCaiInterfaceEnum.SPORT_GAME_DO_BET.getCmd()).append(
                "</cmd></header>").append(bodyBuffer.toString()).append("</message>");
        log.info("[DoBet]:message about to be sent with signature is : " + buffer.toString());
        return buffer.toString();
    }

    /**
     * 封装请求数据
     * @param gameType
     * @param tickets
     * @return
     */
    private StringBuffer createReqParms(Integer gameType, BjGongCaiGameEnum gameEnum, List<TicketDto> tickets) {
        StringBuffer parmsBuffer = new StringBuffer();
        if(gameType.equals(AoliEnum.JCZQ.getCode()) || gameType.equals(AoliEnum.JCLQ.getCode())) {
            parmsBuffer.append("<body><order ").append("lottid=\"").append(gameEnum.getGid()).append("\">");
            parmsBuffer.append(createJCZQ_LQreqBody(tickets, gameEnum));

        }else if(gameType.equals(AoliEnum.DLT.getCode()) || gameType.equals(AoliEnum.PL3.getCode())) {
            parmsBuffer.append("<body><order ")
                    .append("issue=\"").append(tickets.get(0).getNumbers().get(0).getPeriod())
                    .append("\" lottid=\"").append(gameEnum.getGid()).append("\">");
            parmsBuffer.append(createDLT_PL3reqBody(tickets, gameEnum));

        }
        parmsBuffer.append("</order></body>");
        return parmsBuffer;
    }

    /**
     * 封装竞猜足球、篮球请求数据
     * @param tickets
     * @param gameEnum
     * @return
     */
    private StringBuffer createJCZQ_LQreqBody(List<TicketDto> tickets, BjGongCaiGameEnum gameEnum) {
        StringBuffer parmsBuffer = new StringBuffer();
        for (TicketDto ticketDto : tickets) {
            String formatBetCode = gameEnum.formBetCode(ticketDto);
            String[] formatArray = formatBetCode.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_VERTICAL_STR);
            //String extra = ticketDto.getExtra().substring(0, ticketDto.getExtra().indexOf("_"));
            String extra = ticketDto.getExtra().replace(CommonConstant.COMMON_SPLIT_STR, CommonConstant.BLANK_STRING);
            parmsBuffer.append("<ticket ticketid=\"").append(ticketDto.getTicketId()).append("\" play=\"").append(formatArray[1]).append("\" " +
                    "playcode=\"").append(extra)/*.append(ticketDto.getTicketAmount().intValue() / ticketDto.getTimes() / 2)*/.append(
                    "\" " + "amount=\"").append(ticketDto.getTicketAmount().multiply(new BigDecimal("100")).longValue()).append("\" multiple=\"").append(ticketDto.getTimes())
                    .append("\" seri=\"\">");
            parmsBuffer.append(formatArray[0]).append("</ticket>");
        }
        return parmsBuffer;
    }

    /**
     * 封装排列三、大乐透请求数据
     * @param tickets
     * @param gameEnum
     * @return
     */
    private StringBuffer createDLT_PL3reqBody(List<TicketDto> tickets, BjGongCaiGameEnum gameEnum) {
        StringBuffer parmsBuffer = new StringBuffer();

        tickets.forEach(ticket -> {
            List<TicketNumber> ticketNumbers = ticket.getNumbers();

            parmsBuffer.append("<ticket ticketid=\"")
                    .append(ticket.getTicketId())
                    .append("\" playCode=\"").append(BjGongCaiGameEnum.PLAY_CODE_MAP.get(gameEnum.getGid()).get(ticket.getPlayType()+ticket.getExtra()))
                    .append("\" amount=\"").append(ticket.getTicketAmount().multiply(new BigDecimal(100)).setScale(0))
                    .append("\" multiple=\"").append(ticket.getTimes()).append("\">");

            ticketNumbers.forEach(ticketNumber -> {
                String[] lotteryNumbers = ticketNumber.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
                String[] balls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
                for (int i = 0; i < balls.length; i++) {
                    parmsBuffer.append("<betCode>")
                            .append(balls[i].replaceAll(CommonConstant.COMMON_ESCAPE_STR+CommonConstant.COMMON_VERTICAL_STR, CommonConstant.POUND_SPLIT_STR))
                            .append("</betCode>");
                }
            });

            parmsBuffer.append("</ticket>");
        });

        return parmsBuffer;
    }

    @Override
    protected String sendMessage(String requestMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        String doBetUrl = PlatformEnum.BJGONGCAI.getDoBetUrl();
        String url = bjGongCaiReqBody.getReqUrl(gameType);
        String content = "cmd=" + BjGongCaiInterfaceEnum.SPORT_GAME_DO_BET.getCmd() + "&msg=" + requestMessage;
        log.warn("Bj sendMessage: {}", doBetUrl+url+content);
        return HttpServiceUtils.sendPostRequest(url,
                content, HttpParamDto.DEFAULT_CHARSET, HttpParamDto.DEFAULT_CONNECT_TIME_OUT,
                HttpParamDto.DEFAULT_READ_TIME_OUT, false, "application/x-www-form-urlencoded", false);
    }

    @Override
    protected String parseResponseMessage(String responseMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        List<TicketDto> ticketList = (List<TicketDto>)paramMap.get("ticketList");
        List<Long> ids = ticketList.stream().map(ticketDto -> Long.valueOf(ticketDto.getTicketId())).collect(Collectors.toList());

        tblBettingTicketRepository.updateTicketStatusByTicketId(0, ids);
        return null;
    }
}
