package com.huarenzaimeng.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuditStatus {

    PENDING("待审核"),
    PASS("审核通过"),
    REJECT("审核拒绝");

    private final String description;
}
