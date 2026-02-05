package com.market.service;

import java.util.Map;

/**
 * 数据统计Service接口
 */
public interface StatisticsService {
    
    /**
     * 获取概览数据
     */
    Map<String, Object> getOverview();
    
    /**
     * 获取收入趋势
     */
    Map<String, Object> getIncomeTrend();
    
    /**
     * 获取摊位统计
     */
    Map<String, Object> getBoothStatistics();
    
    /**
     * 获取租户统计
     */
    Map<String, Object> getTenantStatistics();
}

