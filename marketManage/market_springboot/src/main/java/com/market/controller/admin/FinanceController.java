package com.market.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.common.PageResult;
import com.market.common.Result;
import com.market.entity.FinanceRecord;
import com.market.service.FinanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 费用收支管理Controller
 */
@RestController
@RequestMapping("/api/admin/finance")
public class FinanceController {
    
    @Autowired
    private FinanceRecordService financeRecordService;
    
    /**
     * 获取收支记录列表（分页，支持多条件筛选）
     */
    @GetMapping("/list")
    public Result<PageResult<FinanceRecord>> getFinanceList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer recordType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<FinanceRecord> page = financeRecordService.getRecordPage(current, size, recordType, category, startDate, endDate);
        PageResult<FinanceRecord> result = new PageResult<>(
                page.getTotal(), 
                page.getRecords(), 
                page.getCurrent(), 
                page.getSize()
        );
        return Result.success(result);
    }
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = financeRecordService.getStatistics();
        return Result.success(statistics);
    }
    
}

