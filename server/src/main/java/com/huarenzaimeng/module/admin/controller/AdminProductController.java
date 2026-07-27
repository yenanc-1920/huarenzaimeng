package com.huarenzaimeng.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.recharge.entity.Product;
import com.huarenzaimeng.module.recharge.entity.ProductHistory;
import com.huarenzaimeng.module.recharge.mapper.ProductHistoryMapper;
import com.huarenzaimeng.module.recharge.mapper.ProductMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductMapper productMapper;
    private final ProductHistoryMapper productHistoryMapper;

    public AdminProductController(ProductMapper productMapper, ProductHistoryMapper productHistoryMapper) {
        this.productMapper = productMapper;
        this.productHistoryMapper = productHistoryMapper;
    }

    @GetMapping
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long operatorId,
            @RequestParam(required = false) String topupType,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (operatorId != null) {
            wrapper.eq(Product::getOperatorId, operatorId);
        }
        if (topupType != null && !topupType.isBlank()) {
            wrapper.eq(Product::getTopupType, topupType);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByAsc(Product::getOperatorId).orderByAsc(Product::getUsdCost);

        Page<Product> result = productMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    @PutMapping("/{id}/price-mode")
    public Result<Void> switchPriceMode(@PathVariable Long id, @RequestBody PriceModeRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.fail(2002, "商品不存在");
        }

        ProductHistory history = new ProductHistory();
        history.setProductId(product.getId());
        history.setUsdCost(product.getUsdCost());
        history.setCnyPrice(product.getCnyPrice());
        history.setPriceMode(product.getPriceMode());
        history.setChangeReason("切换定价模式: " + product.getPriceMode() + " -> " + request.priceMode());
        productHistoryMapper.insert(history);

        product.setPriceMode(request.priceMode());
        if (request.cnyPrice() != null) {
            product.setCnyPrice(request.cnyPrice());
        }
        productMapper.updateById(product);
        return Result.ok(null);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.fail(2002, "商品不存在");
        }
        product.setStatus(request.status());
        productMapper.updateById(product);
        return Result.ok(null);
    }

    @GetMapping("/{id}/history")
    public Result<List<ProductHistory>> history(@PathVariable Long id) {
        List<ProductHistory> list = productHistoryMapper.selectList(new LambdaQueryWrapper<ProductHistory>()
                .eq(ProductHistory::getProductId, id)
                .orderByDesc(ProductHistory::getCreatedAt));
        return Result.ok(list);
    }

    public record PriceModeRequest(String priceMode, java.math.BigDecimal cnyPrice) {
    }

    public record StatusRequest(Integer status) {
    }
}
