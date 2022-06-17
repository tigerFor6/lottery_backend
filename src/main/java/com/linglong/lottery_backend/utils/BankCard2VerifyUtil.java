package com.linglong.lottery_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: qihua.li
 * @since: 2019-04-01
 */
@Component
public class BankCard2VerifyUtil {
    private static final String HOST = "https://bank.market.alicloudapi.com";
    private static final String PATH = "/bank2";
    private static final String METHOD = "GET";
    private static final String APP_CODE = "3d0ce1b2ffc84ecc9bfb169fcc3482ec";

    public JsonNode verify(String name, String bankcardNum) throws Exception {

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + APP_CODE);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("acct_name", name);
        querys.put("acct_pan", bankcardNum);
        querys.put("needBelongArea", "true");

        HttpResponse response = HttpUtil.doGet(HOST, PATH, METHOD, headers, querys);
        //获取response的body
        //System.out.println(EntityUtils.toString(response.getEntity()));
        String json = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(json);
    }
}
