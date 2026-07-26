package com.huarenzaimeng.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriceMode {

    AUTO("自动计算"),
    FIXED("固定价格");

    private final String description;
}
