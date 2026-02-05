package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁信息实体类
 */
@Data
@TableName("rental")
public class Rental {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long contractId;
    private Long boothId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

