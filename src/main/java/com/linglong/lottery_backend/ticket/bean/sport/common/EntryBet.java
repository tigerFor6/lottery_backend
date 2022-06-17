package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.bean.AbstractSportGame;

import java.util.*;

public class EntryBet {

    private String id;
    private String[] bets;
    protected boolean dan;

    protected EntryBet(String id, String[] bets, boolean dan) {
        this.setId(id);
        this.setBets(bets);
        this.dan = dan;
    }

    protected EntryBet(EntryBet ent) {
        this.setId(ent.getId());
        this.setBets(ent.getBets());
        this.dan = ent.dan;
    }

    @Override
    public String toString() {
        String str = "";
        for (String s : getBets())
            str += "|" + s;
        str += "|";
        return getId() + " : " + str + " : " + dan;
    }

    @Override
    public boolean equals(Object e) {
        return getId().equalsIgnoreCase(((EntryBet) e).getId());
    }

    public int hashCode() {
        return id.hashCode();
    }

    public String format() {
        StringBuffer sb = new StringBuffer();
        sb.append(getId()).append(CommonConstant.COMMON_COLON_STR);
        for (int i = 0; i < getBets().length; i++) {
            sb.append(getBets()[i]);
            if (i != getBets().length - 1)
                sb.append(CommonConstant.COMMON_DOT_STR);
        }
        sb.append(CommonConstant.COMMON_COLON_STR).append(
                dan ? RuleConstant.CHAR_FORMAT_DAN : RuleConstant.CHAR_FORMAT_TUO);
        return sb.toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setBets(String[] bets) {
        this.bets = bets;
    }

    public String[] getBets() {
        return bets;
    }

    public List<EntryBet> half() {
        List<EntryBet> half = new ArrayList<EntryBet>();
        if (bets.length < 2) {
            half.add(this);
        } else {
            String[] one = Arrays.copyOf(bets, bets.length / 2);
            String[] two = Arrays.copyOfRange(bets, bets.length / 2, bets.length);
            half.add(new EntryBet(id, one, dan));
            half.add(new EntryBet(id, two, dan));
        }
        return half;
    }

    public List<EntryBet> halfForMix(AbstractSportGame gi) {
        List<EntryBet> half = new ArrayList<EntryBet>();
        if (bets.length < 2) {
            half.add(this);
        } else {
            Map<String, List<Object>> map = new HashMap<String, List<Object>>();
            for (String bet : bets) {
                String play = gi.getGameEnByBetOption(bet);
                if (null != map.get(play)) {
                    List<Object> bs = map.get(play);
                    bs.add(bet);
                    map.put(play, bs);
                } else {
                    List<Object> bs = new ArrayList<Object>();
                    bs.add(bet);
                    map.put(play, bs);
                }
            }

            for (String key : map.keySet()) {
                String[] betArray = new String[map.get(key).size()];
                map.get(key).toArray(betArray);
                half.add(new EntryBet(id, betArray, dan));
            }
        }
        return half;
    }

    public boolean isUnit() {
        return bets.length <= 1;
    }
}
