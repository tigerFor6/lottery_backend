package com.linglong.lottery_backend.ticket.util;

import com.linglong.lottery_backend.ticket.entity.BettingTicket;
import com.linglong.lottery_backend.ticket.entity.TicketStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class OrderTicketStatusUtil {

    private static Integer judgeTicketStatus(Set<Integer> array) {
        List<Integer> l = new ArrayList<>(array);
        boolean mfail = l.contains(TicketStatus.mfail.getValue());
        boolean wait = l.contains(TicketStatus.wait.getValue());
        boolean fail = l.contains(TicketStatus.fail.getValue());
        boolean success = l.contains(TicketStatus.success.getValue());
        boolean processing = l.contains(TicketStatus.processing.getValue());
        boolean unknown = l.contains(TicketStatus.unknown.getValue());
        if (array.size() == 1) {
            if (mfail) {
                return TicketStatus.mfail.getValue();
            }else if (fail) {
                return TicketStatus.fail.getValue();
            } else if (success){
                return TicketStatus.success.getValue();
            } else if (wait || unknown){
                return TicketStatus.wait.getValue();
            } else if (processing){
                return TicketStatus.processing.getValue();
            }
        } else {
            if (processing || wait || unknown){
                return TicketStatus.processing.getValue();
            }
            if (success) {
                return TicketStatus.successPart.getValue();
            }
            if (mfail || fail){
                return TicketStatus.fail.getValue();
            }
        }
        return TicketStatus.processing.getValue();
    }

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

    public static Integer judgeOrderTicketStatus(List<BettingTicket> bettingTickets) {
        Set<Integer> setStatus = new HashSet<>();
        bettingTickets.forEach(f -> setStatus.add(f.getTicketStatus()));
        //Integer[] array = setStatus.toArray(new Integer[setStatus.size()]);
        return judgeTicketStatus(setStatus);
    }
}
