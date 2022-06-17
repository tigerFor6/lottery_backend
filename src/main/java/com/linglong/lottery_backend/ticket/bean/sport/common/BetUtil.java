package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BetUtil {


    //-2.5|88:86|1.77  //比分主队在前，客在后, 返回{88, 86, -2.5, 1.77}//比赛取消返回{-1}
    public static BigDecimal[] parserWinnerCode(String winningCode) {//-2.5|88:86|1.77  //比分主队在前，客在后
        try {
            if (winningCode == null || winningCode.equals(RuleConstant.CHAR_CANCEL)) {
                BigDecimal[] result = new BigDecimal[1];
                result[0] = new BigDecimal(-1);
                return result;
            }

            BigDecimal[] result = new BigDecimal[4];
            String[] parts = winningCode.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_VERTICAL_STR);
            //让球
            result[2] = new BigDecimal(parts[0]).setScale(1, RoundingMode.DOWN);
            String[] points = parts[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);

            //主队得分
            result[0] = new BigDecimal(points[0]).setScale(0, RoundingMode.DOWN);

            //客队得分
            result[1] = new BigDecimal(points[1]).setScale(0, RoundingMode.DOWN);

            //最终SP
            result[3] = new BigDecimal(parts[2]).setScale(3, RoundingMode.DOWN);

            return result;
        } catch (Exception e) {
            log.error("比赛开奖信息格式不正确：" + winningCode, e);
            throw new BusinessException("比赛开奖信息格式不正确：" + winningCode);
        }
    }


    //中心返回经接口转换后的格式
    //307.308	//2.54:3.12							2.54:0:3.12
    //306		//2.54:3.12,181.5					2.54:0:3.12,170.5
    //309		//2.54:3.12,+7.5					2.54:0:3.12,-2.5

    //库中投注号
    //201106256301:3.0 					201106256302:3.0

    //库中经过转换的格式
    //307.308	//201106263201|3:2.54		 		201106273202|3:2.54|0:3.12
    //307.308	//201106263201|3:2.54|0:3.12 		201106273202|3:2.54|0:3.12
    //306		//201106263201|3:2.54|0:3.12,+7.5 	201106273202|3:2.54|0:3.12,-2.5
    //309		//201106263201|3:2.54|0:3.12,181.5 	201106273202|3:2.54|0:3.12,170.5


    //201106263201|0 -> {2.54}
    //201106263201|3 -> {2.54, 181.5}
    public static Map<String, BigDecimal[]> parserFormatedSP(String formatedSP) {//将合并后的出票信息转成map
        try {
            Map<String, BigDecimal[]> map = new HashMap<String, BigDecimal[]>();
            String[] elements = formatedSP.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
            for (String element : elements) {

                String[] entries = element.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMA_SPLIT_STR);
                String[] units = entries[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
                        .COMMON_VERTICAL_STR);

                for (int i = 1; i < units.length; i++) {
                    String key = units[0] + CommonConstant.COMMON_VERTICAL_STR;
                    String[] temp = units[i].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
                    key += temp[0];

                    BigDecimal[] values;
                    if (entries.length > 1) {
                        values = new BigDecimal[2];
                        values[1] = new BigDecimal(entries[1]);
                    } else {
                        values = new BigDecimal[1];
                    }
                    values[0] = new BigDecimal(temp[1]);
                    map.put(key, values);
                }
            }
            return map;
        } catch (Exception e) {
            log.error("库中出票SP格式有误！" + formatedSP, e);
            throw new BusinessException("库中出票SP格式有误！");
        }
    }


    //前提是：投注号和sp值的位置顺序是一一对应的
    //这里stakeNumber被认为格式已经合法的拆过单的
    public static String fusion(String stakeNumber, String extra, String sp) {//合并投注号和出票的一些信息，开奖用
        try {

            String[] tempStakes = stakeNumber.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
            String[] tempSps = sp.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.SPACE_SPLIT_STR);
            if (tempStakes.length != tempSps.length)
                throw new IllegalArgumentException("投注号和出票SP的投注数量不对应1！");

            String[] extras = extra.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_SPLIT_STR);
            if (Integer.parseInt(extras[0]) != tempSps.length)
                throw new IllegalArgumentException("投注号场次数和串关数量不对应2！");

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < tempStakes.length; i++) {
                String[] elementsNumber = tempStakes[i].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
                        .COMMON_COLON_STR);
                String[] elementsSp = tempSps[i].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
                        .COMMA_SPLIT_STR);

                sb.append(elementsNumber[0]).append(CommonConstant.COMMON_VERTICAL_STR);

                String[] sps = elementsSp[0].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_COLON_STR);
                String[] numbers = elementsNumber[1].split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
                        .COMMON_DOT_STR);

                if (numbers.length != sps.length)
                    throw new IllegalArgumentException("投注号和出票SP的投注数量不对应3！");

                for (int j = 0; j < sps.length; j++) {
                    sb.append(numbers[j]).append(CommonConstant.COMMON_COLON_STR).append(sps[j]);
                    if (j != sps.length - 1)
                        sb.append(CommonConstant.COMMON_VERTICAL_STR);
                }

                if (elementsSp.length == 1) {

                } else if (elementsSp.length == 2) {
                    sb.append(CommonConstant.COMMA_SPLIT_STR).append(elementsSp[1]);
                } else {
                    throw new IllegalArgumentException("投注号和出票SP的投注数量不对应4！");
                }

                if (i != tempStakes.length - 1)
                    sb.append(CommonConstant.SPACE_SPLIT_STR);
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("投注号或者出票SP格式有误：" + stakeNumber, e);
            throw new BusinessException("投注号或者出票SP格式有误：" + e.getMessage());
        }
    }

    /**
     * sp值分成 主队 和 客队
     *
     * @param sp like:1.500|2.080
     * @return
     */
    public static BigDecimal[] parserSp(String sp) {
        try {
            BigDecimal[] result = new BigDecimal[2];
            if (StringUtils.isBlank(sp)) {
                result[0] = new BigDecimal(0).setScale(2, RoundingMode.DOWN);
                result[1] = new BigDecimal(0).setScale(2, RoundingMode.DOWN);
                return result;
            }
            String[] sps = sp.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant.COMMON_VERTICAL_STR);
            if (sps == null || sps.length != 2) {
                result[0] = new BigDecimal(0).setScale(2, RoundingMode.DOWN);
                result[1] = new BigDecimal(0).setScale(2, RoundingMode.DOWN);
                return result;
            }

            //主队sp值
            result[0] = new BigDecimal(sps[0]).setScale(2, RoundingMode.DOWN);
            //客队sp值
            result[1] = new BigDecimal(sps[1]).setScale(2, RoundingMode.DOWN);
            return result;
        } catch (Exception e) {
            log.error("篮彩sp值信息格式不正确：" + sp, e);
            throw new BusinessException("篮彩sp值信息格式不正确：" + sp);
        }
    }
}
