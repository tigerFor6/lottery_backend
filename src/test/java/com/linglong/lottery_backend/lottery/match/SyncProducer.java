package com.linglong.lottery_backend.lottery.match;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class SyncProducer {
//    public static void main(String[] args) throws Exception {
//        //Instantiate with a producer group name.
//        DefaultMQProducer producer = new
//                DefaultMQProducer("lottery_match");
//        // Specify name server addresses.
//        producer.setNamesrvAddr("39.98.69.232:9876");
//        producer.setSendMsgTimeout(100000);
//        //Launch the instance.
//        producer.start();
//        for (int i = 0; i < 10 ; i++) {
//            String data = "{\"open_prize_status\":0,\"matchs\":[{\"plays\":{\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.45\"}]}},\"match_id\":23169,\"match_sn\":\"周五001\",\"match_name\":\"阿德莱德联,墨尔本胜利\"},{\"plays\":{\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.10\"}]}},\"match_id\":32277,\"match_sn\":\"周五002\",\"match_name\":\"广岛三箭,东京FC\"},{\"plays\":{\"rqspf\":{\"handicap\":\"-1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"2.01\"}]}},\"match_id\":29396,\"match_sn\":\"周五003\",\"match_name\":\"布里斯托尔城,雷丁\"}],\"bonus\":0,\"chuan_guan\":\"3\",\"multiple\":50,\"hit_prize_status\":0,\"bill_status\":0,\"pay_status\":1,\"bet_number\":1,\"deadline_match\":\"阿德莱德联VS墨尔本胜利\",\"delivery_prize_status\":0,\"deadline\":\"2019年04月19日 17点50分\",\"order_id\":1119058422713684000}";
//            Message msg = new Message("match" /* Topic */,
//                    "betting" /* Tag */,
//                    data.getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//            );
//            //Call send message to deliver message to one of brokers.
//            SendResult sendResult = producer.send(msg);
//            System.out.printf("%s%n", sendResult);
//        }
//
//        //Shut down once the producer instance is not longer in use.
//        producer.shutdown();
//    }

    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new
                DefaultMQProducer("crawler");
        // Specify name server addresses.
        producer.setNamesrvAddr("39.98.69.232:9876");
        //Launch the instance.
        producer.start();
        //String data = "{\"match_id\":26160,\"result\":[{\"code\":\"spf\",\"result\":\"3\",\"odds\":\"1.51\"}]}";
        String data = "{\"match_odds_id\": [49751]}";
        //String data = "{\"match_odds_id\": [18480,18481,18531,18557,18583]}";
        //String data = "{\"match_id\":44122,\"match_result\":true}
        // String data = "{\"open_prize_status\":0,\"matchs\":[{\"plays\":{\"rqspf\":{\"handicap\":\"-1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"3.95\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.85\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.55\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"30\",\"odds\":\"65.00\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"25.00\"},{\"result\":\"--\",\"item\":\"33\",\"odds\":\"1.41\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.13\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"5.75\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"10.00\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"18.00\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"6.60\"},{\"result\":\"--\",\"item\":\"2\",\"odds\":\"3.90\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"3.40\"},{\"result\":\"--\",\"item\":\"7\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"6\",\"odds\":\"11.00\"},{\"result\":\"--\",\"item\":\"5\",\"odds\":\"6.50\"},{\"result\":\"--\",\"item\":\"4\",\"odds\":\"4.15\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"41\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"20\",\"odds\":\"5.50\"},{\"result\":\"--\",\"item\":\"21\",\"odds\":\"7.50\"},{\"result\":\"--\",\"item\":\"50\",\"odds\":\"18.00\"},{\"result\":\"--\",\"item\":\"52\",\"odds\":\"65.00\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"9.50\"},{\"result\":\"--\",\"item\":\"40\",\"odds\":\"9.50\"},{\"result\":\"--\",\"item\":\"32\",\"odds\":\"23.00\"},{\"result\":\"--\",\"item\":\"30\",\"odds\":\"6.25\"},{\"result\":\"--\",\"item\":\"51\",\"odds\":\"26.00\"},{\"result\":\"--\",\"item\":\"90\",\"odds\":\"12.00\"},{\"result\":\"--\",\"item\":\"33\",\"odds\":\"70.00\"},{\"result\":\"--\",\"item\":\"99\",\"odds\":\"300.0\"},{\"result\":\"--\",\"item\":\"23\",\"odds\":\"60.00\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"90.00\"},{\"result\":\"--\",\"item\":\"03\",\"odds\":\"200.0\"},{\"result\":\"--\",\"item\":\"12\",\"odds\":\"30.00\"},{\"result\":\"--\",\"item\":\"02\",\"odds\":\"80.00\"},{\"result\":\"--\",\"item\":\"01\",\"odds\":\"35.00\"},{\"result\":\"--\",\"item\":\"14\",\"odds\":\"250.0\"},{\"result\":\"--\",\"item\":\"24\",\"odds\":\"250.0\"},{\"result\":\"--\",\"item\":\"05\",\"odds\":\"800.0\"},{\"result\":\"--\",\"item\":\"15\",\"odds\":\"750.0\"},{\"result\":\"--\",\"item\":\"25\",\"odds\":\"750.0\"},{\"result\":\"--\",\"item\":\"09\",\"odds\":\"200.0\"},{\"result\":\"--\",\"item\":\"22\",\"odds\":\"22.00\"}]}},\"match_id\":44147,\"match_sn\":\"周三007\",\"match_name\":\"北京国安,布里兰\"},{\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.60\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"3.10\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"13\",\"odds\":\"9.25\"},{\"result\":\"--\",\"item\":\"30\",\"odds\":\"21.00\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"13.50\"},{\"result\":\"--\",\"item\":\"33\",\"odds\":\"7.00\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"3.62\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.40\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"6\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"2\",\"odds\":\"3.60\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"3.35\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"21\",\"odds\":\"11.00\"},{\"result\":\"--\",\"item\":\"30\",\"odds\":\"55.00\"},{\"result\":\"--\",\"item\":\"99\",\"odds\":\"200.0\"},{\"result\":\"--\",\"item\":\"03\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"02\",\"odds\":\"8.50\"},{\"result\":\"--\",\"item\":\"42\",\"odds\":\"80.00\"},{\"result\":\"--\",\"item\":\"22\",\"odds\":\"11.50\"},{\"result\":\"--\",\"item\":\"00\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"01\",\"odds\":\"7.50\"},{\"result\":\"--\",\"item\":\"14\",\"odds\":\"24.00\"},{\"result\":\"--\",\"item\":\"24\",\"odds\":\"40.00\"},{\"result\":\"--\",\"item\":\"05\",\"odds\":\"60.00\"},{\"result\":\"--\",\"item\":\"15\",\"odds\":\"50.00\"},{\"result\":\"--\",\"item\":\"25\",\"odds\":\"75.00\"}]}},\"match_id\":44123,\"match_sn\":\"周三008\",\"match_name\":\"柔佛,山东鲁能\"},{\"plays\":{\"rqspf\":{\"handicap\":\"+1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"1.50\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.65\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"4.60\"}]},\"bqc\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"31\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"30\",\"odds\":\"26.00\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"6.75\"},{\"result\":\"--\",\"item\":\"00\",\"odds\":\"3.15\"},{\"result\":\"--\",\"item\":\"01\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"03\",\"odds\":\"33.00\"},{\"result\":\"--\",\"item\":\"33\",\"odds\":\"4.75\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"3\",\"odds\":\"2.85\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"2.08\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"2\",\"odds\":\"3.15\"},{\"result\":\"--\",\"item\":\"6\",\"odds\":\"20.00\"},{\"result\":\"--\",\"item\":\"5\",\"odds\":\"11.00\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.90\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"8.50\"},{\"result\":\"--\",\"item\":\"4\",\"odds\":\"5.90\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"50\",\"odds\":\"400.0\"},{\"result\":\"--\",\"item\":\"52\",\"odds\":\"400.0\"},{\"result\":\"--\",\"item\":\"30\",\"odds\":\"34.00\"},{\"result\":\"--\",\"item\":\"24\",\"odds\":\"70.00\"},{\"result\":\"--\",\"item\":\"03\",\"odds\":\"17.00\"},{\"result\":\"--\",\"item\":\"12\",\"odds\":\"7.00\"},{\"result\":\"--\",\"item\":\"11\",\"odds\":\"6.50\"},{\"result\":\"--\",\"item\":\"42\",\"odds\":\"100.0\"},{\"result\":\"--\",\"item\":\"00\",\"odds\":\"8.50\"},{\"result\":\"--\",\"item\":\"25\",\"odds\":\"200.0\"},{\"result\":\"--\",\"item\":\"13\",\"odds\":\"16.00\"},{\"result\":\"--\",\"item\":\"04\",\"odds\":\"40.00\"},{\"result\":\"--\",\"item\":\"23\",\"odds\":\"25.00\"},{\"result\":\"--\",\"item\":\"32\",\"odds\":\"29.00\"},{\"result\":\"--\",\"item\":\"40\",\"odds\":\"100.0\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"26.00\"},{\"result\":\"--\",\"item\":\"21\",\"odds\":\"9.00\"},{\"result\":\"--\",\"item\":\"20\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"33\",\"odds\":\"60.00\"},{\"result\":\"--\",\"item\":\"22\",\"odds\":\"14.00\"},{\"result\":\"--\",\"item\":\"02\",\"odds\":\"8.50\"}]}},\"match_id\":27730,\"match_sn\":\"周三009\",\"match_name\":\"埃斯基尔斯蒂纳,厄斯特松德\"},{\"plays\":{\"rqspf\":{\"handicap\":\"-1\",\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"0\",\"odds\":\"1.40\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"4.10\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"5.00\"}]},\"spf\":{\"is_dan_guan\":\"0\",\"items\":[{\"result\":\"--\",\"item\":\"1\",\"odds\":\"3.25\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"2.40\"}]},\"jqs\":{\"score\":\"null\",\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"6\",\"odds\":\"15.00\"},{\"result\":\"--\",\"item\":\"5\",\"odds\":\"8.50\"},{\"result\":\"--\",\"item\":\"0\",\"odds\":\"13.00\"},{\"result\":\"--\",\"item\":\"1\",\"odds\":\"4.90\"},{\"result\":\"--\",\"item\":\"2\",\"odds\":\"3.50\"},{\"result\":\"--\",\"item\":\"3\",\"odds\":\"3.30\"},{\"result\":\"--\",\"item\":\"7\",\"odds\":\"21.00\"},{\"result\":\"--\",\"item\":\"4\",\"odds\":\"4.70\"}]},\"bifen\":{\"is_dan_guan\":\"1\",\"items\":[{\"result\":\"--\",\"item\":\"21\",\"odds\":\"8.00\"},{\"result\":\"--\",\"item\":\"31\",\"odds\":\"17.00\"},{\"result\":\"--\",\"item\":\"51\",\"odds\":\"100.0\"},{\"result\":\"--\",\"item\":\"10\",\"odds\":\"8.50\"},{\"result\":\"--\",\"item\":\"42\",\"odds\":\"60.00\"},{\"result\":\"--\",\"item\":\"50\",\"odds\":\"150.0\"},{\"result\":\"--\",\"item\":\"12\",\"odds\":\"8.00\"},{\"result\":\"--\",\"item\":\"11\",\"odds\":\"6.50\"},{\"result\":\"--\",\"item\":\"02\",\"odds\":\"12.50\"},{\"result\":\"--\",\"item\":\"24\",\"odds\":\"60.00\"},{\"result\":\"--\",\"item\":\"05\",\"odds\":\"150.0\"},{\"result\":\"--\",\"item\":\"15\",\"odds\":\"120.0\"},{\"result\":\"--\",\"item\":\"25\",\"odds\":\"150.0\"}]}},\"match_id\":27727,\"match_sn\":\"周三010\",\"match_name\":\"法尔肯堡,天狼星\"}],\"bonus\":0,\"chuan_guan\":\"4\",\"multiple\":50,\"hit_prize_status\":0,\"bill_status\":2,\"pay_status\":1,\"user_id\":\"1118787252416286720\",\"bet_number\":1133652,\"deadline_match\":\"北京国安VS布里兰\",\"delivery_prize_status\":0,\"deadline\":\"2019年04月24日 20点00分\",\"order_id\":1121008790683127808}";
//        Message msg = new Message("crawler" /* Topic */,
//                "matchResult" /* Tag */,
//                data.getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//        );
        Message msg = new Message("crawler" /* Topic */,
                "updateInfo" /* Tag */,
                data.getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
        );
//        Message msg = new Message("lottery" /* Topic */,
//                "betting" /* Tag */,
//                data.getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//        );
        //Call send message to deliver message to one of brokers.
        SendResult sendResult = producer.send(msg);
        System.out.printf("%s%n", sendResult);

        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }

//
//    public static void main(String[] args) {
//        Integer status = 2;
//        System.out.println(status == 2);
//    }
}
