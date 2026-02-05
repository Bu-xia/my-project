package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Inventory;

import java.util.List;

/**
 * 物资仓储Service接口
 */
public interface InventoryService {
    
    /**
     * 分页查询物资列表
     */
    Page<Inventory> getInventoryPage(int current, int size, String keyword);
    
    /**
     * 分页查询物资列表（带分类筛选）
     */
    Page<Inventory> getInventoryPage(int current, int size, String keyword, String category);
    
    /**
     * 根据ID获取物资
     */
    Inventory getById(Long id);
    
    /**
     * 物资入库
     */
    boolean inbound(Long id, Integer quantity, String operator, String reason);
    
    /**
     * 物资出库
     */
    boolean outbound(Long id, Integer quantity, String operator, String reason);
    
    /**
     * 获取库存预警列表
     */
    List<Inventory> getWarningList();
    
    /**
     * 添加物资
     */
    boolean addInventory(Inventory inventory);
    
    /**
     * 更新物资
     */
    boolean updateInventory(Inventory inventory);
    
    /**
     * 获取指定物资的出入库记录
     */
    java.util.List<com.market.entity.InventoryRecord> getInventoryRecords(Long inventoryId);
}

