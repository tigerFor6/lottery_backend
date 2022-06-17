package com.linglong.lottery_backend.ticket.util;

import com.linglong.lottery_backend.ticket.entity.Platform;
import com.linglong.lottery_backend.ticket.enums.PlatformEnum;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.part.TicketBody;
import com.linglong.lottery_backend.utils.MD5Util;

/**
 *
 */
public class SignUtil {

    public static String bjgcSign(String timeTag, TicketBody body){
        Platform platform = PlatformEnum.BJGONGCAI.getPlatform();
        StringBuffer sb = new StringBuffer();
        sb.append(platform.getPlatformCode());
        sb.append(timeTag);
        sb.append(platform.getPlatformPwd());
        String xml = XStreamUtil.toXml(body);
        sb.append(convertFromXml(xml));
        String digest = MD5Util.getMD5String(sb.toString());
        return digest;
    }


    public static String convertFromXml(String str) {
        boolean flag = true;
        boolean quotesFlag = true;
        StringBuffer ans = new StringBuffer();
        String tmp = "";
        for (int i = 0; i < str.length(); i++) {
            if ('"' == str.charAt(i)) {
                ans.append(str.charAt(i));
                quotesFlag = !quotesFlag;
            } else if ('<' == str.charAt(i)) {
                tmp = tmp.trim();
                ans.append(tmp);
                flag = true;
                ans.append(str.charAt(i));
            } else if ('>' == str.charAt(i)) {
                if(quotesFlag){
                    flag = false;
                    ans.append(str.charAt(i));
                    tmp = "";
                }else{
                    ans.append("&gt;");
                }
            } else if (flag) {
                ans.append(str.charAt(i));
            } else {
                tmp += str.charAt(i);
            }
        }
        return ans.toString();
    }

}
