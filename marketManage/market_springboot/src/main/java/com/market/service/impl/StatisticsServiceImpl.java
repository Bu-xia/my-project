package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.market.entity.*;
import com.market.mapper.*;
import com.market.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计Service实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Autowired
    private BoothMapper boothMapper;
    
    @Autowired
    private ContractMapper contractMapper;
    
    @Autowired
    private FinanceRecordMapper financeRecordMapper;
    
    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 用户总数
        Long userCount = userMapper.selectCount(null);
        result.put("userCount", userCount);
        
        // 租户总数
        Long tenantCount = tenantMapper.selectCount(null);
        result.put("tenantCount", tenantCount);
        
        // 摊位总数
        Long boothCount = boothMapper.selectCount(null);
        result.put("boothCount", boothCount);
        
        // 已租摊位数
        LambdaQueryWrapper<Booth> rentedWrapper = new LambdaQueryWrapper<>();
        rentedWrapper.eq(Booth::getStatus, 1);
        Long rentedBoothCount = boothMapper.selectCount(rentedWrapper);
        result.put("rentedBoothCount", rentedBoothCount);
        
        // 合同总数 - 只统计有效合同
        LambdaQueryWrapper<Contract> contractWrapper = new LambdaQueryWrapper<>();
        contractWrapper.eq(Contract::getStatus, 1);
        Long contractCount = contractMapper.selectCount(contractWrapper);
        result.put("contractCount", contractCount);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getIncomeTrend() {
        Map<String, Object> result = new HashMap<>();
        
        // 总收入
        LambdaQueryWrapper<FinanceRecord> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(FinanceRecord::getRecordType, 1);
        List<FinanceRecord> incomeList = financeRecordMapper.selectList(incomeWrapper);
        BigDecimal totalIncome = incomeList.stream()
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        result.put("totalIncome", totalIncome);
        result.put("incomeRecords", incomeList);
        
        return result;
    }
    
    @Override
    public Map<String, Object> getBoothStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 摊位总数
        Long totalBooth = boothMapper.selectCount(null);
        
        // 可租摊位
        LambdaQueryWrapper<Booth> availableWrapper = new LambdaQueryWrapper<>();
        availableWrapper.eq(Booth::getStatus, 0);
        Long availableBooth = boothMapper.selectCount(availableWrapper);
        
        // 已租摊位
        LambdaQueryWrapper<Booth> rentedWrapper = new LambdaQueryWrapper<>();
        rentedWrapper.eq(Booth::getStatus, 1);
        Long rentedBooth = boothMapper.selectCount(rentedWrapper);
        
        // 维修中摊位
        LambdaQueryWrapper<Booth> repairWrapper = new LambdaQueryWrapper<>();
        repairWrapper.eq(Booth::getStatus, 2);
        Long repairBooth = boothMapper.selectCount(repairWrapper);
        
        result.put("totalBooth", totalBooth);
        result.put("availableBooth", availableBooth);
        result.put("rentedBooth", rentedBooth);
        result.put("repairBooth", repairBooth);
        
        // 计算使用率
        if (totalBooth > 0) {
            double utilizationRate = (rentedBooth * 100.0) / totalBooth;
            result.put("utilizationRate", String.format("%.2f", utilizationRate) + "%");
        } else {
            result.put("utilizationRate", "0%");
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getTenantStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 租户总数
        Long totalTenant = tenantMapper.selectCount(null);
        result.put("totalTenant", totalTenant);
        
        // 正常租户
        LambdaQueryWrapper<Tenant> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Tenant::getStatus, 1);
        Long activeTenant = tenantMapper.selectCount(activeWrapper);
        result.put("activeTenant", activeTenant);
        
        return result;
    }
}

