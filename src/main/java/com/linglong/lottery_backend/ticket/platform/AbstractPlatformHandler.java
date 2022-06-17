package com.linglong.lottery_backend.ticket.platform;

import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.bean.BetResult;
import com.linglong.lottery_backend.ticket.bean.ResendUpdateUnit;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.service.cron.ResendUpdateCoordinator;
import com.linglong.lottery_backend.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by ynght on 2019-04-26
 */
public abstract class AbstractPlatformHandler {
    private static final Logger log = LoggerFactory.getLogger(AbstractPlatformHandler.class);
    protected final BlockingQueue<ResendUpdateUnit> TICKET_STATUS_QUEUE = ResendUpdateCoordinator.RESEND_RESULT_QUEUE;


    public static String generateTicketIdString(List<TicketDto> tickets) {
        StringBuffer buffer = new StringBuffer();
        for (TicketDto ticketDto : tickets) {
            buffer.append(ticketDto.getTicketId()).append(CommonConstant.COMMON_VERTICAL_STR);
        }
        return buffer.toString();
    }

    public static List<BetResult> checkQueryTicketListSize(List<Long> ticketIds, List<BetResult> betResults, Integer
            gameType) {
        if (ticketIds.size() > betResults.size()) {
            /* 查询票张数核验异常*/
            log.info("[QueryTicket] ： 查询票出票方返回张数核验异常 gameType:" + gameType + CommonUtil
                    .mergeUnionKey(ticketIds) + ", query result:" + CommonUtil.mergeUnionKey(betResults));
            /* 初始化票状态，添加到betResults*/
            List<Long> ticketIdList = new ArrayList<>();
            for (Long ticketId : ticketIds) {
                Boolean flag = Boolean.FALSE;
                for (BetResult betResult : betResults) {
                    if (ticketId.equals(betResult.getTicketId())) {
                        flag = Boolean.TRUE;
                        break;
                    }
                }
                if (!flag) {
                    ticketIdList.add(ticketId);
                }
            }
            for (Long ticketId : ticketIdList) {
                BetResult betResult = new BetResult(ticketId, null, BettingTicket.TICKET_STATUS_INIT);
                betResults.add(betResult);
            }
            return betResults;
        }
        return null;
    }

    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 60000L))
    public Object handle(Integer gameType, Map<String, Object> paramMap) throws Exception {
        Long requestFlag = System.currentTimeMillis();
        paramMap.put(CommonConstant.REQUEST_FLAG, requestFlag);
        String requestMessage = createRequestMessage(gameType, paramMap);
        String responseMessage = sendMessage(requestMessage, gameType, paramMap);
        log.warn("res message body: {}", responseMessage);
        return parseResponseMessage(responseMessage, gameType, paramMap);
    }

    protected abstract String createRequestMessage(Integer gameType, Map<String, Object> paramMap);

    protected abstract String sendMessage(String requestMessage, Integer gameType, Map<String, Object>
            paramMap) throws Exception;

    protected abstract String parseResponseMessage(String responseMessage, Integer gameType, Map<String,
            Object> paramMap) throws Exception;

}
