package com.linglong.lottery_backend.utils;

import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ynght on 2019-04-20
 */
public class CommonUtil {
    public static String initBeanName(String gameEn) {
        if (StringUtils.isBlank(gameEn)) {
            throw new BusinessException("initBeanName error. gameEn:" + gameEn);
        }
        char chars[] = gameEn.toCharArray();
        boolean needChange = Boolean.FALSE;
        StringBuffer beanName = new StringBuffer();
        for (char ch : chars) {
            String chStr = String.valueOf(ch);
            if (CommonConstant.COMMON_SPLIT_STR.equals(chStr)) {
                needChange = Boolean.TRUE;
                continue;
            }
            if (needChange) {
                needChange = Boolean.FALSE;
                chStr = chStr.toUpperCase();
            }
            beanName.append(chStr);
        }
        return beanName.toString();
    }

    public static String mergeUnionKey(Object... args) {
        if (args.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (Object arg : args) {
            sb.append(arg).append(CommonConstant.COMMON_VERTICAL_STR);
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * 计算排列组合的值
     *
     * @param total
     * @param select
     * @return
     */
    public static int combine(int total, int select) {
        if (select > total) {
            return 0;
        } else if (select == total) {
            return 1;
        } else if (total == 0) {
            return 1;
        } else {
            if (select > total / 2)
                select = total - select;

            long result = 1;
            for (int i = total; i > total - select; i--) {
                result *= i;
                if (result < 0)
                    return -1;
            }
            for (int j = select; j > 0; j--) {
                result /= j;
            }
            if (result > Integer.MAX_VALUE)
                return -1;
            return (int) result;
        }
    }

    /**
     * 从n个对象中选择m个的所有排列
     *
     * @param a
     * @param m
     * @return
     */
    public static List<String[]> combine(String[] a, int m) {
        List<String[]> result = new ArrayList<String[]>();
        int n = a.length;
        int[] bs = new int[n];
        if (m > n) {
            throw new RuntimeException("Can not get " + n + " elements from " + m + " elements!");
        } else if (m == n) {
            result.add(a);
            return result;
        }

        for (int i = 0; i < n; i++) {
            bs[i] = 0;
        }
        //初始化
        for (int i = 0; i < m; i++) {
            bs[i] = 1;
        }
        boolean flag = true;
        boolean tempFlag = false;
        int pos = 0;
        int sum = 0;
        //首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
        do {
            sum = 0;
            pos = 0;
            tempFlag = true;
            result.add(getElement(bs, a, m));

            for (int i = 0; i < n - 1; i++) {
                if (bs[i] == 1 && bs[i + 1] == 0) {
                    bs[i] = 0;
                    bs[i + 1] = 1;
                    pos = i;
                    break;
                }
            }
            //将左边的1全部移动到数组的最左边

            for (int i = 0; i < pos; i++) {
                if (bs[i] == 1) {
                    sum++;
                }
            }
            for (int i = 0; i < pos; i++) {
                if (i < sum) {
                    bs[i] = 1;
                } else {
                    bs[i] = 0;
                }
            }

            //检查是否所有的1都移动到了最右边
            for (int i = n - m; i < n; i++) {
                if (bs[i] == 0) {
                    tempFlag = false;
                    break;
                }
            }
            if (tempFlag == false) {
                flag = true;
            } else {
                flag = false;
            }

        }
        while (flag);
        result.add(getElement(bs, a, m));

        return result;
    }

    private static String[] getElement(int[] bs, String[] a, int m) {
        String[] result = new String[m];
        int pos = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == 1) {
                result[pos] = a[i];
                pos++;
            }
        }
        return result;
    }

//    public static Integer getRequestOrigin(HttpServletRequest request) {
//        String requestHeader = request.getHeader("user-agent").toLowerCase();
//        String[] deviceArray = new String[]{"android", "iphone", "ios", "windows phone"};
//        if (requestHeader == null) {
//            return 3;
//        }
//        requestHeader = requestHeader.toLowerCase();
//        if (requestHeader.indexOf("android") > 0) {
//            return 1;
//        }
//        for (int i = 0; i < deviceArray.length; i++) {
//            if (requestHeader.indexOf(deviceArray[i]) > 0) {
//                return 2;
//            }
//        }
//        return 3;
//    }
}
