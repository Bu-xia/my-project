package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.FinanceRecord;
import com.market.mapper.FinanceRecordMapper;
import com.market.service.FinanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 费用收支记录Service实现类
 */
@Service
public class FinanceRecordServiceImpl implements FinanceRecordService {
    
    @Autowired
    private FinanceRecordMapper financeRecordMapper;
    
    @Override
    public Page<FinanceRecord> getRecordPage(int current, int size, Integer recordType, String category, LocalDate startDate, LocalDate endDate) {
        Page<FinanceRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 按记录类型筛选
        if (recordType != null) {
            wrapper.eq(FinanceRecord::getRecordType, recordType);
        }
        
        // 按分类筛选
        if (StringUtils.hasText(category)) {
            wrapper.like(FinanceRecord::getCategory, category);
        }
        
        // 按日期范围筛选
        if (startDate != null) {
            wrapper.ge(FinanceRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(FinanceRecord::getRecordDate, endDate);
        }
        
        wrapper.orderByDesc(FinanceRecord::getRecordDate);
        return financeRecordMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 计算总收入
        LambdaQueryWrapper<FinanceRecord> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(FinanceRecord::getRecordType, 1);
        List<FinanceRecord> incomeList = financeRecordMapper.selectList(incomeWrapper);
        BigDecimal totalIncome = incomeList.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算总支出
        LambdaQueryWrapper<FinanceRecord> expenseWrapper = new LambdaQueryWrapper<>();
        expenseWrapper.eq(FinanceRecord::getRecordType, 2);
        List<FinanceRecord> expenseList = financeRecordMapper.selectList(expenseWrapper);
        BigDecimal totalExpense = expenseList.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome.subtract(totalExpense));
        
        return result;
    }
    
    @Override
    public boolean addRecord(FinanceRecord record) {
        return financeRecordMapper.insert(record) > 0;
    }
}

