package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物资出入库记录实体类
 */
@Data
@TableName("inventory_record")
public class InventoryRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inventoryId;
    private Integer recordType;
    private Integer quantity;
    private String operator;
    private String reason;
    private LocalDateTime recordTime;
}

