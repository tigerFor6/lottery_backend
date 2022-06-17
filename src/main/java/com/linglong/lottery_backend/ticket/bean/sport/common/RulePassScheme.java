package com.linglong.lottery_backend.ticket.bean.sport.common;


import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.bean.sport.football.JczqBqcPGame;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

public class RulePassScheme {
    private String regularExp;
    private final String DELIMITER;
    private final String DELIMITER_SPLITE;

    private final int[][][] betwayTable;

    public RulePassScheme(int[][][] table, String delimiter, String regex) {
        this.betwayTable = table;
        this.DELIMITER = delimiter;
        this.DELIMITER_SPLITE = regex;
        this.regularExp = getRegexp(DELIMITER);
    }

    private boolean isAvailableIndexM(int m) {
        int indexM = m - 1;
        return betwayTable[indexM][0] != null && betwayTable[indexM][0][0] > 0;
    }

    protected int[] getAllM() {
        int length = 0;
        for (int i = 1; i <= betwayTable.length; i++) {
            if (isAvailableIndexM(i)) {
                length++;
            }
        }
        int[] res = new int[length];
        int index = 0;
        for (int i = 1; i <= betwayTable.length; i++) {
            if (isAvailableIndexM(i)) {
                res[index] = i;
                index++;
            }
        }
        return res;
    }

    public int[] getAllN(int m) {
        if (isAvailableIndexM(m)) {
            return betwayTable[m - 1][0];
        }
        throw new BusinessException("不包含" + m + "关的过关方式");
    }

    public int[] compose(int m, int n) {
        int[] res;
        try {
            int indexM = m - 1;
            if (!isAvailableIndexM(m)) {
                throw new BusinessException("不包含" + m + "关的过关方式");
            }
            int index = ArrayUtils.indexOf(betwayTable[indexM][0], n) + 1;
            if (index == 0) {
                throw new BusinessException("不包含" + m + DELIMITER + n + "的过关方式");
            }
            res = betwayTable[indexM][index];
            if (res == null) {
                throw new BusinessException("不包含" + m + "串" + n + "的过关方式");
            }
        } catch (IndexOutOfBoundsException e) {
            throw new BusinessException("查询索引错误(" + m + ", " + n + ")", e);
        }
        return res;
    }

    public String getRegexp(String delimiter) {
        StringBuffer buffer = new StringBuffer();
        String connector = delimiter.equals("") ? "" : CommonConstant.COMMON_ESCAPE_STR + delimiter;
        int mLast, nLast;
        int[] ms = getAllM();
        mLast = ms.length;

        buffer.delete(0, buffer.length());
        for (int m : ms) {
            buffer.insert(0, "(").append(m).append(connector).append("(");

            int[] ns = getAllN(m);
            nLast = ns.length;
            for (int n : ns) {
                buffer.append(n);
                nLast--;
                if (nLast > 0) {
                    buffer.append("|");
                }
            }
            mLast--;
            buffer.append("))");
            if (mLast > 0) {
                buffer.append("|");
            }
        }
        regularExp = buffer.toString();
        return regularExp;
    }

    public static void main(String[] args) {
        RulePassScheme a = new RulePassScheme((new JczqBqcPGame()).getRuleTable(), "_", ",");
        System.out.println(a.getRegexp("_"));
        System.out.println(a.formatForSingle("1"));
        System.out.println(a.formatForSingle("1,2_1,3_1"));
    }

    public boolean validate(String userInput) {
        String[] entries = userInput.split(CommonConstant.COMMON_ESCAPE_STR + DELIMITER_SPLITE);
        for (String entry : entries) {
            if (!entry.matches(regularExp)) {
                throw new IllegalArgumentException("过关方式信息" + entry + "不合法 ");
            }
        }
        return true;
    }

    public String formatForSingle(String userInput) {
        String[] entries = userInput.split(CommonConstant.COMMON_ESCAPE_STR + DELIMITER_SPLITE);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String entry : entries) {
            if (count != 0) {
                sb.append(DELIMITER_SPLITE);
            }
            sb.append(entry);
            count++;
        }
        return sb.toString();
    }

    public SetPass parser(String userInput) {
        try {
            //兼容extra的单关格式，在单场胜平负中可能为：1，在其他彩种中为：1_1
            userInput = formatForSingle(userInput);
            Collection<EntryPass> set;
            if (StringUtils.isBlank(userInput)) {
                throw new IllegalArgumentException("过关方式信息不能为空");
            }
            validate(userInput);

            String[] entries = userInput.split(CommonConstant.COMMON_ESCAPE_STR + DELIMITER_SPLITE);
            //对m串n方式按照m进行排序，为了更快的淘汰出胆大于m的情况
            set = new ConcurrentSkipListSet<>();
            for (String entry : entries) {
                String[] str = entry.split(String.valueOf(CommonConstant.COMMON_ESCAPE_STR + DELIMITER));
                set.add(new EntryPass(Integer.valueOf(str[0]), Integer.valueOf(str[1])));
            }
            return new SetPass(set);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("过关方式格式错误：" + e.getMessage());
        }
    }

    private String getAllMString() {
        String str = "M : ";
        try {
            for (int m : getAllM())
                str += (m + " ");
            str += "\n";
        } catch (NullPointerException e) {
            str = "";
        }
        return str;
    }

    private String getAllNString(int m) {
        String str = m + "串 :";
        try {
            for (int n : getAllN(m))
                str += (" " + n);
            str += "\n";
        } catch (NullPointerException e) {
            str = "";
        }
        return str;
    }

    private String getComposeString(int m, int n) {
        String str = "";
        for (int v : compose(m, n)) {
            str += v + " ";
        }
        return str;
    }
}
