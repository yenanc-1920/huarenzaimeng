package com.huarenzaimeng.module.recharge.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long operatorId;

    private String topupType;

    private Long reloadlyProductId;

    private String name;

    private BigDecimal usdCost;

    private BigDecimal bdtAmount;

    private BigDecimal cnyPrice;

    private String priceMode;

    private BigDecimal lossRate;

    private BigDecimal profitRate;

    private Integer status;

    private LocalDateTime reloadlyUpdatedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
