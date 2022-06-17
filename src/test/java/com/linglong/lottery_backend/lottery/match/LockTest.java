//package com.linglong.lottery_backend.lottery.match;
//
//import com.linglong.lottery_backend.lottery.match.entity.TblMatch;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.UnsupportedEncodingException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @ProjectName: lottery_backend
// * @Package: com.linglong.lottery_backend.lottery.match
// * @ClassName: LockTest
// * @Author: hua
// * @Description: ${description}
// * @Date: 2019/4/23 11:54
// * @Version: 1.0
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class LockTest {
//
//    private final Logger logger = LoggerFactory.getLogger(LockTest.class);
//
//    @Test
//    public void contextLoads() {
//        for(int i=0; i<10; i++){
//            new RedissonLockReadThread().start();
//        }
//    }
//
//    class RedissonLockReadThread extends Thread {
//
//        @Override
//        public void run() {
//            TblMatch match = new TblMatch();
//            match.setId(new Long(123));
//            testLock(match);
//        }
//    }
//
//    @LockAction("'updateKey'")
//    private void testLock(TblMatch match){
//        try {
//            long start = System.currentTimeMillis();
//            Thread.sleep(5000);
//            long end = System.currentTimeMillis();
//            System.out.println("123");
//            System.out.println(end-start);
//        } catch (InterruptedException e) {
//            logger.error("exp", e);
//        }
//    }
//}
