package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Rental;
import com.market.entity.vo.RentalVO;

import java.util.List;

/**
 * 租赁信息Service接口
 */
public interface RentalService {
    
    /**
     * 获取当前租赁信息
     */
    List<Rental> getCurrentRental(Long userId);
    
    /**
     * 获取当前租赁信息（包含详细信息）
     */
    List<RentalVO> getCurrentRentalWithDetails(Long userId);
    
    /**
     * 获取历史租赁信息
     */
    Page<Rental> getHistoryRental(int current, int size, Long userId);
    
    /**
     * 获取历史租赁信息（包含详细信息）
     */
    Page<RentalVO> getHistoryRentalWithDetails(int current, int size, Long userId);
    
    /**
     * 根据ID获取租赁详情
     */
    Rental getById(Long id);
    
    /**
     * 根据ID获取租赁详情（包含详细信息）
     */
    RentalVO getRentalDetailById(Long id);
    
    /**
     * 添加租赁信息
     */
    boolean addRental(Rental rental);
}

