package com.linglong.lottery_backend.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.scripts.JO;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: qihua.li
 * @since: 2019-03-21
 */
@Component
public class BankCard4VerifyUtil {

    private static final String HOST = "https://ibankcard.market.alicloudapi.com";
    private static final String PATH = "/integrationBankCard/check";
    private static final String METHOD = "POST";
    private static final String APP_CODE = "3d0ce1b2ffc84ecc9bfb169fcc3482ec";

    public JsonNode verify(String... elements) throws Exception {

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + APP_CODE);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("bankCard", elements[0]);
        bodys.put("name", elements[1]);
        if (elements.length == 3) {
            bodys.put("idCard", elements[2]);
        } else if (elements.length == 4) {
            bodys.put("idCard", elements[2]);
            bodys.put("phone", elements[3]);
        }
        System.out.println("-------" + JSON.toJSONString(bodys));
        HttpResponse response = HttpUtil.doPost(HOST, PATH, METHOD, headers, querys, bodys);
        //获取response的body
        //System.out.println(EntityUtils.toString(response.getEntity()));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = EntityUtils.toString(response.getEntity());
        System.out.println("-------" + json);
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode;
    }
}
