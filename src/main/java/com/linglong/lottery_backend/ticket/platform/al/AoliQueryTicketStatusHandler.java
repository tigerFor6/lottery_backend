package com.linglong.lottery_backend.ticket.platform.al;

import com.linglong.lottery_backend.ticket.cache.GameCache;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.platform.AbstractPlatformHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 */
@Component
@Slf4j
public class AoliQueryTicketStatusHandler extends AbstractPlatformHandler {

    @Value("${bjgc.jc.url}")
    private String jcUrl;

    @Override
    protected String createRequestMessage(Integer gameType, Map<String, Object> paramMap) {

        log.info("[QueryTicket] : AoliQueryTicketStatusHandler message about to be sent ==> ");
        return null;
    }

    @Override
    protected String sendMessage(String requestMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        Game game = GameCache.getGame(gameType);
        String doBetUrl = PlatformEnum.AOLI.getDoBetUrl();
        log.info("AoliQueryTicketStatusHandler sendMessage ==> : {}",doBetUrl);

        return null;
        //return HttpServiceUtils.sendRequest(url, HttpParamDto.DEFAULT_CONNECT_TIME_OUT, HttpParamDto
        //        .DEFAULT_READ_TIME_OUT, HttpParamDto.CHARSET_GBK, false, null, HttpParamDto.DEFAULT_CHARSET);
    }

    @Override
    protected String parseResponseMessage(String responseMessage, Integer gameType, Map<String, Object> paramMap) throws Exception {
        return null;
    }
}
