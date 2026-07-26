package com.huarenzaimeng.module.recharge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_refund")
public class Refund {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String refundNo;

    private String orderNo;

    private String openid;

    private BigDecimal refundAmount;

    private String refundType;

    private String refundStatus;

    private String wxRefundId;

    private String reason;

    private Long operatorId;

    private LocalDateTime refundedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
