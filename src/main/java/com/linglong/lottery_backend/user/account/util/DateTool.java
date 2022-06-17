package com.linglong.lottery_backend.user.account.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class DateTool {
    /**
     * @description: 返回年月的拼接，用于数据库分表的表名后缀。如 201903
     * @author: ZhiYao.Zhang
     * @param: 传入一个num，表示当前时间月份减num 如：-1
     * @return:
     * @create: 2019-04-18
     **/

    public String splcingYearAndMonth(Integer num) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        c.setTime(new Date());
        c.add(Calendar.MONTH, num);
        Date time = c.getTime();
        return format.format(time);
    }

    /**
     * @description: 返回一个时间段内的所有时间
     * @author: ZhiYao.Zhang
     * @param: 传入一个时间段
     * @return:
     * @create: 2019-04-29
     **/

    public List<String> findDates(Date dBegin, Date dEnd){
        List<String> lDate = new ArrayList<String>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
        lDate.add(sd.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime()))
        {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.MONTH, 1);
            lDate.add(sd.format(calBegin.getTime()));
        }
        return lDate;
    }

}
