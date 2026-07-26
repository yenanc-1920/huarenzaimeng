package com.huarenzaimeng.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RefundStatus {

    PENDING("待退款"),
    PROCESSING("退款中"),
    SUCCESS("退款成功"),
    FAILED("退款失败"),
    PRE_FUNDED_LOSS("垫资坏账");

    private final String description;
}
