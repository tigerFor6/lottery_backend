package com.linglong.lottery_backend.lottery.match.service.impl;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.common.bean.LockKey;
import com.linglong.lottery_backend.lottery.match.bean.TblSale;
import com.linglong.lottery_backend.lottery.match.bean.TblSaleTime;
import com.linglong.lottery_backend.lottery.match.entity.TblMatch;
import com.linglong.lottery_backend.lottery.match.entity.TblSaleDate;
import com.linglong.lottery_backend.lottery.match.repository.TblMatchRepository;
import com.linglong.lottery_backend.lottery.match.service.TblMatchService;
import com.linglong.lottery_backend.lottery.match.cache.TblSaleDateCache;
import com.linglong.lottery_backend.utils.DateUtil;
import com.linglong.redisson.configure.annotations.LockAction;
import com.linglong.redisson.configure.util.LockType;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class TblMatchServiceImpl implements TblMatchService {

    @Autowired
    private TblMatchRepository repository;

    @Autowired
    private TblSaleDateCache tblSaleDateCache;

    @Override
    public List<TblMatch> findMatchListByMatchTypeAndMatchStatus(Integer matchType, Integer matchStatus) {
        //List<TblMatch> resultList = null;
        return repository.findMatchListByMatchTypeAndMatchStatusAndMatchSnNotEmpty(matchType, matchStatus);
    }

    @Override
    public List<TblMatch> findMatchListByMatchTypeAndMatchStatusUpdateRedis(Integer matchType, Integer matchStatus) {
        //List<TblMatch> resultList = null;
        return repository.findMatchListByMatchTypeAndMatchStatusAndMatchSnNotEmpty(matchType, matchStatus);
    }

    @Override
    public List<TblMatch> findMatchHotListByMatchTypeAndMatchStatusUpdateRedis(Integer matchType, Integer matchStatus) {
        //List<TblMatch> resultList = null;
        return repository.findMatchListByMatchTypeAndMatchStatusAndMatchSnNotEmptyAAndHotStatus(matchType, matchStatus);
    }

    @Override
    public List<TblMatch> findBasketballMatchListByMatchTypeAndMatchStatus(Integer matchType, Integer matchStatus) {
        return repository.findMatchListByMatchTypeAndMatchStatus(matchType, matchStatus);
    }

    @Override
    public TblMatch findByIdUpdateRedis(Long id) {

        Optional<TblMatch> t = repository.findById(id);
        if (t.isPresent()) {
            TblMatch result = repository.findById(id).get();
            return result;
        }
        return null;
    }

    @Override
    public TblMatch findMatchById(Long matchId) {
        Optional<TblMatch> t = repository.findById(matchId);
        if (t.isPresent()) {
            TblMatch result = repository.findById(matchId).get();
            return result;
        }
        return null;
    }

    @Override
    public List<TblMatch> findMatchByIds(ArrayList<Long> ids) {
        return repository.findAllById(ids);
    }

    @Override
    @LockAction(key = LockKey.lockMatch, value = "#tblMatch.id", lockType = LockType.WRITE_LOCK, waitTime = 30000)
    public void testLock(TblMatch tblMatch) {
        try {
            System.out.println("进入lock");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Date getMinMatchTime(List<Long> ids) {
        return repository.getMinMatchTime(ids);
    }

    @Override
    public Date getMatchEndTime(Integer matchType, Date matchTime) {
        Date deadTime = new Date();
        String startTime = "";
        String endTime = "";
        String lastStartTime = "";
        String lastEndTime = "";
        //找一般的通用配置时间
        TblSaleDate saleDate = tblSaleDateCache.getTblSaleDate(matchType,0);
        //获取周一到周日的开始和截止时间
        TblSale tblsale = JSON.parseObject(saleDate.getSaleTime(), TblSale.class);
        List<TblSaleTime> saleTimes = tblsale.getTblSaleTimes();
        String startTime1 = saleTimes.get(0).getStartTime();
        String endTime1 = saleTimes.get(0).getEndTime();
        String startTime2 = saleTimes.get(1).getStartTime();
        String endTime2 = saleTimes.get(1).getEndTime();
        String startTime3 = saleTimes.get(2).getStartTime();
        String endTime3 = saleTimes.get(2).getEndTime();
        String startTime4 = saleTimes.get(3).getStartTime();
        String endTime4 = saleTimes.get(3).getEndTime();
        String startTime5 = saleTimes.get(4).getStartTime();
        String endTime5 = saleTimes.get(4).getEndTime();
        String startTime6 = saleTimes.get(5).getStartTime();
        String endTime6 = saleTimes.get(5).getEndTime();
        String startTime7 = saleTimes.get(6).getStartTime();
        String endTime7 = saleTimes.get(6).getEndTime();
        Calendar car = Calendar.getInstance();
        car.setTime(matchTime);
        int w = car.get(Calendar.DAY_OF_WEEK) - 1;
        //获取间隔时间
        Integer difTime = saleDate.getDifTime();
        if (w == 1) {
            startTime = startTime1;
            endTime = endTime1;
            lastStartTime = startTime7;
            lastEndTime = endTime7;
        } else if (w == 2) {
            startTime = startTime2;
            endTime = endTime2;
            lastStartTime = startTime1;
            lastEndTime = endTime1;
        } else if (w == 3) {
            startTime = startTime3;
            endTime = endTime3;
            lastStartTime = startTime2;
            lastEndTime = endTime2;
        } else if (w == 4) {
            startTime = startTime4;
            endTime = endTime4;
            lastStartTime = startTime3;
            lastEndTime = endTime3;
        } else if (w == 5) {
            startTime = startTime5;
            endTime = endTime5;
            lastStartTime = startTime4;
            lastEndTime = endTime4;
        } else if (w == 6) {
            startTime = startTime6;
            endTime = endTime6;
            lastStartTime = startTime5;
            lastEndTime = endTime5;
        } else if (w == 0) {
            startTime = startTime7;
            endTime = endTime7;
            lastStartTime = startTime6;
            lastEndTime = endTime6;
        }
        String openTimeOne = DateUtil.formatDate(matchTime, 1) + " " + startTime;//09:00:00
        String endTimeOne = DateUtil.formatDate(matchTime, 1) + " " + endTime;//24:00:00
        String yesterday = DateUtil.formatTime(DateUtil.getIntervalDays(new Timestamp(matchTime.getTime()), -1), "yyyy-MM-dd");
        String lastOpenTimeOne = yesterday + " " + lastStartTime;
        String lastEndTimeOne = yesterday + " " + lastEndTime;
        //上一个时间日的开始，截止时间比较，如果开始时间大于截止时间，那么截止时间就是跨天了，需要在昨天的时间上加一天
        if (DateUtil.formatToDate(lastOpenTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS).after(DateUtil.formatToDate(lastEndTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
            lastEndTimeOne = DateUtil.formatDate(matchTime, 1) + " " + lastEndTime;
        }
        //01:00:00 往后加一天，获取比赛当天的开始和截止时间，如果开始时间大于截止时间，那么截止时间跨天，需要加一天
        if (DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS).after(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
            endTimeOne = DateUtil.formatTime(DateUtil.getIntervalDays(new Timestamp(matchTime.getTime()), 1), "yyyy-MM-dd") + " " + endTime;
        }
        //在开始时间+时间差 ————截止时间  范围内，直接用比赛时间减时间差
        if (DateUtil.isRealBetween(matchTime, DateUtils.addMinutes(DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), difTime), DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
            deadTime = DateUtils.addMinutes(matchTime, -difTime);
        } else if (DateUtil.formatToDate(lastEndTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS).after(matchTime)) {
            //2019-06-10 01:00:00  >2019-06-10 00:40:00 然后不在范围内的，考虑比赛时间小于上个结束截止时间的话，就用比赛时间减时间差
            deadTime = DateUtils.addMinutes(matchTime, -difTime);
        }else if (matchTime.after(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))){
            //2019-06-19 23:00:00 >2019-06-19 22:00:00
            deadTime = DateUtils.addMinutes(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -difTime);
        }else {
            //2019-06-10 01:00:00  < 2019-06-10 01:40:00 然后不在范围内的，考虑比赛时间大于上个结束截止时间的话，就用上个截止时间减时间差
            deadTime = DateUtils.addMinutes(DateUtil.formatToDate(lastEndTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -difTime);
        }

        TblSaleDate specialSaleDate = tblSaleDateCache.getTblSaleDate(matchType,Integer.valueOf(DateUtil.formatDate(matchTime, 5)));
        if (specialSaleDate == null) {
            //获取美国时间去数据库中查找
            specialSaleDate = tblSaleDateCache.getTblSaleDate(matchType,Integer.valueOf(DateUtil.formatDate(DateUtil.getNewyorkTime(matchTime), 5)));
            if (specialSaleDate == null) {
                //一般配置逻辑，上面已得到结果
            } else {
                //特别设置时间
                TblSale specialtblsale = JSON.parseObject(specialSaleDate.getSaleTime(), TblSale.class);
                List<TblSaleTime> specialsaleTimes = specialtblsale.getTblSaleTimes();
                String specialstartTime1 = specialsaleTimes.get(0).getStartTime();
                String specialendTime1 = specialsaleTimes.get(0).getEndTime();
                difTime = specialSaleDate.getDifTime();
                startTime = specialstartTime1;// 2019-06-09 (09:00:00)
                endTime = specialendTime1;//2019-06-10 (02:45:00)
                openTimeOne = DateUtil.formatDate(DateUtil.getNewyorkTime(matchTime), 1) + " " + startTime;//09:00:00
                endTimeOne = DateUtil.formatDate(DateUtil.getNewyorkTime(matchTime), 1) + " " + endTime;//24:00:00
                //跨天时间
                if (DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS).after(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
                    endTimeOne = DateUtil.formatTime(DateUtil.getIntervalDays(new Timestamp(DateUtil.getNewyorkTime(matchTime).getTime()), 1), "yyyy-MM-dd") + " " + endTime;
                }
                //之间
                if (DateUtil.isRealBetween(matchTime, DateUtils.addMinutes(DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), difTime), DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
                    deadTime = DateUtils.addMinutes(matchTime, -difTime);
                }else if (matchTime.after(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))){
                    //大于截止时间
                    deadTime = DateUtils.addMinutes(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -difTime);
                }else{
                    //一般配置逻辑，上面已得到结果

                }
            }
        }else{
            //特别设置时间
            TblSale specialtblsale = JSON.parseObject(specialSaleDate.getSaleTime(), TblSale.class);
            List<TblSaleTime> specialsaleTimes = specialtblsale.getTblSaleTimes();
            String specialstartTime1 = specialsaleTimes.get(0).getStartTime();
            String specialendTime1 = specialsaleTimes.get(0).getEndTime();
            difTime = specialSaleDate.getDifTime();
            startTime = specialstartTime1;// 2019-06-09 (09:00:00)
            endTime = specialendTime1;//2019-06-10 (02:45:00)
            openTimeOne = DateUtil.formatDate(matchTime, 1) + " " + startTime;//09:00:00
            endTimeOne = DateUtil.formatDate(matchTime, 1) + " " + endTime;//24:00:00
            //跨天时间
            if (DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS).after(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
                endTimeOne = DateUtil.formatTime(DateUtil.getIntervalDays(new Timestamp(matchTime.getTime()), 1), "yyyy-MM-dd") + " " + endTime;
            }
            //之间
            if (DateUtil.isRealBetween(matchTime, DateUtils.addMinutes(DateUtil.formatToDate(openTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), difTime), DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))) {
                deadTime = DateUtils.addMinutes(matchTime, -difTime);
            }else if (matchTime.after(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS))){
                //大于截止时间
                deadTime = DateUtils.addMinutes(DateUtil.formatToDate(endTimeOne, DateUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), -difTime);
            }else{
                //一般配置逻辑，上面已得到结果

            }
        }
        return deadTime;
    }

    @Override
    public List<TblMatch> findMatchListByMatchTypeAndMatchStatusAndHotStatus(Integer matchType, Integer matchStatus) {
        return repository.findMatchListByMatchTypeAndMatchStatusAndMatchSnNotEmptyAAndHotStatus(matchType, matchStatus);
    }

}
