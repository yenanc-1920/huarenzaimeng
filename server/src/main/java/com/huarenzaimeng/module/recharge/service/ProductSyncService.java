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
import java.util.List;

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
            boolean bundle = opNode.has("bundle") && opNode.get("bundle").asBoolean();
            BigDecimal fxRate = opNode.has("fx") && opNode.get("fx").has("rate")
                    ? new BigDecimal(opNode.get("fx").get("rate").asText()) : null;

            Operator operator = operatorMapper.selectOne(
                    new LambdaQueryWrapper<Operator>().eq(Operator::getReloadlyOperatorId, reloadlyId));

            if (operator == null) {
                operator = new Operator();
                operator.setReloadlyOperatorId(reloadlyId);
                operator.setName(name);
                operator.setCountry("Bangladesh");
                operator.setBundle(bundle ? 1 : 0);
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

            syncProductsForOperator(operator, opNode);
        }

        log.info("Product sync completed: new={}, updated={}", newCount, updateCount);
        return new SyncResult(newCount, updateCount);
    }

    private void syncProductsForOperator(Operator operator, JsonNode opNode) {
        if (!opNode.has("fixedAmounts")) {
            return;
        }

        JsonNode fixedAmounts = opNode.get("fixedAmounts");
        for (JsonNode amountNode : fixedAmounts) {
            double usdAmount = amountNode.asDouble();
            syncSingleProduct(operator, "AIRTIME", usdAmount);
        }

        if (operator.getBundle() == 1 && opNode.has("fixedAmountsDescriptions")) {
            // Bundle products synced via separate data plans if available
        }
    }

    private void syncSingleProduct(Operator operator, String topupType, double usdCost) {
        BigDecimal cost = BigDecimal.valueOf(usdCost);

        Product existing = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getOperatorId, operator.getId())
                .eq(Product::getTopupType, topupType)
                .eq(Product::getUsdCost, cost));

        if (existing == null) {
            Product product = new Product();
            product.setOperatorId(operator.getId());
            product.setTopupType(topupType);
            product.setName(operator.getName() + " " + topupType + " $" + usdCost);
            product.setUsdCost(cost);
            product.setPriceMode("AUTO");
            product.setLossRate(new BigDecimal("0.03"));
            product.setProfitRate(new BigDecimal("0.15"));
            product.setCnyPrice(priceService.calculateAutoPrice(cost, operator.getFxRate(),
                    product.getLossRate(), product.getProfitRate()));
            product.setStatus(1);
            product.setReloadlyUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            productMapper.insert(product);
        } else {
            if (operator.getFxRate() != null && "AUTO".equals(existing.getPriceMode())) {
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
