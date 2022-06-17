package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.platform.bjgc.BjGongCaiGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreatePL3reqParms;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreatePL5reqParms;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 北京公彩封装竞猜（排列3）请求数据
 */
@Service
public class BjGongCaiPL5reqPrams extends BjGongCaiSZCreqBetCode implements CreatePL5reqParms {

    @Override
    public StringBuffer createParms(List<TicketDto> tickets) {
        Game game = GameCache.getGame(GAME_TYPE);
        BjGongCaiGameEnum gameEnum = BjGongCaiGameEnum.getByGameEn(game.getGameEn());
        StringBuffer parmsBuffer = new StringBuffer();
        parmsBuffer.append("<body><order ")
                .append("issue=\"").append(tickets.get(0).getNumbers().get(0).getPeriod())
                .append("\" lottid=\"").append(gameEnum.getGid()).append("\">");
        parmsBuffer.append(createPL5reqBody(tickets, game));
        parmsBuffer.append("</order></body>");
        return parmsBuffer;
    }

    @Override
    public StringBuffer createPL5reqBody(List<TicketDto> tickets, Game game) {
        return super.createJCreqBody(tickets, game, BjGongCaiGameEnum.PL3_PLAY_CODE_MAP, this);
    }

    @Override
    protected String formBetCode(List<TicketNumber> ticketNumbers, Game game) {
        StringBuffer betCodeBuffer = new StringBuffer();
        ticketNumbers.forEach(ticketNumber -> {
            String[] lotteryNumbers = ticketNumber.getLotteryNumber().split(CommonConstant.COMMON_AT_STR);
            String[] balls = lotteryNumbers[0].split(CommonConstant.SPACE_SPLIT_STR);
            for (int i = 0; i < balls.length; i++) {
                betCodeBuffer.append("<betCode>")
                        .append(balls[i]
                                .replaceAll(CommonConstant.SEMICOLON_SPLIT_STR, CommonConstant.POUND_SPLIT_STR))
                        .append("</betCode>");
            }
        });
        return betCodeBuffer.toString();
    }

    public String getUrl(){
        return super.getUrl();
    }
}
