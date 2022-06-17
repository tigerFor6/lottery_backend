package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.platform.bjgc.BjGongCaiGameEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.CreateJCLQreqParms;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 北京公彩封装竞猜（篮球）请求数据
 */
@Service
public class BjGongCaiLQreqPrams extends BjGongCaiJCreqBetCode implements CreateJCLQreqParms {

    @Override
    public StringBuffer createParms(List<TicketDto> tickets) {
        Game game = GameCache.getGame(GAME_TYPE);
        BjGongCaiGameEnum gameEnum = BjGongCaiGameEnum.getByGameEn(game.getGameEn());
        StringBuffer parmsBuffer = new StringBuffer();
        parmsBuffer.append("<body><order ").append("lottid=\"").append(gameEnum.getGid()).append("\">");
        parmsBuffer.append(createJCLQreqBody(tickets, game));
        parmsBuffer.append("</order></body>");
        return parmsBuffer;
    }

    @Override
    public StringBuffer createJCLQreqBody(List<TicketDto> tickets, Game game) {
        return super.createJCreqBody(tickets, game, BjGongCaiGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP);
    }

    public String getUrl(){
        return super.getUrl();
    }
}
