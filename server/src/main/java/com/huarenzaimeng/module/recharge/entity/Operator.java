package com.huarenzaimeng.module.recharge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_operator")
public class Operator {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reloadlyOperatorId;

    private String name;

    private String logoUrl;

    private String country;

    private String phonePrefix;

    private Integer bundle;

    private BigDecimal fxRate;

    private LocalDateTime fxRateUpdatedAt;

    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
