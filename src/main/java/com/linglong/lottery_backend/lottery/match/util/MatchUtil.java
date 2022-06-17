package com.linglong.lottery_backend.lottery.match.util;

import com.linglong.lottery_backend.lottery.match.ctrl.entity.MatchType;
import com.linglong.lottery_backend.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/5
 */
@Slf4j
public class MatchUtil {

    public static Date getMatchEndTime(Integer matchType,Date matchTime){
        Calendar car = Calendar.getInstance();
        String openTimeOne = DateUtil.formatDate(matchTime,1) + " 09:20:00";
        String openTimeTwo = DateUtil.formatDate(matchTime,1) + " 07:30:00";
        String endTimeOne = DateUtil.formatDate(matchTime,1) + " 24:00:00";
        Date specialTimeOne = DateUtil.formatToDate(DateUtil.formatDate(matchTime,1) + " 00:00:00",DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS);
        Date specialTimeTwo = DateUtil.formatToDate(DateUtil.formatDate(matchTime,1) + " 01:00:00",DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS);
        String yesterday = DateUtil.formatTime(DateUtil.getIntervalDays(new Timestamp(matchTime.getTime()), -1), "yyyy-MM-dd");
        Date endTime = new Date();
        //1足球、2篮球
        if(matchType.equals(MatchType.jczq)) {
            if (DateUtil.isBetween(matchTime, DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
                endTime = DateUtils.addMinutes(matchTime, -20);
            } else {
                endTime = DateUtils.addMinutes(DateUtil.formatToDate(yesterday + " 24:00:00", DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -20);
            }
        }else if(matchType.equals(MatchType.jclq)) {
            car.setTime(matchTime);
            int ws = car.get(Calendar.DAY_OF_WEEK) - 1;
            if(ws == 3 || ws == 4){
                if (DateUtil.isBetween(matchTime,DateUtil.formatToDate(openTimeTwo,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS),DateUtil.formatToDate(endTimeOne,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))){
                    endTime =  DateUtils.addMinutes(matchTime, -20);
                }else{
                    endTime =  DateUtils.addMinutes(DateUtil.formatToDate(yesterday + " 24:00:00",DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -20);
                }
            }else{
                if (DateUtil.isBetween(matchTime,DateUtil.formatToDate(openTimeOne,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS),DateUtil.formatToDate(endTimeOne,DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))){
                    endTime =  DateUtils.addMinutes(matchTime, -20);
                }else{
                    endTime =  DateUtils.addMinutes(DateUtil.formatToDate(yesterday + " 24:00:00",DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -20);
                }
            }
        }
        car.setTime(endTime);
        int w = car.get(Calendar.DAY_OF_WEEK) - 1;
        if (w == 6 || w == 0) {
//          加一个小时
            endTime = DateUtils.addHours(endTime, 1);
            if (endTime.after(matchTime) || endTime.getTime() == matchTime.getTime()) {
                endTime = DateUtils.addMinutes(matchTime, -20);
            }
            if (DateUtil.isBetween(matchTime,specialTimeOne,specialTimeTwo)){
                endTime = DateUtils.addMinutes(matchTime, -20);
            }
        }

        return endTime;
}
    public static void main(String[] args) {
        log.info("结果是：：："+DateUtil.formatDate(getMatchEndTime(1,DateUtil.formatToDate("2019-06-04 00:40:00",DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS)),2));
    }
}
