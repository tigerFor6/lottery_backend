package com.linglong.lottery_backend.ticket.bean.sport.common;


import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ynght on 2019-04-20
 */
public abstract class AbstractRuleBet<E> {

    protected String regularExp;
    protected final String DELIMITER;
    private final int maxTimes;
    private final Set<String> values;

    public AbstractRuleBet() {
        regularExp = ".";
        this.DELIMITER = "";
        this.values = new HashSet<>();
        this.maxTimes = 1;
    }

    public AbstractRuleBet(Collection<String> values, String delimiter) {

        this.DELIMITER = delimiter;
        this.values = new HashSet<>();
        this.values.addAll(values);
        this.maxTimes = values.size();
        this.regularExp = getRegexp(DELIMITER);
    }

    public AbstractRuleBet(Collection<String> values, int times) {

        this.DELIMITER = "";
        this.values = new HashSet<>();
        this.values.addAll(values);
        this.maxTimes = times;
        this.regularExp = getRegexp(DELIMITER);
    }

    public AbstractRuleBet(Collection<String> values, int times, String delimiter) {

        this.DELIMITER = delimiter;
        this.values = new HashSet<>();
        this.values.addAll(values);
        this.maxTimes = times;
        this.regularExp = getRegexp(DELIMITER);
    }

    private String getRegexp(String delimiter) {
        if (values.isEmpty()) {
            throw new BusinessException("规则集合未被初始化，请检查 " + getClass().getSimpleName() + "中的参数");
        }
        Iterator<String> it = values.iterator();
        StringBuffer buffer = new StringBuffer();
        String connector = delimiter.equals("") ? "" : CommonConstant.COMMON_ESCAPE_STR + delimiter;
        String temp;
        boolean first = true;

        //每次调用改方法，更改正则表达式
        buffer.delete(0, buffer.length());
        while (it.hasNext()) {
            temp = it.next();
            if (first)
                buffer.append("(").append(temp).append(")");
            else
                buffer.append("|(").append(temp).append(")");
            first = false;
        }
        buffer.insert(0, "(").append(")").insert(0, "(").append(connector).append(")").append("{0,")
                .append(maxTimes - 1).append("}");
        int index = buffer.length();
        buffer.append(buffer).delete(buffer.indexOf("{", index), buffer.length());

        if (StringUtils.isNotBlank(connector)) {
            index = buffer.indexOf(connector, index);
            buffer.delete(index, index + 2);
        }
        regularExp = buffer.toString();

        return regularExp;
    }

    public boolean validate(String userInput) {
        return userInput.matches(regularExp);
    }

    public String[] getValues() {
        return values.toArray(new String[0]);
    }

    public boolean contains(String str) {
        return values.contains(str);
    }

    public boolean containsAll(Collection<String> c) {
        return values.containsAll(c);
    }

    public boolean add(String o) {
        boolean res = values.add(o);
        this.regularExp = getRegexp(DELIMITER);
        return res;
    }

    public boolean remove(String o) {
        boolean res = values.remove(o);
        this.regularExp = getRegexp(DELIMITER);
        return res;
    }

    public abstract E parser(String userInput);

    public int valueLength() {
        if (values.isEmpty())
            return 0;
        return getValues()[0].length();
    }

    public String toString() {
        String str = values.size() + " : ";
        Iterator<String> it = values.iterator();
        while (it.hasNext()) {
            str += (it.next() + " ");
        }
        return str;
    }

    public int getValueSize() {
        return values.size();
    }
}
