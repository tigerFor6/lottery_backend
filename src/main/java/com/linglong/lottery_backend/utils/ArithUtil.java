package com.linglong.lottery_backend.utils;

import java.math.BigDecimal;

/**
 * 化增光
 */
public class ArithUtil {
    // 这个类不能实例化
    private ArithUtil() {
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */

    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        return v1.add(v2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */

    public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
        return v1.subtract(v2);
    }

    /**
     * 提供精确的乘法法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */

    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        return v1.multiply(v2);
    }

    /**
     * 提供精确的除法法运算。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */

    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2);
    }
}
