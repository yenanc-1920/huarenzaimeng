package com.huarenzaimeng.module.recharge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String openid;

    private String phone;

    private Long operatorId;

    private Long productId;

    private String topupType;

    private BigDecimal usdCost;

    private BigDecimal bdtAmount;

    private BigDecimal cnyPrice;

    private String orderStatus;

    private String statusDesc;

    private Integer mnpVerified;

    private String wxTransactionId;

    private String wxPrepayId;

    private Long reloadlyTransactionId;

    private LocalDateTime paidAt;

    private LocalDateTime chargedAt;

    private Integer retryCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
