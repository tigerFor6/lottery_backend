package com.linglong.lottery_backend.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 化增光
 */
@Slf4j
public class MD5Util {
    /**
     * MD5加密字符串（32位大写）
     *
     * @param str 需要进行MD5加密的字符串
     * @return 加密后的字符串（大写）
     */
    public static String md5Encrypt32Upper(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            //一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方）
            return new BigInteger(1, md.digest()).toString(16).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 生成字符串的md5校验值
     *
     * @param s
     * @return
     */
    public static String getMD5String(String s) {
        if (StringUtils.isBlank(s)){
            return null;
        }
        return getMD5String(s.getBytes());
    }

    /**
     * 生成字符串的md5校验值，指定字符编码方式
     *
     * @param s
     * @return
     */
    public static String getMD5String(String s, String charset) {
        try {
            return getMD5String(s.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            log.error("md5摘要发生异常，getMD5String有问题，s is " + s + " charset is" + charset, e);
        }
        return null;
    }

    public static String getMD5String(byte[] bytes) {
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) {
        if (file == null || !file.exists()) {
            log.warn("File not found!" + (file == null ? "null" : file.getAbsolutePath()));
        }
        MessageDigest messagedigest = null;
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                messagedigest.update(buffer, 0, numRead);
            }
        } catch (Exception e) {
            log.warn("Get MD5 Error!");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            }
        }

        return bufferToHex(messagedigest.digest());
    }

    /**
     * 判断字符串的md5校验码是否与一个已知的md5码相匹配
     *
     * @param sourceStr 要校验的字符串
     * @param checkStr  已知的md5校验码
     * @return
     */
    public static boolean check(String sourceStr, String checkStr) {
        if (sourceStr == null && checkStr == null) {
            return true;
        }
        if (sourceStr == null || checkStr == null) {
            return false;
        }
        String s = getMD5String(sourceStr);
        return s.equals(checkStr);
    }

    public static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = HEX_DIGITS[(bt & 0xf0) >>> 4];// 取字节中高 4 位的数字转换
        char c1 = HEX_DIGITS[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }
}
