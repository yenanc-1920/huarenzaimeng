package com.huarenzaimeng.module.recharge.service;

import com.huarenzaimeng.common.util.MoneyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PriceService {

    public BigDecimal calculateAutoPrice(BigDecimal usdCost, BigDecimal fxRate,
                                         BigDecimal lossRate, BigDecimal profitRate) {
        if (fxRate == null || fxRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid fxRate for price calculation, using fallback");
            return usdCost.multiply(new BigDecimal("7.2")).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        return MoneyUtil.usdToCny(usdCost, fxRate, lossRate, profitRate);
    }

    public boolean isPriceInverted(BigDecimal cnyPrice, BigDecimal usdCost, BigDecimal fxRate) {
        if (fxRate == null || fxRate.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal costInCny = usdCost.multiply(fxRate).setScale(2, java.math.RoundingMode.HALF_UP);
        return cnyPrice.compareTo(costInCny) < 0;
    }

    public boolean isPriceChangeExceedsThreshold(BigDecimal oldPrice, BigDecimal newPrice, BigDecimal threshold) {
        if (oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        BigDecimal changeRate = newPrice.subtract(oldPrice).abs()
                .divide(oldPrice, 4, java.math.RoundingMode.HALF_UP);
        return changeRate.compareTo(threshold) > 0;
    }
}
