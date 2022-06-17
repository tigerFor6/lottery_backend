package com.linglong.lottery_backend.ticket.bean.sport.common;


import com.linglong.lottery_backend.ticket.constant.CommonConstant;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;

public class SetBet {

    private final CopyOnWriteArraySet<EntryBet> set;//除去重复

    protected Map<Integer, List<EntryBet>> counterMap;//有序Map, 如.(复选2注，胜和输) 对应  (共有5场比赛复选了2注)
    protected Map<Integer, List<EntryBet>> counterDanMap;//有序Map, 如.(复选2注，胜和输) 对应  (所有复选了2注的比赛中有3场有胆)

    private int[][] betMatrix;
    private int[][] betDanMatrix;
    private boolean multiple;//单式or复试，有一场比赛选了2种(包含)以上的结果时，为复试

    protected SetBet(Collection<EntryBet> set) {
        this.set = new CopyOnWriteArraySet<EntryBet>();
        this.getSet().addAll(set);
        init();
    }

    private void init() {

        counterMap = new TreeMap<Integer, List<EntryBet>>();
        counterDanMap = new TreeMap<Integer, List<EntryBet>>();

        Iterator<EntryBet> it = getSet().iterator();
        while (it.hasNext()) {

            EntryBet entry = it.next();
            if (entry.getBets().length > 1)
                setMultiple(true);
            int numberChoice = entry.getBets().length;
            List<EntryBet> list = counterMap.get(numberChoice);
            List<EntryBet> danList = counterDanMap.get(numberChoice);

            if (list == null || list.isEmpty()) {
                list = new ArrayList<EntryBet>();
                list.add(entry);
                counterMap.put(numberChoice, list);
            } else {
                list.add(entry);
            }

            if (entry.dan) {
                if (danList == null || danList.isEmpty()) {
                    danList = new ArrayList<EntryBet>();
                    danList.add(entry);
                    counterDanMap.put(numberChoice, danList);
                } else {
                    danList.add(entry);
                }
            }
        }
    }

    private int calculateDan(List<EntryBet> list) {
        int res = 0;
        for (EntryBet e : list)
            if (e.dan)
                res++;
        return res;
    }

    public int[][] getBetMatrix() {
        if (betMatrix != null)
            return betMatrix;

        betMatrix = new int[5][counterMap.size()];
        int length = counterMap.size();
        int i = 0;
        int[] total = new int[length];
        int[] danTotal = new int[length];
        for (Entry<Integer, List<EntryBet>> set : counterMap.entrySet()) {
            betMatrix[0][i] = set.getKey();
            betMatrix[1][i] = set.getValue().size();
            betMatrix[2][i] = calculateDan(set.getValue());

            total[i] = (i == 0 ? 0 : total[i - 1]) + betMatrix[1][i];
            danTotal[i] = (i == 0 ? 0 : danTotal[i - 1]) + betMatrix[2][i];

            i++;
        }
        for (i = 0; i < length; i++) {
            total[i] = total[length - 1] - total[i];
            danTotal[i] = danTotal[length - 1] - danTotal[i];
        }
        betMatrix[3] = total;
        betMatrix[4] = danTotal;
        return betMatrix;
    }

    //生成和getBetDanMatrix()一致的SetBet
    public SetBet getDanSetBet() {
        List<EntryBet> danSet = new ArrayList<EntryBet>();
        for (EntryBet ent : getSet()) {
            if (ent.dan) {
                EntryBet same = new EntryBet(ent);
                same.dan = false;
                danSet.add(same);
            }
        }
        SetBet setBet = new SetBet(danSet);
        return setBet;
    }

    public int[][] getBetDanMatrix() {
        if (betDanMatrix != null)
            return betDanMatrix;

        int[][] matrix = getBetMatrix();
        int len = getDanMatrixLength(matrix);
        betDanMatrix = new int[5][len];

        int index = 0;
        for (int i = 0; i < matrix[0].length; i++) {
            if (betMatrix[2][i] > 0) {
                betDanMatrix[0][index] = matrix[0][i];
                betDanMatrix[1][index] = matrix[2][i];
                betDanMatrix[2][index] = 0;
                betDanMatrix[3][index] = matrix[4][i];
                betDanMatrix[4][index] = 0;
                index++;
            }
        }
        return betDanMatrix;
    }

    private int getDanMatrixLength(int[][] matrix) {
        int length = 0;
        for (int i = 0; i < betMatrix[0].length; i++) {
            if (betMatrix[2][i] > 0)
                length++;
        }
        return length;
    }

    public int getTotalSeceneDan() {
        int[][] matrix = getBetMatrix();
        return matrix[2][0] + matrix[4][0];
    }

    public int getTotalSecene() {
        int[][] matrix = getBetMatrix();
        return matrix[1][0] + matrix[3][0];
    }

    protected String getMatrixString(int[][] matrix) {
        String str = "";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                str += matrix[i][j] + " ";
            }
            str += "\n";
        }
        return str;
    }

    protected Iterator<EntryBet> iterator() {
        return getSet().iterator();
    }

    protected int size() {
        return getSet().size();
    }

    protected boolean isEmpty() {
        return getSet().isEmpty();
    }

    @Override
    public String toString() {
        String str = "";
        int[][] matrix = getBetMatrix();
        ;
        for (int i = 0; i < betMatrix[0].length; i++) {
            str += "单场比赛复选" + matrix[0][i] + "次的共有" + matrix[1][i] + "场，其中有" + matrix[2][i] + "场有胆\n";
        }
        return str;
    }

    public String format() {
        StringBuffer sb = new StringBuffer();
        int index = 0;
        int length = getSet().size();
        for (EntryBet bet : getSet()) {
            sb.append(bet.format());
            if (index != length - 1)
                sb.append(CommonConstant.SPACE_SPLIT_STR);
            index++;
        }
        return sb.toString();
    }

    public boolean isSingle() {
        return !isMultiple();
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public CopyOnWriteArraySet<EntryBet> getSet() {
        return set;
    }
}
