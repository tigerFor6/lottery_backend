package com.linglong.lottery_backend.utils;

import com.linglong.lottery_backend.order.model.order_model.PlayType;
import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetNumUtil {

    public static int loop(Integer[] arr, Integer[] typeArr){
        int count=0;
        for(Integer type:typeArr)
        {
            List<Integer> list = new ArrayList<Integer>();
            Generator.combination(arr)
                    .simple(type)
                    .stream()
                    .forEach(data->{
                        System.out.println(data);
                                int base=1;
                                for(int i=0;i<data.size();i++)
                                {
                                    base*=data.get(i);
                                }
                                list.add(base);
                            }
                    );
            count += list.stream().mapToInt(Integer::intValue).sum();

        }
        return count;
    }

    /**
     * 计算双色球注数
     */
    public static long twoColorLoop(String redStr,String blueStr,String twoColorType){
        long count=0;
        String[] blue = blueStr.split(",");
        int[] blueArr = Arrays.stream(blue).mapToInt(Integer::valueOf).toArray();
        if(PlayType.TWOCOLORTYPEONE.getType().equals(twoColorType)){
            //单式
            count = 1;
        }else if(PlayType.TWOCOLORTYPETWO.getType().equals(twoColorType)){
            //复式
            String[] red = redStr.split(",");
            int[] redArr = Arrays.stream(red).mapToInt(Integer::valueOf).toArray();
            count = factorial(redArr.length)/(factorial(redArr.length-6)*factorial(6))*blueArr.length;
        }else if(PlayType.TWOCOLORTYPETHREE.getType().equals(twoColorType)){
            //胆拖
            String[] st = redStr.split(";");
            int[] mainRedArr = Arrays.stream(st[0].split(",")).mapToInt(Integer::valueOf).toArray();
            int[] slaveRedArr = Arrays.stream(st[1].split(",")).mapToInt(Integer::valueOf).toArray();
            count = factorial(slaveRedArr.length)/(factorial(slaveRedArr.length+mainRedArr.length-6)*factorial(6-mainRedArr.length))*blueArr.length;
        }

        return count;
    }

    /**
     * 计算11选5注数
     * @param numStr
     * @param elevenChooseFiveType
     * @return
     */
    public static long elevenChooseFiveLoop(String numStr,String elevenChooseFiveType){
        long count=0;
        Integer selectNum = Integer.valueOf(elevenChooseFiveType.split("-")[2]);
        if(elevenChooseFiveType.startsWith("4-2")){
            int mainNumLen = numStr.split(";")[0].split(",").length;
            int slaveNumLen = numStr.split(";")[1].split(",").length;
            count = Combine(slaveNumLen,selectNum-mainNumLen);
        }else if (PlayType.ELEVENCHOOSEFIVE4121.getType().equals(elevenChooseFiveType)){
            String[] mainStr = numStr.split(";")[0].split(",");
            String[] slaveStr = numStr.split(";")[1].split(",");
            count = mainStr.length * slaveStr.length - numJewelsInStones(mainStr,slaveStr);
        }else if (PlayType.ELEVENCHOOSEFIVE4131.getType().equals(elevenChooseFiveType)){
            //待定算法,s1,s2,s3相同算法
            //Str1,str2重复
            //str1.len*str2.len*str3.len-same(str1,str2)*str3.len
            //str1.str3重复
            //0<str1.len<12,0<str2.len<12,0<str3.len str1.len*str2.len*str3.len-same(str1,str3)*str2.len
            //Str2,str3重复
            //str1.len*str2.len*str3.len-same(str2,str3)*str1.len
            String[] mainStr = numStr.split(";")[0].split(",");
            String[] slaveStr = numStr.split(";")[1].split(",");
            String[] endStr = numStr.split(";")[2].split(",");
            count = mainStr.length * slaveStr.length * endStr.length;
        }else {
            String[] mainStr = numStr.split(",");
            count = Combine(mainStr.length,selectNum);
        }
        return count;
    }

    /**
     * 计算大乐透的注数
     * @param redStr
     * @param blueStr
     * @param superLottoType
     * @return
     */
    public static long superLottoLoop(String redStr,String blueStr,String superLottoType){
        long count=0;
        if(PlayType.SUPERLOTTOTHREE.getType().equals(superLottoType)){
            //胆拖
            int mainRedLen = redStr.split(";")[0].split(",").length;
            int slaveRedLen = redStr.split(";")[1].split(",").length;
            int mainBlueLen;
            int slaveBlueLen;
            long blueSize;
            if (blueStr.contains(";")){
                //蓝球选胆，个数最多为1
                mainBlueLen = blueStr.split(";")[0].split(",").length;
                slaveBlueLen = blueStr.split(";")[1].split(",").length;
                blueSize = Combine(slaveBlueLen,2-mainBlueLen);
            }else{
                //蓝球没选胆
                String[] mainStr = blueStr.split(",");
                blueSize = Combine(mainStr.length,2);
            }
            count = Combine(slaveRedLen,5-mainRedLen) * blueSize;
        }else{
            //单式，复式
            String[] rStr = redStr.split(",");
            String[] bStr = blueStr.split(",");
            count = Combine(rStr.length,5) * Combine(bStr.length,2);
        }

        return count;
    }

    public static long rankThreeLoop(String numStr,String rankThreeType){
        long count=0;
        if (PlayType.RANK61.getType().equals(rankThreeType)){
            int bailen = numStr.split(";")[0].split(",").length;
            int shilen = numStr.split(";")[1].split(",").length;
            int gelen = numStr.split(";")[2].split(",").length;
            count = bailen * shilen * gelen;
        }else if (PlayType.RANK62.getType().equals(rankThreeType)){
            int lenNum = numStr.split(",").length;
            count = lenNum * (lenNum - 1);
        }else if (PlayType.RANK63.getType().equals(rankThreeType)){
            int lenNum = numStr.split(",").length;
            count = Combine(lenNum,3);
        }
        return count;
    }

    public static long rankFiveLoop(String numStr,String rankFiveType){
        long count=0;
        if (PlayType.RANK71.getType().equals(rankFiveType)){
            //单式
            count = 1;
        }else if (PlayType.RANK72.getType().equals(rankFiveType)){
            int wanlen = numStr.split(";")[0].split(",").length;
            int qianlen = numStr.split(";")[1].split(",").length;
            int bailen = numStr.split(";")[2].split(",").length;
            int shilen = numStr.split(";")[3].split(",").length;
            int gelen = numStr.split(";")[4].split(",").length;
            count = wanlen * qianlen * bailen * shilen * gelen;
        }
        return count;
    }

    public static long factorial(int x){
        long y = 1;
        for (int i = 1; i <= x; i++)
            y = y*i;
        return y;
    }

    public static long Combine(int x, int y){
        long m = x;
        if(x > y){
            for (int i = 1; i < y; i++) {
                m = m * (x-i);
            }
            return m/factorial(y);
        }else{
            return 1;
        }
    }

    /**
     * 判断一个字符串数组是否包含相同字符串
     * @param strings
     * @return
     */
    public static boolean judgeStrArrayContainsSameStr(String[] strings) {
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            String temp = strings[i];
            for (int j = i + 1; j < len; j++) {
                String string = strings[j];
                if (string.equals(temp)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 计算2个字符数组中相同元素的个数
     * @param ja
     * @param sa
     * @return
     */
    public static int numJewelsInStones(String[] ja, String[] sa) {
        int r = 0;
        for (int i = 0;i < ja.length ; i ++){
            for(int j = 0; j < sa.length; j++){
                if(ja[i] == sa[j])
                    r ++;
            }
        }
        return r;
    }

    public static void main(String[] args) {
        System.out.println(Combine(25,5));
    }

}
