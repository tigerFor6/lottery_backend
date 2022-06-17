package com.linglong.lottery_backend.ticket.bean.sport.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public class SetPass {

    //给系统配的m串n列表，每个游戏配置均不同，这里只会根据游戏变化而变化，所以使用了读效率较高的CopyOnWriteArraySet
    //除去重复
    protected final CopyOnWriteArraySet<EntryPass> set;

    protected SetPass(Collection<EntryPass> set) {
        this.set = new CopyOnWriteArraySet<EntryPass>();
        this.set.addAll(set);
    }

    protected Iterator<EntryPass> iterator() {
        return set.iterator();
    }

    protected int size() {
        return set.size();
    }

    protected boolean isEmpty() {
        return set.isEmpty();
    }

    protected int[] getAllM() {
        int i = 0;
        int[] m = new int[set.size()];
        for (EntryPass ent : set) {
            m[i] = ent.getM();
            i++;
        }
        return m;
    }

    public String toString() {
        if (set == null || set.isEmpty())
            return null;
        String res = "";
        Iterator<EntryPass> it = iterator();
        while (it.hasNext()) {
            res += it.next() + " ";
        }
        return res;
    }
}
