package com.huarenzaimeng.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HolidayConfirmStatus {

    CONFIRMED("已确认"),
    PENDING_MOON("待观月确认");

    private final String description;
}
