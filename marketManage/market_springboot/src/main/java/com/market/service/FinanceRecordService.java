package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.FinanceRecord;

import java.time.LocalDate;
import java.util.Map;

/**
 * 费用收支记录Service接口
 */
public interface FinanceRecordService {
    
    /**
     * 分页查询收支记录（支持多条件筛选）
     */
    Page<FinanceRecord> getRecordPage(int current, int size, Integer recordType, String category, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 添加收支记录
     */
    boolean addRecord(FinanceRecord record);
}

