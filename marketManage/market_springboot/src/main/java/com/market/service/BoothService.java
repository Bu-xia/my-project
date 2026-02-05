package com.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.entity.Booth;

import java.util.List;

/**
 * 摊位Service接口
 */
public interface BoothService {
    
    /**
     * 分页查询摊位列表
     */
    Page<Booth> getBoothPage(int current, int size, String keyword, Integer status, String minArea, String maxArea);
    
    /**
     * 根据ID获取摊位
     */
    Booth getById(Long id);
    
    /**
     * 添加摊位
     */
    boolean addBooth(Booth booth);
    
    /**
     * 更新摊位
     */
    boolean updateBooth(Booth booth);
    
    /**
     * 删除摊位
     */
    boolean deleteBooth(Long id);
    
    /**
     * 获取可租摊位列表
     */
    List<Booth> getAvailableBooths();
}

