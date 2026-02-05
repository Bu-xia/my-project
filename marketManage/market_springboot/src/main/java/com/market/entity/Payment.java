package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@TableName("payment")
public class Payment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long contractId;
    private String paymentNo;
    private Integer paymentType;
    private BigDecimal amount;
    private Integer status;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    private String receiptUrl;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

