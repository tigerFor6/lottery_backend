package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 北京公彩 投注（数字彩）类型彩种 封装投注项
 */
public abstract class BjGongCaiSZCreqBetCode {

    @Value("${bjgc.szc.url}")
    private String url;

    //彩种类型-数字彩
    Integer TYPE = 2;

    protected StringBuffer createJCreqBody(List<TicketDto> tickets, Game game, Map<String, String> playCode, BjGongCaiSZCreqBetCode bjGongCaiSZCreqBetCode) {
        StringBuffer parmsBuffer = new StringBuffer();

        tickets.forEach(ticket -> {
            List<TicketNumber> ticketNumbers = ticket.getNumbers();

            parmsBuffer.append("<ticket ticketid=\"")
                    .append(ticket.getTicketId())
                    .append("\" playcode=\"").append(playCode.get(ticket.getPlayType()+ticket.getExtra()))
                    .append("\" amount=\"").append(ticket.getTicketAmount().multiply(new BigDecimal(100)).setScale(0))
                    .append("\" multiple=\"").append(ticket.getTimes()).append("\">");
            parmsBuffer.append(bjGongCaiSZCreqBetCode.formBetCode(ticketNumbers, game));
            parmsBuffer.append("</ticket>");
        });

        return parmsBuffer;
    }

    protected String formBetCode(List<TicketNumber> ticketNumbers, Game game){
        StringBuffer betCodeBuffer = new StringBuffer();
        ticketNumbers.forEach(ticketNumber -> {
            String[] lotteryNumbers = ticketNumber.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR + "|" + CommonConstant.COMMON_AT_STR);
            String[] balls = lotteryNumbers[1].split(CommonConstant.SPACE_SPLIT_STR);
            for (int i = 0; i < balls.length; i++) {
                betCodeBuffer.append("<betCode>")
                        .append(balls[i]
                                .replaceAll(CommonConstant.COMMON_ESCAPE_STR+CommonConstant.COMMON_VERTICAL_STR, CommonConstant.POUND_SPLIT_STR)
                                .replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.COMMON_AT_STR))
                        .append("</betCode>");
            }
        });
        return betCodeBuffer.toString();
    }

    protected String getUrl() {
        return url;
    }
}
