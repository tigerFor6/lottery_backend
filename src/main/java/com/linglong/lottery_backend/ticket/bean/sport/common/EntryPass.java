package com.linglong.lottery_backend.ticket.bean.sport.common;


import com.linglong.lottery_backend.ticket.constant.CommonConstant;

public class EntryPass implements Comparable<EntryPass> {

    private int m;
    private int n;

    public EntryPass(int m, int n) {
        this.setM(m);
        this.setN(n);
    }

    public boolean equals(Object e) {
        return (getM() == ((EntryPass) e).getM() && getN() == ((EntryPass) e).getN());
    }

    public int hashCode(Object e) {
        if (e instanceof EntryPass) {
            return (m + "_" + n).hashCode();
        } else {
            return hashCode();
        }
    }

    //正序，从小到大
    public int compareTo(EntryPass o) {
        if (getM() > o.getM()) {
            return 1;
        } else if (getM() < o.getM()) {
            return -1;
        } else if (getM() == o.getM() && getN() > o.getN()) {
            return 1;
        } else if (getM() == o.getM() && getN() < o.getN()) {
            return -1;
        } else {
            return -1;
        }
    }

    public String format() {
        return getM() + CommonConstant.COMMON_SPLIT_STR + getN();
    }

    public String toString() {
        return getM() + "串" + getN();
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getM() {
        return m;
    }

    public Object clone() {
        return new EntryPass(m, n);
    }
}
