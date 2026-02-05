package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 物资仓储实体类
 */
@Data
@TableName("inventory")
public class Inventory {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemNo;
    private String itemName;
    private String category;
    private String unit;
    private Integer quantity;
    private Integer warningQuantity;
    private String itemImage;
    private String storageLocation;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

