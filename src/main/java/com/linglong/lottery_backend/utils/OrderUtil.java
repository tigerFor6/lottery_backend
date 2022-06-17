package com.linglong.lottery_backend.utils;

import com.linglong.lottery_backend.order.model.order_model.PlayType;
import com.linglong.lottery_backend.order.service.bean.Play;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: qihua.li
 * @since: 2019-04-08
 */
@Slf4j
@Component
public class OrderUtil {

    public List<Play> oddsTemplate(String odds, String[] databaseOdds, String[] template) {
//        log.info("dataBaseOdds:{}", Arrays.toString(databaseOdds));
        List<Play> playList = new ArrayList<>();
        List<String> listTemplate = Stream.of(template).collect(Collectors.toList());
        String[] oddsArr = odds.split(",");
        for (String odd : oddsArr) {
            Play p = new Play();
            int i = listTemplate.indexOf(odd);
//            log.info("i:{}", i);
            p.setItem(odd);
            p.setOdds(databaseOdds[i]);
//            log.info("odds:{}", databaseOdds[i]);
            p.setResult("--");
            playList.add(p);
        }
        return playList;
    }

    public String[] getBetTemplate(String playCode, Integer lotteryType) {
        String[] template = null;
        if (lotteryType == 1) {
            if (PlayType.SPF.getType().equals(playCode)) {
                template = new String[]{"3", "1", "0"};
            } else if (PlayType.RQSPF.getType().equals(playCode)) {
                template = new String[]{"3", "1", "0"};
            } else if (PlayType.JQS.getType().equals(playCode)) {
                template = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
            } else if (PlayType.BIFEN.getType().equals(playCode)) {
                template = new String[]{"10", "20", "21", "30", "31", "32", "40", "41", "42", "50", "51", "52", "90",
                        "00", "11", "22", "33", "99", "01", "02", "12", "03", "13", "23", "04", "14", "24", "05", "15", "25", "09"};
            } else if (PlayType.BQC.getType().equals(playCode)) {
                template = new String[]{"33", "31", "30", "13", "11", "10", "03", "01", "00"};
            }
        } else if (lotteryType == 2) {
            template = new String[]{"1", "2"};
        }
        return template;
    }

    public String getPlayCode(String key, Integer lotteryType) {
        String playCode = "";
        if (lotteryType == 1) {
            if (PlayType.SPF.name().equalsIgnoreCase(key)) {
                playCode = PlayType.SPF.getType();
            } else if (PlayType.RQSPF.name().equalsIgnoreCase(key)) {
                playCode = PlayType.RQSPF.getType();
            } else if (PlayType.JQS.name().equalsIgnoreCase(key)) {
                playCode = PlayType.JQS.getType();
            } else if (PlayType.BQC.name().equalsIgnoreCase(key)) {
                playCode = PlayType.BQC.getType();
            } else if (PlayType.BIFEN.name().equalsIgnoreCase(key)) {
                playCode = PlayType.BIFEN.getType();
            }
        } else if (lotteryType == 2) {
            if (PlayType.SF.name().equalsIgnoreCase(key)) {
                playCode = PlayType.SF.getType();
            } else if (PlayType.RFSF.name().equalsIgnoreCase(key)) {
                playCode = PlayType.RFSF.getType();
            } else if (PlayType.DXF.name().equalsIgnoreCase(key)) {
                playCode = PlayType.DXF.getType();
            }
        }
        return playCode;
    }

    public String getBetItem(String item, String playCode) {
        String[] template = new String[0];
        String[] oddsTemple = new String[0];
        if (PlayType.SPF.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"3", "1", "0"};
            oddsTemple = new String[]{"主胜", "平", "主负"};
        } else if (PlayType.RQSPF.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"3", "1", "0"};
            oddsTemple = new String[]{"主胜", "平", "主负"};
        } else if (PlayType.JQS.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"0", "1", "2", "3", "4", "5", "6", "7"};
            oddsTemple = new String[]{"0球", "1球", "2球", "3球", "4球", "5球", "6球", "7+"};
        } else if (PlayType.BIFEN.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"10", "20", "21", "30", "31", "32", "40", "41", "42", "50", "51", "52", "90",
                    "00", "11", "22", "33", "99", "01", "02", "12", "03", "13", "23", "04", "14", "24", "05", "15", "25", "09"};
            oddsTemple = new String[]{"1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0", "5:1", "5:2", "胜其他",
                    "0:0", "1:1", "2:2", "3:3", "平其他", "0:1", "0:2", "1:2", "0:3", "1:3", "2:3", "0:4", "1:4", "2:4", "0:5", "1:5", "2:5", "负其他"};
        } else if (PlayType.BQC.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"33", "31", "30", "13", "11", "10", "03", "01", "00"};
            oddsTemple = new String[]{"胜胜", "胜平", "胜负", "平胜", "平平", "平负", "负胜", "负平", "负负"};
        } else if (PlayType.SF.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"2", "1"};
            oddsTemple = new String[]{"客胜", "主胜"};
        } else if (PlayType.RFSF.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"2", "1"};
            oddsTemple = new String[]{"客胜", "主胜"};
        } else if (PlayType.DXF.name().equalsIgnoreCase(playCode)) {
            template = new String[]{"2", "1"};
            oddsTemple = new String[]{"小", "大"};
        }
        int index = Stream.of(template).collect(Collectors.toList()).indexOf(item);
        return oddsTemple[index];
    }
}
