package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.platform.bjgc.BjGongCaiGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateDLTreqParms;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 北京公彩封装竞猜（大乐透）请求数据
 */
@Service
public class BjGongCaiDLTreqPrams extends BjGongCaiSZCreqBetCode implements CreateDLTreqParms {


    @Override
    public StringBuffer createParms(List<TicketDto> tickets) {
        Game game = GameCache.getGame(GAME_TYPE);
        BjGongCaiGameEnum gameEnum = BjGongCaiGameEnum.getByGameEn(game.getGameEn());
        StringBuffer parmsBuffer = new StringBuffer();
        parmsBuffer.append("<body><order ")
                .append("issue=\"").append(tickets.get(0).getNumbers().get(0).getPeriod())
                .append("\" lottid=\"").append(gameEnum.getGid()).append("\">");
        parmsBuffer.append(createDLTreqBody(tickets, game));
        parmsBuffer.append("</order></body>");
        return parmsBuffer;
    }

    @Override
    public StringBuffer createDLTreqBody(List<TicketDto> tickets, Game game) {
        return this.createJCreqBody(tickets, game, BjGongCaiGameEnum.DLT_PLAY_CODE_MAP, this);
    }

    @Override
    protected String formBetCode(List<TicketNumber> ticketNumbers, Game game) {
        return super.formBetCode(ticketNumbers, game);
    }

    public String getUrl(){
        return super.getUrl();
    }
}
