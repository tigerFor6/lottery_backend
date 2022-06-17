package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.bean.AbstractSportGame;

import java.util.*;

/**
 * 经过拆分处理的竞彩投注类
 */
public class BetVo {

    private Collection<EntryBet> bets; //投注号集合
    private EntryPass passWay; //过关方式，只有一种
    private int betTimes; //注数
    private boolean betWay; //单式复试

    protected static Comparator<EntryBet> comparator = new Comparator<EntryBet>() {
        public int compare(EntryBet o1, EntryBet o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    public BetVo() {

    }

    public BetVo(Collection<EntryBet> bets, EntryPass passWay) {
        this.setBets(bets);
        this.setPassWay(passWay);
        boolean sin = true;
        int betNumber = 1;
        for (EntryBet b : bets) {
            if (b.getBets().length > 1) {
                sin = false;
            }
            if (b.getBets().length > 0) {
                betNumber *= b.getBets().length;
            }
        }
        this.setBetWay(sin);
        this.setBetTimes(betNumber);
    }

    public void setBets(Collection<EntryBet> bets) {
        this.bets = bets;
    }

    public void setPassWay(EntryPass passWay) {
        this.passWay = passWay;
    }

    public void setBetTimes(int betTimes) {
        this.betTimes = betTimes;
    }

    public void setBetWay(boolean betWay) {
        this.betWay = betWay;
    }

    protected List<EntryBet> sort() {
        List<EntryBet> list = new ArrayList<EntryBet>();
        list.addAll(getBets());
        Collections.sort(list, comparator);
        return list;
    }

    //已排序
    public String formatLotteryNumber() {
        StringBuffer sb = new StringBuffer();
        List<EntryBet> list = sort();
        int length = list.size();
        for (int n = 0; n < length; n++) {
            EntryBet element = list.get(n);
            sb.append(element.getId()).append(CommonConstant.COMMON_COLON_STR);

            for (int i = 0; i < element.getBets().length; i++) {
                sb.append(element.getBets()[i]);
                if (i != element.getBets().length - 1) {
                    sb.append(CommonConstant.POUND_SPLIT_STR);
                }
            }

            if (n != (length - 1)) {
                sb.append(CommonConstant.SPACE_SPLIT_STR);
            }
        }
        return sb.toString();
    }

    public String formatPassWay() {
        return getPassWay().format();
    }

    /**
     * mwlv modified the parameters of this function ,added "gi" to differentiate football and basketball
     *
     * @param isMix
     * @param gi
     * @return
     */
    public List<BetVo> split(boolean isMix, AbstractSportGame gi) {
        List<BetVo> result = new ArrayList<>();
        int mulNumber = 0;
        List<List<EntryBet>> target = new ArrayList<>();
        for (EntryBet entry : bets) {
            List<EntryBet> t;
            if (isMix) {
                t = entry.halfForMix(gi);
            } else {
                t = entry.half();
            }
            target.add(t);
            if (!entry.isUnit()) {
                mulNumber++;
            }
        }
        if (mulNumber <= 0) {
            return result;
        }
        List<List<EntryBet>> col = new ArrayList<List<EntryBet>>();
        for (List<EntryBet> element : target) {
            if (col.isEmpty()) {
                for (EntryBet e : element) {
                    ArrayList<EntryBet> l = new ArrayList<EntryBet>();
                    l.add(e);
                    col.add(l);
                }
            } else {
                int i = col.size();
                while (i > 0) {
                    List<EntryBet> combination = col.remove(0);
                    for (EntryBet e : element) {
                        ArrayList<EntryBet> l = new ArrayList<EntryBet>();
                        l.addAll(combination);
                        l.add(e);
                        col.add(l);
                    }
                    i--;
                }
            }
        }
        for (List<EntryBet> temp : col) {
            if (temp.size() != bets.size()) {
                throw new BusinessException("二次拆分有错误");
            }
            result.add(new BetVo(temp, passWay));
        }
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        List<EntryBet> list = sort();
        int len = list.size();
        int index = 0;
        for (EntryBet ent : list) {
            sb.append(ent.format());
            if (index != len - 1) {
                sb.append(CommonConstant.SPACE_SPLIT_STR);
            }
            index++;
        }
        sb.append(" 过关方式 ").append(getPassWay().format()).append(" 注数 ").append(getBetTimes());
        return sb.toString();
    }

    public EntryPass getPassWay() {
        return passWay;
    }

    public Collection<EntryBet> getBets() {
        return bets;
    }

    public int getBetTimes() {
        return betTimes;
    }

    public boolean isBetWay() {
        return betWay;
    }

    public boolean isUnit() {
        for (EntryBet entry : bets) {
            if (!entry.isUnit()) {
                return false;
            }
        }
        return true;
    }
}
