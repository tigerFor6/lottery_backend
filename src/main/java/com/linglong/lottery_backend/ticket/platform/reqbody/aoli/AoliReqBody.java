package com.linglong.lottery_backend.ticket.platform.reqbody.aoli;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.reqbody.aoli.jc.AoLiLQreqParms;
import com.linglong.lottery_backend.ticket.platform.reqbody.aoli.jc.AoLiZQreqParms;
import com.linglong.lottery_backend.ticket.platform.reqbody.aoli.szc.*;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc.BjGongCaiLQreqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc.BjGongCaiZQreqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc.BjGongCaiDLTreqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc.BjGongCaiPL3reqPrams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Aoli出票请求
 */
@Service
public class AoliReqBody {

    @Autowired
    private AoLiLQreqParms aoLiLQreqParms;

    @Autowired
    private AoLiZQreqParms aoLiZQreqParms;

    @Autowired
    private AoLiSSQreqParms aoLiSSQreqParms;

    @Autowired
    private AoLiSHX115reqParms aoLiSHX115reqParms;

    @Autowired
    private AoLiPL3reqParms aoLiPL3reqParms;

    @Autowired
    private AoLiDLTreqParms aoLiDLTreqParms;

    @Autowired
    private AoLiPL5reqParms aoLiPL5reqParms;

    public StringBuffer getReqMessageBody(Integer gameType, List<TicketDto> tickets) {
        Game game = GameCache.getGame(gameType);
        if(aoLiZQreqParms.GAME_TYPE.equals(gameType)) {
            return aoLiZQreqParms.createJCZQreqBody(tickets, game);

        }else if(aoLiLQreqParms.GAME_TYPE.equals(gameType)) {
            return aoLiLQreqParms.createJCLQreqBody(tickets, game);

        }else if(aoLiSSQreqParms.GAME_TYPE.equals(gameType)) {
            return aoLiSSQreqParms.createSSQreqBody(tickets, game);

        }else if(aoLiSHX115reqParms.GAME_TYPE.equals(gameType)) {
            return aoLiSHX115reqParms.createSHX115reqBody(tickets, game);

        }else if(aoLiPL3reqParms.GAME_TYPE.equals(gameType)) {
            return aoLiPL3reqParms.createPL3reqBody(tickets, game);

        }else if(aoLiDLTreqParms.GAME_TYPE.equals(gameType)) {
            return aoLiDLTreqParms.createDLTreqBody(tickets, game);

        }else if(aoLiPL5reqParms.GAME_TYPE.equals(gameType)) {
            return aoLiPL5reqParms.createPL5reqBody(tickets, game);

        }

        return null;
    }

    public String getReqUrl() {
        return PlatformEnum.AOLI.getDoBetUrl();
    }
}
