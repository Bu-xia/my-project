package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 费用收支记录实体类
 */
@Data
@TableName("finance_record")
public class FinanceRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String recordNo;
    private Integer recordType;
    private String category;
    private BigDecimal amount;
    private Long paymentId;
    private String relatedPerson;
    private String description;
    private LocalDate recordDate;
    private LocalDateTime createTime;
}

