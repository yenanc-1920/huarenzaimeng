package com.huarenzaimeng.module.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_company")
public class Company {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String category;

    private String description;

    private String logoUrl;

    private String logoAuditStatus;

    private String phone;

    private String wechat;

    private String address;

    private Integer status;

    private Integer sortOrder;

    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
