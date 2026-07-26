package com.huarenzaimeng.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyUtil {

    private MoneyUtil() {
    }

    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal applyRate(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(BigDecimal.ONE.add(rate)).setScale(2, RoundingMode.HALF_UP);
    }

    public static boolean isGreaterThan(BigDecimal a, BigDecimal b) {
        return a.compareTo(b) > 0;
    }

    public static BigDecimal usdToCny(BigDecimal usdAmount, BigDecimal fxRate, BigDecimal lossRate, BigDecimal profitRate) {
        BigDecimal result = usdAmount.multiply(fxRate);
        result = result.multiply(BigDecimal.ONE.add(lossRate));
        result = result.multiply(BigDecimal.ONE.add(profitRate));
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
