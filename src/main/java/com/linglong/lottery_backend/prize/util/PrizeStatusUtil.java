package com.linglong.lottery_backend.prize.util;

import com.linglong.lottery_backend.prize.entity.OpenPrizeStatus;
import com.linglong.lottery_backend.prize.entity.PrizeStatus;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketDetails;
import com.linglong.lottery_backend.prize.entity.TblBettingTicketGroup;
import com.linglong.lottery_backend.ticket.entity.BettingTicket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrizeStatusUtil {

//    private static Integer judgePrizeStatus(Set<Integer> array) {
//        if (array.size()> 1){
//            return array.contains(PrizeStatus.losing.getValue())?PrizeStatus.losing.getValue():PrizeStatus.wait.getValue();
//        }
//        if (array.contains(PrizeStatus.losing.getValue())){
//            return PrizeStatus.losing.getValue();
//        }
//        if (array.contains(PrizeStatus.awarded.getValue())){
//            return PrizeStatus.awarded.getValue();
//        }
//        if (array.contains(PrizeStatus.wait.getValue())){
//            return PrizeStatus.wait.getValue();
//        }
//        return PrizeStatus.none.getValue();
//    }

//    public static Integer judgePrizeStatus(TblBettingGroup group) {
//        Set<Integer> setStatus = new HashSet<>();
//        group.getBettingGroupDetails().forEach(f -> setStatus.add(f.getResult()));
//        Integer[] array = setStatus.toArray(new Integer[setStatus.size()]);
//        return judgePrizeStatus(array);
//    }

//    public static Integer judgePrizeStatus(List<TblBettingGroupDetails> groupDetails) {
//        Set<Integer> setStatus = new HashSet<>();
//        groupDetails.forEach(f -> setStatus.add(f.getResult()));
//        Integer[] array = setStatus.toArray(new Integer[setStatus.size()]);
//        return judgePrizeStatus(array);
//    }

    /**
     *
     * @param group
     * @return
     */
    public static Integer judgeTicketGroupStatus(List<TblBettingTicketDetails> group) {
        Set<Integer> array = new HashSet<>();
        group.forEach(f -> array.add(f.getResult()));
        if (array.size() > 1) {
            return array.contains(PrizeStatus.losing.getValue()) ? PrizeStatus.losing.getValue() : PrizeStatus.wait.getValue();
        }
        return judgeStatus(array);
    }

    private static Integer judgeStatus(Set<Integer> array) {
        //判断有没有中大奖
        if (array.contains(PrizeStatus.bigAwarded.getValue())) {
            return PrizeStatus.bigAwarded.getValue();
        }
        //判断有没有中奖
        if (array.contains(PrizeStatus.awarded.getValue())) {
            return PrizeStatus.awarded.getValue();
        }
        //判断是否未中奖
        if (array.contains(PrizeStatus.losing.getValue())) {
            return PrizeStatus.losing.getValue();
        }
        //判断是否有未开奖
        if (array.contains(PrizeStatus.wait.getValue())) {
            return PrizeStatus.wait.getValue();
        }
        return PrizeStatus.none.getValue();
    }

//    private static Integer judgeStatus(Set<Integer> array) {
//        //判断有没有大奖
//        if (array.contains(PrizeStatus.bigAwarded.getValue())) {
//            return PrizeStatus.bigAwarded.getValue();
//        }
//        //判断有没有中奖
//        if (array.contains(PrizeStatus.awarded.getValue())) {
//            return PrizeStatus.awarded.getValue();
//        }
//        //判断是否未中奖
//        if (array.contains(PrizeStatus.losing.getValue())) {
//            return PrizeStatus.losing.getValue();
//        }
//        //判断是否有未开奖
//        if (array.contains(PrizeStatus.wait.getValue())) {
//            return PrizeStatus.wait.getValue();
//        }
//        return PrizeStatus.none.getValue();
//    }

//    public static Integer judgeTicketStatus(List<TblBettingTicketGroup> ticketGroups, Set<Integer> array) {
//        return judgeStatus(array);
//    }

//    public static Integer judgeOrderStatus(List<BettingTicket> tickets, Set<Integer> array) {
////        if (array.size() > 1) {
////            //判断有没有中奖
////            if (array.contains(PrizeStatus.awarded.getValue())) {
////                return PrizeStatus.awarded.getValue();
////            }
////            //判断有没有大奖
////            if (array.contains(PrizeStatus.bigAwarded.getValue())) {
////                return PrizeStatus.bigAwarded.getValue();
////            }
////            //判断有没有部分开奖
////            if (array.contains(PrizeStatus.losing.getValue())) {
////                return PrizeStatus.bigAwarded.getValue();
////            }
////        }
//        return judgeStatus(array);
//    }

    public static Integer judgeOpenPrizeStatus(Set<Integer> array) {
        //判断是否已开奖
        if (array.contains(OpenPrizeStatus.opened.getValue())) {
            return OpenPrizeStatus.opened.getValue();
        }
        //判断有没有部分开奖
        if (array.contains(OpenPrizeStatus.openedPart.getValue())) {
            return  OpenPrizeStatus.openedPart.getValue();
        }
        //判断是否未中奖
        if (array.contains(OpenPrizeStatus.wait.getValue())) {
            return OpenPrizeStatus.wait.getValue();
        }
        return OpenPrizeStatus.none.getValue();
    }
//
//    public static Integer judgePrizeStatus(Set<Integer> array) {
//        return judgeStatus(array);
//    }

    public static Integer judgeTicketStatus(Set<Integer> array) {

        return judgeStatus(array);
    }

    public static Integer judgePrizeStatus(Set<Integer> prizeArray) {
        return judgeStatus(prizeArray);
    }
}
