package com.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 摊位实体类
 */
@Data
@TableName("booth")
public class Booth {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String boothNo;
    private String boothName;
    private String location;
    private BigDecimal area;
    private BigDecimal price;
    private Integer status;
    private String images;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

