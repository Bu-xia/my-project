package com.market.dto;

import lombok.Data;

/**
 * 物资出库DTO
 */
@Data
public class InventoryOutboundDTO {
    
    private Long inventoryId;
    private Integer quantity;
    private String operator;
    private String reason;
}
