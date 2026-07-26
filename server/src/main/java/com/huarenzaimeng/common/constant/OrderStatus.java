package com.huarenzaimeng.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    PENDING_PAY("待支付"),
    PRICE_CHECKING("价格校验中"),
    MNP_CHECKING("MNP校验中"),
    PENDING_CHARGE("待充值"),
    CHARGING("充值中"),
    SUCCESS("充值成功"),
    FAILED("充值失败"),
    EXCEPTION("订单异常"),
    CANCELLED("已取消"),
    REFUNDED("已退款");

    private final String description;
}
