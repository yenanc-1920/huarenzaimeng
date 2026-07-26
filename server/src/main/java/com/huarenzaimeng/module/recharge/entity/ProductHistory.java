package com.huarenzaimeng.module.recharge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product_history")
public class ProductHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long productId;

    private BigDecimal usdCost;

    private BigDecimal cnyPrice;

    private String priceMode;

    private String changeReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
