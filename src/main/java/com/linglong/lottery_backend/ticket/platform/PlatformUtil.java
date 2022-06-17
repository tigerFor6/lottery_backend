package com.linglong.lottery_backend.ticket.platform;

import com.google.common.collect.Sets;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.bean.TicketDto;
import com.linglong.lottery_backend.ticket.entity.bo.TicketNumber;
import com.linglong.lottery_backend.ticket.platform.al.AoliGameEnum;
import com.linglong.lottery_backend.ticket.platform.bjgc.BjGongCaiGameEnum;
import com.linglong.lottery_backend.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ynght on 2019-04-26
 */
@Slf4j
public class PlatformUtil {

    public static Map<String, String> dateEnum = new ConcurrentHashMap<>();

    static {
        dateEnum.put("周一", "1");
        dateEnum.put("周二", "2");
        dateEnum.put("周三", "3");
        dateEnum.put("周四", "4");
        dateEnum.put("周五", "5");
        dateEnum.put("周六", "6");
        dateEnum.put("周日", "7");
    }

    public static String getBjGongCaiJcCommonExtraBetCode(String gameEn, TicketDto ticket) {
        List<TicketNumber> numbers = ticket.getNumbers();
        Map<String, Map<String, String>> betCodeMap = null;
        if (gameEn.startsWith("jczq")) {
            betCodeMap = BjGongCaiGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP;
        } else {
            betCodeMap = BjGongCaiGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP;
        }
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
                String s = dateEnum.get(targetWeek);
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
        String play = BjGongCaiGameEnum.getByGameEn(gameEn).getPlay();
        if (ifMix) {
            ticketExtra = play;
        }
        return numberBuffer.append("|").append(ticketExtra).toString();
    }

    public static String getAoliJcCommonExtraBetCode(String gameEn, TicketDto ticket) {
        log.info("PlatformUtil getAoliJcCommonExtraBetCode ------");
        List<TicketNumber> numbers = ticket.getNumbers();
        Map<String, Map<String, String>> betCodeMap = null;
        if (gameEn.startsWith("jczq")) {
            betCodeMap = AoliGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_FOOTBALL_MAP;
        } else {
            betCodeMap = AoliGameEnum.CHAR_EXTRA_MAPPING_BET_CODE_BASKETBALL_MAP;
        }
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
                numberBuffer.append(date).append(matchAndCode[0].substring(8)).append(CommonConstant.COMMON_COLON_STR);
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
                numberBuffer.append(CommonConstant.SEMICOLON_SPLIT_STR);
            }
            numberBuffer = numberBuffer.deleteCharAt(numberBuffer.length() - 1);
            numberBuffer.append("</betCode>");
        }
        log.info("PlatformUtil getAoliJcCommonExtraBetCode: {}",numberBuffer);
        String play = AoliGameEnum.getByGameEn(gameEn).getPlay();
        if (ifMix) {
            ticketExtra = play;
        }
        return numberBuffer.append("|").append(ticketExtra).toString();
    }
}
