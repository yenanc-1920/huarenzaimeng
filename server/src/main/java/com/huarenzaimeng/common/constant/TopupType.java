package com.huarenzaimeng.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TopupType {

    AIRTIME("话费"),
    BUNDLE("流量包");

    private final String description;
}
