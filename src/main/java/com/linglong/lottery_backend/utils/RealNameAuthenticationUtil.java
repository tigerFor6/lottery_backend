package com.linglong.lottery_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linglong.lottery_backend.common.error.UserNotFoundException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: qihua.li
 * @since: 2019-04-25
 */
@Component
public class RealNameAuthenticationUtil {

    private static final String HOST = "http://idcard3.market.alicloudapi.com";
    private static final String PATH = "/idcardAudit";
    private static final String METHOD = "GET";
    private static final String APP_CODE = "3d0ce1b2ffc84ecc9bfb169fcc3482ec";

    public Map<String, Object> verify(String idcard, String realname) throws Exception {
        if (StringUtils.isEmpty(idcard) || StringUtils.isEmpty(realname)) {
            throw new UserNotFoundException("姓名和身份证号码不能为空");
        }
        Map<String, String> headers = new HashMap<>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + APP_CODE);
        Map<String, String> querys = new HashMap<>();
        querys.put("idcard", idcard);
        querys.put("name", realname);
        HttpResponse response = HttpUtil.doGet(HOST, PATH, METHOD, headers, querys);
        //获取response的body
        String string = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(string);
        int retCode = jsonNode.findPath("ret_code").asInt();
        Map<String, Object> map = new HashMap<>();
        if (retCode == 0) {
            //认证成功
            int code = jsonNode.findPath("code").asInt();
            if (code == 0) {
                //匹配
                map.put("message", "匹配");
                map.put("flag", true);
            } else if (code == 1) {
                //不匹配-身份证号存在
                map.put("message", "身份证号码和姓名不匹配");
                map.put("flag", false);
            } else if (code == 2) {
                //无此身份证号码
                map.put("message", "无此身份证号码");
                map.put("flag", false);
            }
        } else {
            //认证失败
            map.put("message", "认证失败");
            map.put("flag", false);
        }
        return map;
    }
}