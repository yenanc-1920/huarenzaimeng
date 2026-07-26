package com.huarenzaimeng.module.recharge.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huarenzaimeng.common.result.Result;
import com.huarenzaimeng.module.recharge.entity.Operator;
import com.huarenzaimeng.module.recharge.entity.Order;
import com.huarenzaimeng.module.recharge.entity.Product;
import com.huarenzaimeng.module.recharge.mapper.OperatorMapper;
import com.huarenzaimeng.module.recharge.mapper.ProductMapper;
import com.huarenzaimeng.module.recharge.service.MnpService;
import com.huarenzaimeng.module.recharge.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wx")
public class OrderController {

    private final OrderService orderService;
    private final OperatorMapper operatorMapper;
    private final ProductMapper productMapper;
    private final MnpService mnpService;

    public OrderController(OrderService orderService,
                           OperatorMapper operatorMapper,
                           ProductMapper productMapper,
                           MnpService mnpService) {
        this.orderService = orderService;
        this.operatorMapper = operatorMapper;
        this.productMapper = productMapper;
        this.mnpService = mnpService;
    }

    @GetMapping("/operators")
    public Result<List<Operator>> listOperators() {
        List<Operator> operators = operatorMapper.selectList(
                new LambdaQueryWrapper<Operator>()
                        .eq(Operator::getStatus, 1)
                        .orderByAsc(Operator::getSortOrder));
        return Result.ok(operators);
    }

    @GetMapping("/products")
    public Result<List<Product>> listProducts(@RequestParam Long operatorId,
                                              @RequestParam(defaultValue = "AIRTIME") String topupType) {
        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getOperatorId, operatorId)
                        .eq(Product::getTopupType, topupType)
                        .eq(Product::getStatus, 1)
                        .orderByAsc(Product::getUsdCost));
        return Result.ok(products);
    }

    @PostMapping("/order/create")
    public Result<Order> createOrder(@RequestHeader("x-wx-openid") String openid,
                                     @RequestBody Map<String, Object> body) {
        String phone = (String) body.get("phone");
        Long productId = Long.valueOf(body.get("productId").toString());

        MnpService.MnpResult mnpResult = mnpService.verify(phone);

        Order order = orderService.createOrder(openid, phone, productId);
        if (mnpResult.degraded()) {
            order.setMnpVerified(0);
        }
        return Result.ok(order);
    }

    @GetMapping("/order/{orderNo}")
    public Result<Order> getOrder(@RequestHeader("x-wx-openid") String openid,
                                  @PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (order == null || !order.getOpenid().equals(openid)) {
            return Result.fail(3001, "订单不存在");
        }
        return Result.ok(order);
    }

    @GetMapping("/orders")
    public Result<Page<Order>> listOrders(@RequestHeader("x-wx-openid") String openid,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Page<Order> result = orderService.getOrdersByOpenid(openid, page, size);
        return Result.ok(result);
    }
}
