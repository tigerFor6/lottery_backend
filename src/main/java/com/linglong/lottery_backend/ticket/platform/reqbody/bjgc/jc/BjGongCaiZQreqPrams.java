package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.platform.bjgc.BjGongCaiGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateJCZQreqParms;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 北京公彩封装竞猜（足球）请求数据
 */
@Service
public class BjGongCaiZQreqPrams extends BjGongCaiJCreqBetCode implements CreateJCZQreqParms {

    @Override
    public StringBuffer createJCZQreqBody(List<TicketDto> tickets, Game game) {
        return super.createJCreqBody(tickets, game, BjGongCaiGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP);
    }

    @Override
    public StringBuffer createParms(List<TicketDto> tickets) {
        Game game = GameCache.getGame(GAME_TYPE);
        BjGongCaiGameEnum gameEnum = BjGongCaiGameEnum.getByGameEn(game.getGameEn());
        StringBuffer parmsBuffer = new StringBuffer();
        parmsBuffer.append("<body><order ").append("lottid=\"").append(gameEnum.getGid()).append("\">");
        parmsBuffer.append(createJCZQreqBody(tickets, game));
        parmsBuffer.append("</order></body>");
        return parmsBuffer;
    }
}
