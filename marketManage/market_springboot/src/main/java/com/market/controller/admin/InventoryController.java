package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.dto.InventoryInboundDTO;
import com.market.dto.InventoryOutboundDTO;
import com.market.entity.Inventory;
import com.market.entity.InventoryRecord;
import com.market.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物资仓储管理Controller
 */
@RestController
@RequestMapping("/api/admin/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    /**
     * 获取物资列表（分页）
     */
    @GetMapping("/list")
    public Result<PageResult<Inventory>> getInventoryList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        Page<Inventory> page = inventoryService.getInventoryPage(current, size, keyword, category);
        PageResult<Inventory> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 物资入库
     */
    @PostMapping("/inbound")
    public Result<String> inbound(@RequestBody InventoryInboundDTO dto) {
        try {
            if (dto.getInventoryId() == null) {
                return Result.error("物资ID不能为空");
            }
            if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                return Result.error("入库数量必须大于0");
            }
            if (dto.getOperator() == null || dto.getOperator().trim().isEmpty()) {
                return Result.error("操作人不能为空");
            }
            
            if (inventoryService.inbound(dto.getInventoryId(), dto.getQuantity(), dto.getOperator(), dto.getReason())) {
                return Result.success("入库成功");
            }
            return Result.error("入库失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("入库失败：" + e.getMessage());
        }
    }
    
    /**
     * 物资出库
     */
    @PostMapping("/outbound")
    public Result<String> outbound(@RequestBody InventoryOutboundDTO dto) {
        try {
            if (dto.getInventoryId() == null) {
                return Result.error("物资ID不能为空");
            }
            if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
                return Result.error("出库数量必须大于0");
            }
            if (dto.getOperator() == null || dto.getOperator().trim().isEmpty()) {
                return Result.error("操作人不能为空");
            }
            
            if (inventoryService.outbound(dto.getInventoryId(), dto.getQuantity(), dto.getOperator(), dto.getReason())) {
                return Result.success("出库成功");
            }
            return Result.error("出库失败，可能是库存不足或物资不存在");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("出库失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取库存预警列表
     */
    @GetMapping("/warning")
    public Result<List<Inventory>> getWarningList() {
        List<Inventory> list = inventoryService.getWarningList();
        return Result.success(list);
    }
    
    /**
     * 获取指定物资的出入库记录
     */
    @GetMapping("/records/{inventoryId}")
    public Result<List<InventoryRecord>> getInventoryRecords(@PathVariable Long inventoryId) {
        try {
            List<InventoryRecord> records = inventoryService.getInventoryRecords(inventoryId);
            return Result.success(records);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取记录失败：" + e.getMessage());
        }
    }
}

