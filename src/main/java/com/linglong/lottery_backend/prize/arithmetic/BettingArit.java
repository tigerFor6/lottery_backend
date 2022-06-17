//package com.linglong.lottery_backend.prize.arithmetic;
//
//import com.linglong.lottery_backend.prize.entity.custom.BettingMatch;
//import com.linglong.lottery_backend.prize.entity.custom.BettingOdds;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * 投注方式组合方式算法
// * 基于排列+笛卡尔乘积
// * huazengguang
// */
//public class BettingArit {
//    /**
//     * 排列算法
//     *
//     */
//    public static List<List<BettingMatch>> arrange(List<BettingMatch> elements, int num ){
//        List<List<BettingMatch>> result = new ArrayList<>();
//        elements.forEach(data->{
//            List<BettingMatch> list = new ArrayList<>();
//            list.add(data);
//            result.add(list);
//        });
//        return combiner(elements, num, result);
//    }
//
//    public static List<List<BettingMatch>> combiner(List<BettingMatch> elements, int num, List<List<BettingMatch>> result) {
//        if(num == 1){
//            return result;
//        }
//        int len = result.size();
//        for (int i = 0; i < len; i++) {
//            for (int j = 0; j < elements.size(); j++) {
//                if(!result.get(i).contains(elements.get(j))){
//                    List<BettingMatch> list = new ArrayList<>();
//                    for (int k = 0; k < result.get(i).size(); k++) {
//                        list.add(result.get(i).get(k));
//                    }
//                    list.add(elements.get(j));
//                    Collections.sort(list);
//                    result.add(list);
//                }
//            }
//        }
//        for (int i = 0; i < len; i++) {
//            result.remove(0);
//        }
//        Iterator<List<BettingMatch>> it=result.iterator();
//        List<List<BettingMatch>> listTemp= new ArrayList<>();
//        while(it.hasNext()){
//            List<BettingMatch> a=it.next();
//            if (listTemp.contains(a)){
//                it.remove();
//            }else {
//                listTemp.add(a);
//            }
//        }
//        combiner(elements, num - 1, result);
//        return result;
//    }
//
//    public static void recursive (List<List<BettingOdds>> dimValue, List<List<BettingOdds>> result, int layer, List<BettingOdds> curList) {
//        if (layer < dimValue.size() - 1) {
//            if (dimValue.get(layer).size() == 0) {
//                recursive(dimValue, result, layer + 1, curList);
//            } else {
//                for (int i = 0; i < dimValue.get(layer).size(); i++) {
//                    List<BettingOdds> list = new ArrayList<>(curList);
//                    list.add(dimValue.get(layer).get(i));
//                    recursive(dimValue, result, layer + 1, list);
//                }
//            }
//        } else if (layer == dimValue.size() - 1) {
//            if (dimValue.get(layer).size() == 0) {
//                result.add(curList);
//            } else {
//                for (int i = 0; i < dimValue.get(layer).size(); i++) {
//                    List<BettingOdds> list = new ArrayList<>(curList);
//                    list.add(dimValue.get(layer).get(i));
//                    result.add(list);
//                }
//            }
//        }
//    }
//}
