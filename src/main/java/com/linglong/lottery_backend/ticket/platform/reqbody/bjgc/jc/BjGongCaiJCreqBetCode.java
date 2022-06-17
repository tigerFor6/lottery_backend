package com.linglong.lottery_backend.ticket.platform.reqbody.bjgc.jc;

import com.google.common.collect.Sets;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.platform.PlatformUtil;
import com.linglong.lottery_backend.ticket.platform.bjgc.BjGongCaiGameEnum;
import com.linglong.lottery_backend.utils.DateUtil;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 北京公彩 投注（竞猜）类型彩种 封装投注项
 */
public abstract class BjGongCaiJCreqBetCode {

    @Value("${bjgc.jc.url}")
    private String url;

    //彩种类型-竞彩
    Integer TYPE = 1;

    protected StringBuffer createJCreqBody(List<TicketDto> tickets, Game game, Map<String, Map<String, String>> betCodeMap) {
        StringBuffer parmsBuffer = new StringBuffer();
        for (TicketDto ticketDto : tickets) {
            String formatBetCode = formBetCode(ticketDto, game, betCodeMap);
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

    protected String formBetCode(TicketDto ticket, Game game, Map<String, Map<String, String>> betCodeMap){
        List<TicketNumber> numbers = ticket.getNumbers();
        StringBuffer numberBuffer = new StringBuffer();
        String[] split = null;
        Boolean ifMix = Boolean.FALSE;
        LinkedHashSet<String> extras = Sets.newLinkedHashSet();
        String ticketExtra = null;
        for (TicketNumber number : numbers) {
            numberBuffer.append("<betCode>");
            split = number.getLotteryNumber().split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_AT_STR);
            String[] allMatches = split[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
            for (String singleMatch : allMatches) {
                String[] matchAndCode = singleMatch.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
                String[] split1 = matchAndCode[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_WAVE_STR);
                extras.add(split1[0]);
            }
            for (String singleMatch : allMatches) {
                String[] matchAndCode = singleMatch.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
                String date = matchAndCode[0].substring(0, 8);
                Timestamp timestamp = DateUtil.formatString(date, DateUtil.DATE_FORMAT_YYYYMMDD);
                String targetWeek = DateUtil.getTargetWeek(timestamp);
                String s = PlatformUtil.dateEnum.get(targetWeek);
                numberBuffer.append(date).append(s).append(matchAndCode[0].substring(8)).append(CommonConstant.COMMON_COLON_STR);
//                if (Objects.equals(WordConstant.MULTIPLE, ticket.getPlayType())) {
                String[] split1 =
                        matchAndCode[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_WAVE_STR);
                ticketExtra = BjGongCaiGameEnum.CHAR_BET_EXTRA_MAP.get(split1[0]);
                String[] codes = split1[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.POUND_SPLIT_STR);
                if (extras.size() > 1) {
                    ifMix = Boolean.TRUE;
                    numberBuffer.append(ticketExtra).append(CommonConstant.COMMON_DASH_STR);
                }

                for (String code : codes) {
                    numberBuffer.append(betCodeMap.get(split1[0]).get(code)).append(CommonConstant.COMMA_SPLIT_STR);
                }
                numberBuffer = numberBuffer.deleteCharAt(numberBuffer.length() - 1);

//                }
                /*else {
                    for (String singleCode :
                            matchAndCode[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_WAVE_STR)) {
                        String[] extraAndCode = singleCode.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_WAVE_STR);
                        numberBuffer.append(betCodeMap.get(extraAndCode[0]).get(extraAndCode[1])).append(CommonConstant.COMMA_SPLIT_STR);
                    }
                }*/
//                numberBuffer = numberBuffer.deleteCharAt(numberBuffer.length() - 1);
                numberBuffer.append(CommonConstant.SEMICOLON_SPLIT_STR);
            }
            numberBuffer = numberBuffer.deleteCharAt(numberBuffer.length() - 1);
            numberBuffer.append("</betCode>");
        }

        String play = BjGongCaiGameEnum.getByGameEn(game.getGameEn()).getPlay();
        if (ifMix) {
            ticketExtra = play;
        }
        return numberBuffer.append("|").append(ticketExtra).toString();
    }


    protected String getUrl() {
        return url;
    }
}
