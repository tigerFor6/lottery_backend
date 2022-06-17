package com.linglong.lottery_backend.ticket.bean.sport.common;

import java.util.ArrayList;
import java.util.List;

public class Combination {

    protected int c;
    protected int n;
    protected int remain;
    protected CombinationGenerator<EntryBet> generator;

    public Combination(int c, int n, int r) {
        this.c = c;
        this.n = n;
        this.remain = r;
    }

    public String toString() {
        return "C(" + c + ", " + n + ") * ?^" + n + " - (" + remain + ")";
    }

    public List<List<EntryBet>> combination(List<EntryBet> from) {
        List<List<EntryBet>> result = new ArrayList<List<EntryBet>>();
        if (from.size() == n) {
            result.add(from);
            return result;
        }
        if (from.size() == 0) {
            return null;
        }

        generator = new CombinationGenerator<EntryBet>(from, n);
        for (List<EntryBet> list : generator) {
            result.add(list);
        }
        return result;
    }
}
