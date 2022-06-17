//
//
//package com.linglong.lottery_backend.lottery.match;
//
//import com.alibaba.fastjson.JSON;
//import com.linglong.lottery_backend.prize.entity.custom.BettingMatch;
//import com.linglong.lottery_backend.prize.entity.custom.BettingOdds;
//import com.linglong.lottery_backend.prize.entity.custom.BettingOrder;
//import com.linglong.lottery_backend.prize.service.TblBettingPrizeService;
//import com.linglong.lottery_backend.prize.task.PrizeAsyncExecutorTask;
//import org.apache.rocketmq.client.exception.MQBrokerException;
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.DefaultMQProducer;
//import org.apache.rocketmq.client.producer.SendCallback;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//import org.apache.rocketmq.remoting.exception.RemotingException;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.messaging.support.MessageBuilder;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class MatchTests {
//
//    private static final Logger logger = LoggerFactory.getLogger(MatchTests.class);
//
//    @Resource
//    private RocketMQTemplate rocketMQTemplate;
//
////    @Autowired
////    TblBettingPrizeService bettingPrizeService;
//
//    @Autowired
//    TblBettingPrizeService bettingPrizeService;

//    @Autowired
//    PrizeAsyncExecutorTask prizeAsyncExecutorTask;
//
//    @Test
//    public void contextLoads() throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
//        //String topic = "match";
//        String data = "{\"match_odds_id\": [18480,18481,18531,18557,18583]}";
//
//        rocketMQTemplate.send("lottery:statistics", MessageBuilder.withPayload(data).build());
//    }
//
//
//    @Test
//    public void testPrize() {
//        String body = "{\"open_prize_status\":0,\"matchs\":[{\"plays\":{\"rqspf\":{\"handicap\":\"-1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"2.17\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.55\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.40\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"11\",\"odds\":\"5.80\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"4.50\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"16.00\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.46\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.85\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"4.65\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"3.35\"},{\"result\":\"--\",\"item\":\"7\",\"odds\":\"20.00\"},{\"result\":\"--\",\"item\":\"6\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"2\",\"odds\":\"3.60\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"5.10\"},{\"result\":\"--\",\"item\":\"5\",\"odds\":\"7.75\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"4\",\"odds\":\"4.70\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"10\",\"odds\":\"7.00\"},{\"result\":\"--\",\"item\":\"42\",\"odds\":\"39.00\"},{\"result\":\"--\",\"item\":\"50\",\"odds\":\"50.00\"},{\"result\":\"--\",\"item\":\"52\",\"odds\":\"80.00\"},{\"result\":\"--\",\"item\":\"90\",\"odds\":\"30.00\"},{\"result\":\"--\",\"item\":\"99\",\"odds\":\"250.0\"},{\"result\":\"--\",\"item\":\"22\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"34.00\"},{\"result\":\"--\",\"item\":\"23\",\"odds\":\"34.00\"},{\"result\":\"--\",\"item\":\"09\",\"odds\":\"120.0\"},{\"result\":\"--\",\"item\":\"05\",\"odds\":\"500.0\"},{\"result\":\"--\",\"item\":\"02\",\"odds\":\"24.00\"}]}},\"match_id\":29408,\"match_sn\":\"周一001\",\"match_name\":\"阿斯顿维拉,米尔沃尔\"},{\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.95\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"3.46\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.62\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"30\",\"odds\":\"22.00\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"12.00\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.82\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.60\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"1.90\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"5\",\"odds\":\"6.50\"},{\"result\":\"--\",\"item\":\"6\",\"odds\":\"11.00\"},{\"result\":\"--\",\"item\":\"7\",\"odds\":\"14.00\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"03\",\"odds\":\"19.00\"},{\"result\":\"--\",\"item\":\"52\",\"odds\":\"150.0\"},{\"result\":\"--\",\"item\":\"51\",\"odds\":\"120.0\"},{\"result\":\"--\",\"item\":\"41\",\"odds\":\"50.00\"},{\"result\":\"--\",\"item\":\"12\",\"odds\":\"7.50\"}]}},\"match_id\":26160,\"match_sn\":\"周一002\",\"match_name\":\"多德勒支,阿尔梅勒城\"},{\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"3.80\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.80\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.58\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.85\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.35\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"1.96\"}]}},\"match_id\":26164,\"match_sn\":\"周一003\",\"match_name\":\"奥斯,前进之鹰\"},{\"plays\":{\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.14\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"5.55\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"9.75\"}]}},\"match_id\":26166,\"match_sn\":\"周一004\",\"match_name\":\"特温特,阿尔克马尔青年队\"}],\"bonus\":0,\"chuan_guan\":\"4\",\"multiple\":50,\"hit_prize_status\":0,\"bill_status\":0,\"pay_status\":1,\"bet_number\":8352,\"deadline_match\":\"阿斯顿维拉VS米尔沃尔\",\"delivery_prize_status\":0,\"deadline\":\"2019年04月22日 20点00分\",\"order_id\":1120251276773429200}";
//
//        BettingOrder bettingOrder = assembleBettingOrder(body);
//        //prizeAsyncExecutorTask.doSaveBettingOrder(bettingOrder);
//
//        // bettingPrizeService.saveBettingOrder(bettingOrder);
//    }
//
//    private BettingOrder assembleBettingOrder(String data) {
//        BettingOrder bettingOrder = JSON.parseObject(data, BettingOrder.class);
//        bettingOrder.getMatchs().forEach(e -> {
//            HashMap<String, List<BettingOdds>> odds = new HashMap<>();
//            e.getPlays().forEach((k, v) -> {
//                HashMap<String, Object> m = JSON.parseObject(v, HashMap.class);
//                List<BettingOdds> list = JSON.parseArray(JSON.toJSONString(m.get("items")), BettingOdds.class);
//                odds.put(k, list);
//            });
//            e.setOdds(odds);
//        });
//        return bettingOrder;
//    }
//
//}


