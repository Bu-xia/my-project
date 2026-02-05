package com.market.dto;

import lombok.Data;

/**
 * 物资入库DTO
 */
@Data
public class InventoryInboundDTO {
    
    private Long inventoryId;
    private Integer quantity;
    private String operator;
    private String reason;
}
