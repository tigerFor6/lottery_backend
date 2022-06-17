package com.linglong.lottery_backend.activity.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    /**
     * 添加秒数
     * @param date
     * @param addSeconds
     * @return
     */
    public static Date transToDate(Date date,int addSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }


    /**
     * 判断日期  是否在另两个时间段内
     * @param time
     * @param from
     * @param to
     * @return
     */
    public static boolean belongCalendar(Date time, Date from, Date to) {
        Calendar date = Calendar.getInstance();
        date.setTime(time);

        Calendar after = Calendar.getInstance();
        after.setTime(from);

        Calendar before = Calendar.getInstance();
        before.setTime(to);

        if (date.after(after) && date.before(before)) {
            return true;
        } else {
            return false;
        }
    }
}
