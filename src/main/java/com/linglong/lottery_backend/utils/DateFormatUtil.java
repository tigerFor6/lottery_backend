package com.linglong.lottery_backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: qihua.li
 * @since: 2019-04-10
 */
@Component
public class DateFormatUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateFormatUtil.class);

    public String correcToMinute(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY年MM月dd日 HH:mm");
        return sdf.format(date);
    }

    public Boolean compareTime(String deadline, Date time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");
        Date deadTime = sdf.parse(deadline);
//        logger.info("deadline:{},now:{}", deadTime, new Date());
        return new Date().after(deadTime);
    }
}
