package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Inventory;
import com.market.entity.InventoryRecord;
import com.market.mapper.InventoryMapper;
import com.market.mapper.InventoryRecordMapper;
import com.market.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 物资仓储Service实现类
 */
@Service
public class InventoryServiceImpl implements InventoryService {
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private InventoryRecordMapper inventoryRecordMapper;
    
    @Override
    public Page<Inventory> getInventoryPage(int current, int size, String keyword) {
        return getInventoryPage(current, size, keyword, null);
    }
    
    @Override
    public Page<Inventory> getInventoryPage(int current, int size, String keyword, String category) {
        Page<Inventory> page = new Page<>(current, size);
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        // 关键词筛选（物资编号或名称）
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Inventory::getItemNo, keyword)
                              .or().like(Inventory::getItemName, keyword));
        }
        
        // 分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.eq(Inventory::getCategory, category);
        }
        
        wrapper.orderByDesc(Inventory::getCreateTime);
        return inventoryMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Inventory getById(Long id) {
        return inventoryMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public boolean inbound(Long id, Integer quantity, String operator, String reason) {
        // 更新库存
        Inventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            return false;
        }
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryMapper.updateById(inventory);
        
        // 添加入库记录
        InventoryRecord record = new InventoryRecord();
        record.setInventoryId(id);
        record.setRecordType(1); // 入库
        record.setQuantity(quantity);
        record.setOperator(operator);
        record.setReason(reason);
        record.setRecordTime(LocalDateTime.now());
        inventoryRecordMapper.insert(record);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean outbound(Long id, Integer quantity, String operator, String reason) {
        // 更新库存
        Inventory inventory = inventoryMapper.selectById(id);
        if (inventory == null || inventory.getQuantity() < quantity) {
            return false;
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryMapper.updateById(inventory);
        
        // 添加出库记录
        InventoryRecord record = new InventoryRecord();
        record.setInventoryId(id);
        record.setRecordType(2); // 出库
        record.setQuantity(quantity);
        record.setOperator(operator);
        record.setReason(reason);
        record.setRecordTime(LocalDateTime.now());
        inventoryRecordMapper.insert(record);
        
        return true;
    }
    
    @Override
    public List<Inventory> getWarningList() {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.apply("quantity <= warning_quantity");
        return inventoryMapper.selectList(wrapper);
    }
    
    @Override
    public boolean addInventory(Inventory inventory) {
        return inventoryMapper.insert(inventory) > 0;
    }
    
    @Override
    public boolean updateInventory(Inventory inventory) {
        return inventoryMapper.updateById(inventory) > 0;
    }
    
    @Override
    public List<InventoryRecord> getInventoryRecords(Long inventoryId) {
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryRecord::getInventoryId, inventoryId)
               .orderByDesc(InventoryRecord::getRecordTime);
        return inventoryRecordMapper.selectList(wrapper);
    }
}

