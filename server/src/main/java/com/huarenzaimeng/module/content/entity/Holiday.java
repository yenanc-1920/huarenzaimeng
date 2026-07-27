package com.huarenzaimeng.module.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_holiday")
public class Holiday {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String nameEn;

    private LocalDate holidayDate;

    private String confirmStatus;

    private String description;

    private Integer year;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
