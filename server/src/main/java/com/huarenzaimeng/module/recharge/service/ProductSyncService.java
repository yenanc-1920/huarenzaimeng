package com.huarenzaimeng.module.recharge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.huarenzaimeng.infra.adapter.reloadly.ReloadlyOperatorAdapter;
import com.huarenzaimeng.module.recharge.entity.Operator;
import com.huarenzaimeng.module.recharge.entity.Product;
import com.huarenzaimeng.module.recharge.entity.ProductHistory;
import com.huarenzaimeng.module.recharge.mapper.OperatorMapper;
import com.huarenzaimeng.module.recharge.mapper.ProductHistoryMapper;
import com.huarenzaimeng.module.recharge.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Service
public class ProductSyncService {

    private final ReloadlyOperatorAdapter operatorAdapter;
    private final OperatorMapper operatorMapper;
    private final ProductMapper productMapper;
    private final ProductHistoryMapper productHistoryMapper;
    private final PriceService priceService;

    public ProductSyncService(ReloadlyOperatorAdapter operatorAdapter,
                              OperatorMapper operatorMapper,
                              ProductMapper productMapper,
                              ProductHistoryMapper productHistoryMapper,
                              PriceService priceService) {
        this.operatorAdapter = operatorAdapter;
        this.operatorMapper = operatorMapper;
        this.productMapper = productMapper;
        this.productHistoryMapper = productHistoryMapper;
        this.priceService = priceService;
    }

    @Transactional
    public SyncResult syncAll() {
        JsonNode operators = operatorAdapter.getOperators("BD");
        int newCount = 0;
        int updateCount = 0;

        for (JsonNode opNode : operators) {
            long reloadlyId = opNode.get("operatorId").asLong();
            String name = opNode.get("name").asText();
            boolean isData = opNode.has("data") && opNode.get("data").asBoolean();
            boolean isBundle = opNode.has("bundle") && opNode.get("bundle").asBoolean();
            BigDecimal fxRate = opNode.has("fxRate") && !opNode.get("fxRate").isNull()
                    ? new BigDecimal(opNode.get("fxRate").asText()) : null;

            Operator operator = operatorMapper.selectOne(
                    new LambdaQueryWrapper<Operator>().eq(Operator::getReloadlyOperatorId, reloadlyId));

            if (operator == null) {
                operator = new Operator();
                operator.setReloadlyOperatorId(reloadlyId);
                operator.setName(name);
                operator.setCountry("Bangladesh");
                operator.setBundle((isData || isBundle) ? 1 : 0);
                operator.setFxRate(fxRate);
                operator.setFxRateUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                operator.setStatus(1);
                operatorMapper.insert(operator);
                newCount++;
            } else {
                boolean changed = false;
                if (!name.equals(operator.getName())) {
                    operator.setName(name);
                    changed = true;
                }
                int bundleFlag = (isData || isBundle) ? 1 : 0;
                if (operator.getBundle() == null || operator.getBundle() != bundleFlag) {
                    operator.setBundle(bundleFlag);
                    changed = true;
                }
                if (fxRate != null && (operator.getFxRate() == null || fxRate.compareTo(operator.getFxRate()) != 0)) {
                    operator.setFxRate(fxRate);
                    operator.setFxRateUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    changed = true;
                }
                if (changed) {
                    operatorMapper.updateById(operator);
                    updateCount++;
                }
            }

            syncProductsForOperator(operator, opNode, isData);
        }

        log.info("Product sync completed: newOperators={}, updatedOperators={}", newCount, updateCount);
        return new SyncResult(newCount, updateCount);
    }

    private void syncProductsForOperator(Operator operator, JsonNode opNode, boolean isData) {
        String topupType = isData ? "BUNDLE" : "AIRTIME";

        JsonNode denominations = opNode.get("fixedTopupsDenominations");
        if (denominations != null && denominations.isArray() && !denominations.isEmpty()) {
            for (JsonNode amountNode : denominations) {
                double usdAmount = amountNode.asDouble();
                syncSingleProduct(operator, topupType, usdAmount, null);
            }
            return;
        }

        JsonNode localDenominations = opNode.get("localFixedTopupsDenominations");
        if (localDenominations != null && localDenominations.isArray() && !localDenominations.isEmpty()) {
            for (JsonNode amountNode : localDenominations) {
                double localAmount = amountNode.asDouble();
                syncSingleProduct(operator, topupType, 0, localAmount);
            }
            return;
        }

        if (!isData) {
            JsonNode suggested = opNode.get("suggestedTopupsDenominations");
            if (suggested != null && suggested.isArray() && !suggested.isEmpty()) {
                for (JsonNode amountNode : suggested) {
                    double usdAmount = amountNode.asDouble();
                    syncSingleProduct(operator, topupType, usdAmount, null);
                }
            }
        }
    }

    private void syncSingleProduct(Operator operator, String topupType, double usdCost, Double localAmount) {
        BigDecimal cost = BigDecimal.valueOf(usdCost);
        BigDecimal bdtAmount = localAmount != null ? BigDecimal.valueOf(localAmount) : null;

        Product existing;
        if (usdCost > 0) {
            existing = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                    .eq(Product::getOperatorId, operator.getId())
                    .eq(Product::getTopupType, topupType)
                    .eq(Product::getUsdCost, cost));
        } else {
            existing = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                    .eq(Product::getOperatorId, operator.getId())
                    .eq(Product::getTopupType, topupType)
                    .eq(Product::getBdtAmount, bdtAmount));
        }

        if (existing == null) {
            Product product = new Product();
            product.setOperatorId(operator.getId());
            product.setTopupType(topupType);
            if (usdCost > 0) {
                product.setName(operator.getName() + " $" + usdCost);
                product.setUsdCost(cost);
            } else {
                product.setName(operator.getName() + " " + bdtAmount + " BDT");
                product.setUsdCost(BigDecimal.ZERO);
                product.setBdtAmount(bdtAmount);
            }
            product.setPriceMode("AUTO");
            product.setLossRate(new BigDecimal("0.03"));
            product.setProfitRate(new BigDecimal("0.15"));
            product.setCnyPrice(priceService.calculateAutoPrice(
                    usdCost > 0 ? cost : BigDecimal.ZERO,
                    operator.getFxRate(), product.getLossRate(), product.getProfitRate()));
            product.setStatus(1);
            product.setReloadlyUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            productMapper.insert(product);
        } else {
            if (operator.getFxRate() != null && "AUTO".equals(existing.getPriceMode()) && usdCost > 0) {
                BigDecimal newPrice = priceService.calculateAutoPrice(cost, operator.getFxRate(),
                        existing.getLossRate(), existing.getProfitRate());
                if (newPrice.compareTo(existing.getCnyPrice()) != 0) {
                    ProductHistory history = new ProductHistory();
                    history.setProductId(existing.getId());
                    history.setUsdCost(existing.getUsdCost());
                    history.setCnyPrice(existing.getCnyPrice());
                    history.setPriceMode(existing.getPriceMode());
                    history.setChangeReason("FX rate update");
                    productHistoryMapper.insert(history);

                    existing.setCnyPrice(newPrice);
                    existing.setReloadlyUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    productMapper.updateById(existing);
                }
            }
        }
    }

    public record SyncResult(int newCount, int updateCount) {
    }
}
