package com.market.controller.admin;

import com.market.common.Result;
import com.market.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 数据统计与分析Controller
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取概览数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> overview = statisticsService.getOverview();
        return Result.success(overview);
    }
    
    /**
     * 获取收入趋势
     */
    @GetMapping("/income")
    public Result<Map<String, Object>> getIncomeTrend() {
        Map<String, Object> trend = statisticsService.getIncomeTrend();
        return Result.success(trend);
    }
    
    /**
     * 获取摊位统计
     */
    @GetMapping("/booth")
    public Result<Map<String, Object>> getBoothStatistics() {
        Map<String, Object> statistics = statisticsService.getBoothStatistics();
        return Result.success(statistics);
    }
    
    /**
     * 获取租户统计
     */
    @GetMapping("/tenant")
    public Result<Map<String, Object>> getTenantStatistics() {
        Map<String, Object> statistics = statisticsService.getTenantStatistics();
        return Result.success(statistics);
    }
}

