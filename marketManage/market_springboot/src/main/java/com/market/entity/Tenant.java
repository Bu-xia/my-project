package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户信息实体类
 */
@Data
@TableName("tenant")
public class Tenant {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String businessName;
    private String businessType;
    private String contactPerson;
    private String contactPhone;
    private String businessLicense;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

