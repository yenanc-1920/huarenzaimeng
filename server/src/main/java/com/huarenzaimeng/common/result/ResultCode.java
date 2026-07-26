package com.huarenzaimeng.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    LOGIN_FAILED(1002, "登录失败"),

    OPERATOR_NOT_FOUND(2001, "运营商不存在"),
    PRODUCT_NOT_FOUND(2002, "商品不存在"),
    PRODUCT_OFF_SHELF(2003, "商品已下架"),
    PRICE_CHANGED(2004, "价格已变动，请刷新"),
    PRICE_INVERTED(2005, "售价低于成本，暂停销售"),

    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_INVALID(3002, "订单状态不允许此操作"),
    ORDER_DUPLICATE(3003, "订单重复"),
    RISK_FREQUENCY_LIMIT(3004, "操作过于频繁，请稍后再试"),
    RISK_AMOUNT_LIMIT(3005, "超出金额限制"),
    MNP_VERIFY_FAILED(3006, "号码校验失败"),

    PAY_FAILED(4001, "支付失败"),
    REFUND_FAILED(4002, "退款失败"),
    REFUND_CIRCUIT_BREAK(4003, "退款熔断，暂停自动退款"),

    RELOADLY_ERROR(5001, "充值服务异常"),
    RELOADLY_BALANCE_LOW(5002, "充值服务余额不足"),

    CONTENT_AUDIT_PENDING(6001, "内容审核中"),
    CONTENT_AUDIT_REJECTED(6002, "内容审核未通过");

    private final int code;
    private final String message;
}
