package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc;

import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc.BjGongCaiLQreqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc.BjGongCaiZQreqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc.BjGongCaiDLTreqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc.BjGongCaiPL3reqPrams;
import com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.szc.BjGongCaiPL5reqPrams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 北京公彩出票请求
 */
@Service
public class BjGongCaiReqBody {

    @Autowired
    BjGongCaiLQreqPrams bjGongCaiJCLQreqPrams;

    @Autowired
    BjGongCaiZQreqPrams bjGongCaiJCZQreqPrams;

    @Autowired
    BjGongCaiDLTreqPrams bjGongCaiDLTreqPrams;

    @Autowired
    BjGongCaiPL3reqPrams bjGongCaiPL3reqPrams;

    @Autowired
    BjGongCaiPL5reqPrams bjGongCaiPL5reqPrams;

    public StringBuffer getReqMessageBody(Integer gameType, List<TicketDto> tickets) {
        if(bjGongCaiJCLQreqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiJCLQreqPrams.createParms(tickets);

        }else if(bjGongCaiJCZQreqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiJCZQreqPrams.createParms(tickets);

        }else if(bjGongCaiDLTreqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiDLTreqPrams.createParms(tickets);

        }else if(bjGongCaiPL3reqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiPL3reqPrams.createParms(tickets);

        }else if(bjGongCaiPL5reqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiPL5reqPrams.createParms(tickets);

        }
        return null;
    }

    public String getReqUrl(Integer gameType) {
        if(bjGongCaiJCLQreqPrams.GAME_TYPE.equals(gameType) || bjGongCaiJCZQreqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiJCLQreqPrams.getUrl();

        }else if(bjGongCaiDLTreqPrams.GAME_TYPE.equals(gameType) || bjGongCaiPL3reqPrams.GAME_TYPE.equals(gameType)
                    || bjGongCaiPL5reqPrams.GAME_TYPE.equals(gameType)) {
            return bjGongCaiDLTreqPrams.getUrl();
        }
        return null;
    }

}
