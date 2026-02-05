package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁合同实体类
 */
@Data
@TableName("contract")
public class Contract {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String contractNo;
    private Long tenantId;
    private Long boothId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private BigDecimal deposit;
    private Integer status;
    private String contractFile;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