//    PrizeAsyncExecutorTask prizeAsyncExecutorTask;
//
//    @Test
//    public void contextLoads() throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
//        //String topic = "match";
//        String data = "{\"match_odds_id\": [18480,18481,18531,18557,18583]}";
//
//        rocketMQTemplate.send("lottery:statistics", MessageBuilder.withPayload(data).build());
//    }
//
//
//    @Test
//    public void testPrize() {
//        String body = "{\"open_prize_status\":0,\"matchs\":[{\"plays\":{\"rqspf\":{\"handicap\":\"-1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"2.17\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.55\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.40\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"11\",\"odds\":\"5.80\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"4.50\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"16.00\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.46\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.85\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"4.65\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"3.35\"},{\"result\":\"--\",\"item\":\"7\",\"odds\":\"20.00\"},{\"result\":\"--\",\"item\":\"6\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"2\",\"odds\":\"3.60\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"5.10\"},{\"result\":\"--\",\"item\":\"5\",\"odds\":\"7.75\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"4\",\"odds\":\"4.70\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"10\",\"odds\":\"7.00\"},{\"result\":\"--\",\"item\":\"42\",\"odds\":\"39.00\"},{\"result\":\"--\",\"item\":\"50\",\"odds\":\"50.00\"},{\"result\":\"--\",\"item\":\"52\",\"odds\":\"80.00\"},{\"result\":\"--\",\"item\":\"90\",\"odds\":\"30.00\"},{\"result\":\"--\",\"item\":\"99\",\"odds\":\"250.0\"},{\"result\":\"--\",\"item\":\"22\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"34.00\"},{\"result\":\"--\",\"item\":\"23\",\"odds\":\"34.00\"},{\"result\":\"--\",\"item\":\"09\",\"odds\":\"120.0\"},{\"result\":\"--\",\"item\":\"05\",\"odds\":\"500.0\"},{\"result\":\"--\",\"item\":\"02\",\"odds\":\"24.00\"}]}},\"match_id\":29408,\"match_sn\":\"周一001\",\"match_name\":\"阿斯顿维拉,米尔沃尔\"},{\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.95\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"3.46\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.62\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"30\",\"odds\":\"22.00\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"12.00\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.82\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.60\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"1.90\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"5\",\"odds\":\"6.50\"},{\"result\":\"--\",\"item\":\"6\",\"odds\":\"11.00\"},{\"result\":\"--\",\"item\":\"7\",\"odds\":\"14.00\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"03\",\"odds\":\"19.00\"},{\"result\":\"--\",\"item\":\"52\",\"odds\":\"150.0\"},{\"result\":\"--\",\"item\":\"51\",\"odds\":\"120.0\"},{\"result\":\"--\",\"item\":\"41\",\"odds\":\"50.00\"},{\"result\":\"--\",\"item\":\"12\",\"odds\":\"7.50\"}]}},\"match_id\":26160,\"match_sn\":\"周一002\",\"match_name\":\"多德勒支,阿尔梅勒城\"},{\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"3.80\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.80\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.58\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.85\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.35\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"1.96\"}]}},\"match_id\":26164,\"match_sn\":\"周一003\",\"match_name\":\"奥斯,前进之鹰\"},{\"plays\":{\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.14\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"5.55\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"9.75\"}]}},\"match_id\":26166,\"match_sn\":\"周一004\",\"match_name\":\"特温特,阿尔克马尔青年队\"}],\"bonus\":0,\"chuan_guan\":\"4\",\"multiple\":50,\"hit_prize_status\":0,\"bill_status\":0,\"pay_status\":1,\"bet_number\":8352,\"deadline_match\":\"阿斯顿维拉VS米尔沃尔\",\"delivery_prize_status\":0,\"deadline\":\"2019年04月22日 20点00分\",\"order_id\":1120251276773429200}";
//
//        BettingOrder bettingOrder = assembleBettingOrder(body);
//        prizeAsyncExecutorTask.doSaveBettingOrder(bettingOrder);
//
//        // bettingPrizeService.saveBettingOrder(bettingOrder);
//    }
//
//    private BettingOrder assembleBettingOrder(String data) {
//        BettingOrder bettingOrder = JSON.parseObject(data, BettingOrder.class);
//        bettingOrder.getMatchs().forEach(e -> {
//            HashMap<String, List<BettingOdds>> odds = new HashMap<>();
//            e.getPlays().forEach((k, v) -> {
//                HashMap<String, Object> m = JSON.parseObject(v, HashMap.class);
//                List<BettingOdds> list = JSON.parseArray(JSON.toJSONString(m.get("items")), BettingOdds.class);
//                odds.put(k, list);
//            });
//            e.setOdds(odds);
//        });
//        return bettingOrder;
//    }
//
//}
//
//
