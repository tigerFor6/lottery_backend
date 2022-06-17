package com.linglong.lottery_backend.message.sms;

import com.linglong.lottery_backend.common.error.MobileFormatException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
@Slf4j
public class SmsSend {
    @Value("${sms.url}")
    private String url;
    @Value("${sms.user}")
    private String user;
    @Value("${sms.passwd}")
    private String passwd;

    private static final String DTYPE = "1";

    /**
     * 那时快短信
     * @param mobile
     * @param msg
     * @return
     */
    public String send(String mobile, String msg) {
        //校验手机号
        boolean isLegal = isPhoneLegal(mobile);
        if (!isLegal) {
            throw new MobileFormatException("手机号码格式错误，请重新输入正确的大陆地区或港澳地区手机号");
        }
        String result = null;
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("user", user)
                .add("passwd", passwd)
                .add("msg", msg)
                .add("mobs", mobile)
                .add("dtype", DTYPE)
                .build();

        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            result = response.body().string();
            Document dom = DocumentHelper.parseText(result);
            Element rootElement = dom.getRootElement();
            String resultCode = rootElement.element("result").getText();
            return resultCode;
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

}