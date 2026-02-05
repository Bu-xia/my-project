package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 摊位租赁申请实体类
 */
@Data
@TableName("booth_apply")
public class BoothApply {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long boothId;
    private String applyReason;
    private String businessPlan;
    private Integer leaseMonths;
    private String startDate; // 开始日期
    private String businessType; // 经营类型
    private String contactPhone; // 联系电话
    private String businessDescription; // 经营描述
    private String idCardImages; // 身份证件图片
    private Integer status;
    private String rejectReason;
    private LocalDateTime applyTime;
    private LocalDateTime auditTime;
}

