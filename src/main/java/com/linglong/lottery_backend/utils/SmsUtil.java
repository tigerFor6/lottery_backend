package com.linglong.lottery_backend.utils;

import com.linglong.lottery_backend.common.error.MobileFormatException;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
@Component
@Slf4j
public class SmsUtil {

    private static final String DEF_CHATSET = "UTF-8";
    private static final int DEF_CONN_TIMEOUT = 30000;
    private static final int DEF_READ_TIMEOUT = 30000;
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

    private static final String ID_TEMPLATE = "144896";
    private static final String KEY = "360e7607cc9e12666dd88d528f260d06";
    private static final String URL = "http://v.juhe.cn/sms/send";

    public String send(String mobile, String code) {
        //校验手机号
        boolean isLegal = isPhoneLegal(mobile);
        if (!isLegal) {
            throw new MobileFormatException("手机号码格式错误，请重新输入正确的大陆地区或港澳地区手机号");
        }
        String result = null;
        Map<String, String> params = new HashMap<String, String>();//请求参数
        params.put("mobile", mobile);//接受短信的用户手机号码
        params.put("tpl_id", ID_TEMPLATE);//您申请的短信模板ID，根据实际情况修改
        params.put("tpl_value", "#code#=" + code);//您设置的模板变量，根据实际情况修改
        params.put("key", KEY);//应用APPKEY(应用详细页查询)
        try {
            result = net(URL, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if (object.getInt("error_code") == 0) {
                System.out.println(object.get("result"));
                return object.get("result").toString();
            } else {
                System.out.println(object.get("error_code") + ":" + object.get("reason"));
                return object.get("error_code") + ":" + object.get("reason");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "出现未知异常";
    }

    //手机号码校验
    public boolean isPhoneLegal(String str) throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    public boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^((13[0-9])|(14[0-9])|(15[0-9])|(166)|(17[0-9])" +
                "|(18[0-9])|(19[0-9]))\\d{8}$";
//        String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[1,3,5,6,7,8])" +
//                "|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public boolean isHKPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return 网络请求字符串
     * @throws Exception
     */
    private String net(String strUrl, Map<String, String> params, String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if (method == null || method.equals("GET")) {
                strUrl = strUrl + "?" + urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (method == null || method.equals("GET")) {
                conn.setRequestMethod("GET");
            } else {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params != null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    //将map型转为请求参数型
    private String urlencode(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void sendWarningMessage(String mobile, String templateCode) {
        Map<String, String> params = new HashMap<String, String>();//请求参数
        params.put("mobile", mobile);//接受短信的用户手机号码
        params.put("tpl_id", templateCode);//您申请的短信模板ID，根据实际情况修改
        //params.put("tpl_value", "#code#=" + code);//您设置的模板变量，根据实际情况修改
        params.put("key", KEY);//应用APPKEY(应用详细页查询)
        try {
            String result = net(URL, params, "GET");
            log.info("赛果冲突: {}",result);
        } catch (Exception e) {
            log.error("sendWarningMessage: {}",e);
        }
    }
}